package com.jetpack.ui.manga

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.gson.Gson
import com.jetpack.R
import com.jetpack.navigation.Screen
import com.jetpack.networkCall.ApiResponseHandle
import com.jetpack.networkCall.Connectivity
import com.jetpack.room.UserDatabase
import com.jetpack.ui.login.UserAuthViewModelFactory
import com.jetpack.room.repository.UserRepository
import com.jetpack.room.viewModel.UserViewModel
import com.jetpack.utils.progressBar
import com.jetpack.ui.manga.viewModel.MangaViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MangaList(navController: NavHostController) {
    val isLoading = remember { mutableStateOf(false) }
    val isRefreshing = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val viewModel: MangaViewModel = viewModel()

    val mangaState = viewModel.mangaResponse.observeAsState()
    val mangaList = remember {
        mutableStateOf<List<MangaResponse.Data>>(emptyList())
    }
    val db = remember { UserDatabase.getInstance(context) }

    val repository = UserRepository(db.appDao())
    val factory = UserAuthViewModelFactory(repository)
    val userAuthViewModel: UserViewModel = viewModel(factory = factory)
    val listState = rememberLazyGridState()
    val currentPage = remember { mutableIntStateOf(1) }
    val endReached = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (mangaList.value.isEmpty()) {
            if (Connectivity.isConnected(context)) {

                isLoading.value = true
                viewModel.getMangaList(1, context)

            } else {
                val offlineList = userAuthViewModel.getMangaData()
                mangaList.value = offlineList
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { index ->
                if (index != null && index >= mangaList.value.size - 1 &&
                    !isLoading.value && !endReached.value && !isRefreshing.value) {
                    currentPage.value += 1
                    isLoading.value = true
                    viewModel.getMangaList(currentPage.value, context)
                }
            }
    }
    when (mangaState.value?.status) {
        ApiResponseHandle.Status.SUCCESS -> {
            isLoading.value = false
            isRefreshing.value = false
            val response = Gson().fromJson(mangaState.value!!.data, MangaResponse::class.java)
            if (response.data.isNotEmpty()) {
                LaunchedEffect(response) {
                    if (currentPage.value == 1) {
                        userAuthViewModel.clearMangaList()
                        userAuthViewModel.addMangaData(response.data)
                        mangaList.value = response.data
                    } else {
                        userAuthViewModel.addMangaData(response.data)
                        mangaList.value += response.data
                    }
                }
            } else {
                endReached.value = true
            }
        }
        ApiResponseHandle.Status.ERROR -> {
            isLoading.value = false
            isRefreshing.value = false
        }
        else -> {}
    }

    Scaffold(modifier = Modifier
        .statusBarsPadding()
        .navigationBarsPadding(), containerColor = Color.Black) {

            SwipeRefresh(
                state = SwipeRefreshState(isRefreshing.value),
                onRefresh = {
                    isRefreshing.value = true
                    currentPage.value = 1
                    endReached.value = false
                    viewModel.getMangaList(1, context)
                }
            ) {
                Box(modifier = Modifier.padding(top = 10.dp, start = 10.dp)) {
                    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                        items(mangaList.value.size) { index ->

                            Box(
                                modifier = Modifier
                                    .fillMaxSize().padding(end = 10.dp, bottom = 10.dp)
                                    .clickable {
                                        val json = Uri.encode(Gson().toJson(mangaList.value[index]))
                                        navController.navigate(Screen.MangaDetail.route + "/$json")
                                    }.clip(shape = RoundedCornerShape(12.dp)).border(
                                        border = BorderStroke(
                                            width = 1.dp,
                                            colorResource(
                                                id = R.color.grey
                                            )
                                        )
                                    ).background(
                                        colorResource(
                                            id = R.color.grey
                                        )
                                    ),
                            )
                            {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model =  ImageRequest.Builder(context)
                                            .data(mangaList.value[index].thumb)
                                            .diskCachePolicy(CachePolicy.ENABLED)
                                            .build(),
                                        placeholder = painterResource(id = R.drawable.placeholder)
                                    ), contentScale = ContentScale.Crop, contentDescription = "",
                                    modifier = Modifier
                                        .padding(1.dp)
                                        .clip(shape = RoundedCornerShape(12.dp))
                                        .height(150.dp)
                                        .fillMaxWidth()

                                )
                            }

                        }

                        if (isLoading.value && !isRefreshing.value) {
                            item(span = { GridItemSpan(3) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    progressBar()
                                }
                            }
                        }

                    }
                }
            }


    }
}