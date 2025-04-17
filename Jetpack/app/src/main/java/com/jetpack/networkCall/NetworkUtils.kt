package com.jetpack.networkCall

import android.content.Context
import com.jetpack.ui.manga.repository.MangaRepository

object NetworkUtils {

    fun mangaClientCall(context:Context): MangaRepository {
        return MangaRepository(RetrofitHelper.getClient(context).create(ApiCallServices::class.java),context)
    }

}