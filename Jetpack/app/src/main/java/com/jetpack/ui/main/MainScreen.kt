package com.jetpack.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jetpack.navigation.Screen
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jetpack.R
import com.jetpack.ui.faceDetection.FaceDetectionScreen
import com.jetpack.ui.login.LoginScreen
import com.jetpack.ui.manga.MangaDetail
import com.jetpack.ui.manga.MangaList
import com.jetpack.ui.manga.MangaResponse
import com.jetpack.utils.SharedPrefsManager

@Composable
fun MainScreen(navController: NavHostController) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination?.route
    val context = LocalContext.current
    val showBottomBar = currentDestination in listOf(Screen.Manga.route, Screen.FaceDetection.route)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                CustomNavigationBar(navController,currentDestination)
            }
        }
    )
 { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (SharedPrefsManager.getIsLogin(context)){Screen.Manga.route}
            else{
                Screen.Login.route
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) { LoginScreen(navController) }
            composable(Screen.SignUp.route) { LoginScreen(navController) }
            composable(Screen.FaceDetection.route) { FaceDetectionScreen() }
            composable(route = Screen.MangaDetail.route+ "/{mangaDetail}") { backStackEntry ->
                val mangaData = backStackEntry.arguments?.getString("mangaDetail")
                val mangaDetail = Gson().fromJson<MangaResponse.Data>(
                    mangaData,
                    object : TypeToken<MangaResponse.Data>() {}.type
                )
                MangaDetail(navController,mangaDetail)

            }
            composable(Screen.Manga.route) { MangaList(navController) }
        }
    }
}

@Composable
fun CustomNavigationBar(
    navController: NavController,
    currentDestination: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable {
                if (currentDestination!=Screen.Manga.route)
                    navController.navigate(Screen.Manga.route) {
                        popUpTo(Screen.Manga.route) { inclusive = true }
                        launchSingleTop = true
                    }
            }
        ) {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .background(colorResource(id = R.color.grey)).padding(horizontal = 12.dp, vertical = 3.dp)){
                Icon(
                    painter = painterResource(id = R.drawable.manga_icon),
                    contentDescription = null,
                    tint = Color.White
                )
                    }

            Text("Manga", color = Color.White,modifier = Modifier.padding(top = 5.dp), fontSize = 12.sp)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White).padding(horizontal = 10.dp, vertical = 4.dp)
                .clickable {
                    if (currentDestination!=Screen.FaceDetection.route)
                        navController.navigate(Screen.FaceDetection.route) {
                            popUpTo(Screen.Manga.route) { inclusive = false }
                            launchSingleTop = true
                        }
                }
        ) {
            Text(
                "Face",
                fontSize = 18.sp,
                fontWeight = FontWeight.W700,
                color = Color.Black
            )
        }
    }

}
