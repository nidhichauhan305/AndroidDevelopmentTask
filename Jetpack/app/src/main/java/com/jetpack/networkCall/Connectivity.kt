package com.jetpack.networkCall

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.TransportInfo

object Connectivity {
    fun isConnected(context: Context):Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork?:return false
        val activeNw = connectivityManager.getNetworkCapabilities(networkCapabilities)?:return false

        return when
        {
           activeNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
           activeNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
           activeNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}