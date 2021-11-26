package com.firdavs.firebaseauthentication.models

import com.google.firebase.auth.PhoneAuthProvider

data class FirebaseUser(
    var phone: String,
    var verificationId: String,
    var token: PhoneAuthProvider.ForceResendingToken?)