package com.firdavs.firebaseauthentication.repository

import com.firdavs.firebaseauthentication.api.RetrofitInstance
import com.firdavs.firebaseauthentication.models.PhoneAndId
import com.firdavs.firebaseauthentication.models.RequestModel
import com.firdavs.firebaseauthentication.models.Response
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class Repository {

    fun checkUser(phone: String, id: String): Observable<retrofit2.Response<Response>> {
        val requestModel = RequestModel("raw", PhoneAndId("+79259051896", "ura"))
        return RetrofitInstance.api.checkUser(requestModel).subscribeOn(Schedulers.io())
    }
}