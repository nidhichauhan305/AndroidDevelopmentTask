package com.jetpack.navigation

sealed class Screen(val route:String) {
    data object Login : Screen("login")
    data object SignUp : Screen("signUp")
    data object Manga : Screen("manga")
    data object MangaDetail : Screen("mangaDetail")
    data object FaceDetection : Screen("faceDetection")
}