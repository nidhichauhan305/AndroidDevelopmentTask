package com.jetpack.ui.manga

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.jetpack.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MangaDetail(navController: NavHostController, mangaDetail: MangaResponse.Data) {
    Scaffold(modifier = Modifier
        .statusBarsPadding()
        .navigationBarsPadding(), containerColor = Color.Black) {

        Column(modifier = Modifier.padding(all = 15.dp))  {
            Icon(modifier = Modifier.align(alignment = Alignment.End), painter = painterResource(R.drawable.baseline_star_24), contentDescription = "", tint = Color.White)
            Row(modifier = Modifier.padding(top = 5.dp)){
                Image(
                    painter = rememberAsyncImagePainter(
                        model = mangaDetail.thumb,
                        placeholder = painterResource(id = R.drawable.placeholder)
                    ), contentScale = ContentScale.Crop, contentDescription = "",
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp)


                )

                Column(modifier = Modifier
                    .weight(2f)
                    .padding(start = 10.dp, top = 10.dp), horizontalAlignment = Alignment.Start) {
                    Text(text = mangaDetail.title, color = Color.White, style = TextStyle(fontWeight = FontWeight.W500))
                    Box(modifier = Modifier.height(10.dp))
                    Text(text = mangaDetail.subTitle, color = Color.White)
                }
            }

            Box(modifier = Modifier.padding(top = 10.dp))
            Text(text = mangaDetail.summary, color = Color.White)

        }

    }
}