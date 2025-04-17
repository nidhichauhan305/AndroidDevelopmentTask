package com.jetpack.ui.manga.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.jetpack.networkCall.ApiCallServices
import com.jetpack.networkCall.ApiResponseHandle
import com.jetpack.networkCall.DataFetchClass
import retrofit2.Response

class MangaRepository(val apiCallServices: ApiCallServices, val context: Context) {

    fun getMangaData(page:Int,mangaResponse:MutableLiveData<ApiResponseHandle<JsonObject>>){
        object : DataFetchClass<JsonObject>(mangaResponse,context) {
            override suspend fun createAsyncCall(): Response<JsonObject> {
                return apiCallServices.getMangaList(page)
            }
        }.execute()
    }

}