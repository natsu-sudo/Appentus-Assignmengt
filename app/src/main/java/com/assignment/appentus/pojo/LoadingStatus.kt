package com.assignment.appentus.pojo

enum class ErrorCode{
    NO_DATA,
    NETWORK_ERROR,
    UNKNOWN_ERROR
}

enum class Status{
    LOADING,
    SUCCESS,
    ERROR
}

data class LoadingStatus(val status: Status,val error:ErrorCode?,val message: String?){
    companion object{
        fun loading():LoadingStatus{
            return LoadingStatus(Status.LOADING,null,null)
        }

        fun success(error:ErrorCode?=null,message: String?=null):LoadingStatus{
            return LoadingStatus(Status.SUCCESS,error,message)
        }

        fun error(error:ErrorCode?,message: String?=null):LoadingStatus{
            return LoadingStatus(Status.ERROR,error,message)
        }
    }
}