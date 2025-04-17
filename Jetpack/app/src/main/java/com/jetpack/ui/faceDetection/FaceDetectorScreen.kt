package com.jetpack.ui.faceDetection

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.RectF
import android.graphics.YuvImage
import android.media.Image
import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import com.jetpack.R
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

@Composable
fun FaceDetectionScreen() {
    val context = LocalContext.current
    val cameraPermission = android.Manifest.permission.CAMERA
    val permissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, cameraPermission) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> permissionGranted.value = granted }
    )

    LaunchedEffect(Unit) {
        if (!permissionGranted.value) {
            launcher.launch(cameraPermission)
        }
    }

    if (permissionGranted.value) {
        FaceDetectorCameraView()
    } else {
        Text("Camera permission is required to use this feature.")
    }
}

@Composable
fun FaceDetectorCameraView() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    var isFaceDetected by remember { mutableStateOf(false) }
    var faceDetector by remember { mutableStateOf<FaceDetector?>(null) }
    var boundingBox by remember { mutableStateOf<RectF?>(null) } // Store the bounding box



    LaunchedEffect(Unit) {
        try {
            faceDetector = FaceDetector.createFromOptions(
                context,
                FaceDetector.FaceDetectorOptions.builder()
                    .setMinDetectionConfidence(0.5f)
                    .setRunningMode(RunningMode.LIVE_STREAM)
                    .build()
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    DisposableEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                        faceDetector?.let { detector ->
                            processImageProxy(imageProxy, detector, { faceFound ->
                                isFaceDetected = faceFound
                            }, { detectedBoundingBox ->
                                boundingBox = detectedBoundingBox // Update bounding box state
                            })
                        } ?: imageProxy.close()
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            faceDetector?.close()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        Box(modifier = Modifier
            .align(Alignment.Center)
            .border(
                BorderStroke(
                    width = 2.dp,
                    color = colorResource(R.color.text_color)
                )
            )
            .size(250.dp)){


            Box(
                modifier = Modifier
                    .align(Alignment.Center).padding(bottom = 50.dp)
                    .border(
                        BorderStroke(
                            width = 2.dp,
                            color = if (isFaceDetected) colorResource(R.color.green) else colorResource(R.color.red)
                        )
                    )
                    .size(100.dp)
            )

        }
    }
}

@OptIn(ExperimentalGetImage::class)
fun processImageProxy(
    imageProxy: ImageProxy,
    faceDetector: FaceDetector,
    onResult: (Boolean) -> Unit,
    updateBoundingBox: (RectF) -> Unit // Add this parameter to update bounding box
) {
    try {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val bitmap = mediaImage.toBitmap() ?: run {
            imageProxy.close()
            return
        }

        val image = BitmapImageBuilder(bitmap).build()

        // Detect faces
        val result = faceDetector.detect(image)
        val detections = result.detections()

        if (detections.isNotEmpty()) {
            // If faces are detected, process the bounding box
            detections.forEach { detection ->
                val boundingBox = detection.boundingBox()
                // Convert the normalized bounding box to screen coordinates
                val screenBoundingBox = getScreenCoordinates(boundingBox, bitmap.width, bitmap.height)
                updateBoundingBox(screenBoundingBox) // Update the bounding box UI state
            }
            onResult(true)
        } else {
            onResult(false)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        onResult(false)
    } finally {
        imageProxy.close()
    }
}

// Helper function to convert normalized bounding box to screen coordinates
fun getScreenCoordinates(boundingBox: RectF, imageWidth: Int, imageHeight: Int): RectF {
    val scaleX = imageWidth.toFloat() / boundingBox.width()
    val scaleY = imageHeight.toFloat() / boundingBox.height()

    val left = boundingBox.left * scaleX
    val top = boundingBox.top * scaleY
    val right = boundingBox.right * scaleX
    val bottom = boundingBox.bottom * scaleY

    return RectF(left, top, right, bottom)
}

fun Image.toBitmap(): Bitmap? {
    try {
        if (format != ImageFormat.YUV_420_888) return null

        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(android.graphics.Rect(0, 0, width, height), 90, out)

        val yuv = out.toByteArray()
        return BitmapFactory.decodeByteArray(yuv, 0, yuv.size)

    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}


