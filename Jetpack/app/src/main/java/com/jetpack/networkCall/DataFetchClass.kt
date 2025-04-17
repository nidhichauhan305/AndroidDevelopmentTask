package com.jetpack.networkCall

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

abstract class DataFetchClass<ResultType>(private val responseLiveData:MutableLiveData<ApiResponseHandle<ResultType>>, private val context:Context) {
    abstract suspend fun createAsyncCall():Response<ResultType>
    open fun saveResult(resultType: ResultType){}

        fun execute(){
            if (Connectivity.isConnected(context)){
                responseLiveData.postValue(ApiResponseHandle.loading())
                callNetworkData()
            }
            else{
                responseLiveData.postValue(ApiResponseHandle.error(ApiResponseHandle.ApiError(code = 1001,"No internet connection")))
            }
        }

        @OptIn(DelicateCoroutinesApi::class)
        private fun callNetworkData(){
            GlobalScope.launch {
                try {
                    val request = createAsyncCall()
                    if (request.isSuccessful){
                        saveResult(request.body()!!)
                        responseLiveData.postValue(ApiResponseHandle.success(request.body()!!))
                    }
                    else{
                        responseLiveData.postValue(ApiResponseHandle.error(ApiResponseHandle.ApiError(request.code(),request.message(),JSONObject(request.errorBody()!!.charStream().readText()).toString())))
                    }

                }
                catch (e : Exception){
                    responseLiveData.postValue(ApiResponseHandle.error(ApiResponseHandle.ApiError(500,e.message.toString())))

                }
            }
        }

}