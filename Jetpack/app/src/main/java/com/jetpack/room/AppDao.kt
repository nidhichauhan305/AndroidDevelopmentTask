package com.jetpack.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jetpack.ui.manga.MangaResponse

@Dao
interface AppDao {

    @Insert
    suspend fun insertUserInfo(userLoginInfoClass: UserLoginInfoClass)

    @Query("SELECT * FROM userLoginCredentials")
    suspend fun getAllUserInfo(): List<UserLoginInfoClass>

    @Insert
    suspend fun insertAllMangas(mangaEntities: List<MangaResponse.Data>)

    @Query("SELECT * FROM manga_table")
    suspend fun getAllMangas(): List<MangaResponse.Data>

    @Query("DELETE FROM manga_table")
    suspend fun clearAllManga()
}