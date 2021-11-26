package com.firdavs.firebaseauthentication.api

import com.firdavs.firebaseauthentication.models.RequestModel
import com.firdavs.firebaseauthentication.models.Response
import io.reactivex.Observable
import retrofit2.http.*

interface PostmanAPI {

    @Headers("Content-Type: application/json")
    @POST("checkUser")
    fun checkUser(@Body requestModel: RequestModel): Observable<retrofit2.Response<Response>>
}