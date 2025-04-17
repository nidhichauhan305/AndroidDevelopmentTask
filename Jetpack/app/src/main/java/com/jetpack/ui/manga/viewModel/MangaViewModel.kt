package com.jetpack.ui.manga.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.jetpack.networkCall.ApiResponseHandle
import com.jetpack.networkCall.NetworkUtils

class MangaViewModel:ViewModel() {
    private val _mangaResponse by lazy {
        MutableLiveData<ApiResponseHandle<JsonObject>>()
    }

    val mangaResponse: MutableLiveData<ApiResponseHandle<JsonObject>> = _mangaResponse

    fun getMangaList(page:Int,context: Context){
        NetworkUtils.mangaClientCall(context).getMangaData(page,_mangaResponse)
    }
}