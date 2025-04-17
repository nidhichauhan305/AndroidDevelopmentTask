package com.jetpack.networkCall

import com.google.gson.JsonObject
import com.jetpack.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiCallServices {

    @GET(Constants.MANGA_DATA)
    suspend fun getMangaList(
        @Query ("page") page:Int, //1
    ):Response<JsonObject>

}