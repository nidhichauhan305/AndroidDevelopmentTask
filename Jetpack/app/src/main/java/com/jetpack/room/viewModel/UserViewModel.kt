package com.jetpack.room.viewModel

import androidx.lifecycle.ViewModel
import com.jetpack.room.UserLoginInfoClass
import com.jetpack.ui.login.LoginResult
import com.jetpack.room.repository.UserRepository
import com.jetpack.ui.manga.MangaResponse

class UserViewModel(private val userAuthRepository: UserRepository) : ViewModel() {

    suspend fun getMangaData(): List<MangaResponse.Data> {
        return userAuthRepository.getMangaList()
    }
     fun clearMangaList() {
        userAuthRepository.clearMangaData()
    }

    fun addMangaData(mangaResponse: List<MangaResponse.Data>) {
        userAuthRepository.addMangaList(mangaResponse)
    }

    fun signUp(newUser: UserLoginInfoClass, onResult: (LoginResult) -> Unit) {
        userAuthRepository.addUser(newUser, onResult)
    }

    fun login(email: String, password: String, onResult: (LoginResult) -> Unit) {
        userAuthRepository.checkUserCredentials(email, password, onResult)
    }
}