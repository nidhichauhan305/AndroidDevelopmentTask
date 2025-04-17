package com.jetpack.networkCall

class ApiResponseHandle<T>(val status:Status,val data:T?,val error:ApiError?) {
    companion object{
        fun <T> loading():ApiResponseHandle<T>{
            return ApiResponseHandle(Status.LOADING,null,null)
        }

        fun <T> success(data:T):ApiResponseHandle<T>{
            return ApiResponseHandle(Status.SUCCESS,data,null)
        }

        fun <T> error(error:ApiError):ApiResponseHandle<T>{
            return ApiResponseHandle(Status.ERROR,null,error)
        }

    }

    enum class Status{
        LOADING,SUCCESS,ERROR
    }

    class ApiError(val code:Int,val message:String,val errorBody:String="")
}