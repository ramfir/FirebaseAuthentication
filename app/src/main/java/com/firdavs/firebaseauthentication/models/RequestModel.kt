package com.firdavs.firebaseauthentication.models

data class RequestModel(val mode: String, val raw: PhoneAndId)
data class PhoneAndId(val phone: String, val id: String)