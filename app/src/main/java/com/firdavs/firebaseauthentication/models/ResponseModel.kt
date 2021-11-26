package com.firdavs.firebaseauthentication.models

data class Response(val mode: String, val raw: Raw, val id: String)
data class Raw(val id: String, val phone: String)