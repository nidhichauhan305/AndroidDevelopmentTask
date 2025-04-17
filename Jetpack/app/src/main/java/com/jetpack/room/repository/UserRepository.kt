package com.jetpack.room.repository

import com.jetpack.room.AppDao
import com.jetpack.room.UserLoginInfoClass
import com.jetpack.ui.login.LoginResult
import com.jetpack.ui.manga.MangaResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserRepository(private val appDao: AppDao) {


    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun addUser(newUser: UserLoginInfoClass, onResult: (LoginResult) -> Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            appDao.insertUserInfo(newUser)
            onResult(LoginResult.Success)
        }
    }

    fun checkUserCredentials(email: String, password: String, onResult: (LoginResult) -> Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val users = appDao.getAllUserInfo()
                val user = users.find { it.email == email }

                if (user == null) {
                    onResult(LoginResult.UserNotFound)
                } else if (user.password != password) {
                    onResult(LoginResult.IncorrectPassword)
                } else {
                    onResult(LoginResult.Success)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addMangaList(mangaList: List<MangaResponse.Data>) {
        coroutineScope.launch(Dispatchers.IO) {
            appDao.insertAllMangas(mangaList)
        }
    }

    suspend fun getMangaList(): List<MangaResponse.Data> {
        return appDao.getAllMangas()
    }

     fun clearMangaData() {
         coroutineScope.launch(Dispatchers.IO) {
             appDao.clearAllManga()
         }
    }
}
