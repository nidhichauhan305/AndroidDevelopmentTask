package com.jetpack.ui.login

sealed class LoginResult {
    data object Success : LoginResult()
    data object IncorrectPassword : LoginResult()
    data object UserNotFound : LoginResult()
}