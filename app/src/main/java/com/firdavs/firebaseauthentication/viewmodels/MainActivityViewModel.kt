package com.firdavs.firebaseauthentication.viewmodels

import android.app.Activity
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firdavs.firebaseauthentication.models.FirebaseUser
import com.firdavs.firebaseauthentication.models.Response
import com.firdavs.firebaseauthentication.repository.Repository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class MainActivityViewModel: ViewModel() {

    val firebaseUser = FirebaseUser("", "", null)

    private lateinit var timer: CountDownTimer
    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long> = _currentTime
    private val _isTimerFinished = MutableLiveData(false)
    val isTimerFinished: LiveData<Boolean> = _isTimerFinished
    var isTimerStarted = false

    private var repository = Repository()
    private val _response = MutableLiveData<retrofit2.Response<Response>>()
    val response: LiveData<retrofit2.Response<Response>> = _response

    private val disposables = CompositeDisposable()

    private val _hideProgressBar = MutableLiveData(false)
    val hideProgressBar: LiveData<Boolean> = _hideProgressBar

    val firebaseAuth = FirebaseAuth.getInstance()
    private val _verificationFailed = MutableLiveData<FirebaseException>()
    val verificationFailed: LiveData<FirebaseException> = _verificationFailed
    private val _codeSent = MutableLiveData(false)
    val codeSent: LiveData<Boolean> = _codeSent
    fun resetCodeSent() {
        _codeSent.value = false
    }
    val mCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }
            override fun onVerificationFailed(e: FirebaseException) {
                _verificationFailed.value = e
            }
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                _codeSent.value = true
                firebaseUser.token = token
                firebaseUser.verificationId = verificationId
            }
        }
    private val _addOnSuccessListener = MutableLiveData(false)
    val addOnSuccessListener: LiveData<Boolean> = _addOnSuccessListener
    private val _addOnFailureListener = MutableLiveData<Exception>()
    val addOnFailureListener: LiveData<Exception> = _addOnFailureListener
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                _addOnSuccessListener.value = true
            }
            .addOnFailureListener {
                _addOnFailureListener.value = it
            }
    }
    fun startPhoneNumberVerification(phone: String, activity: Activity) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(mCallBacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }
    fun resendVerificationCode(phone: String, token: PhoneAuthProvider.ForceResendingToken?, activity: Activity) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(mCallBacks)
            .setForceResendingToken(token)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun startTimer() {
        if (!isTimerStarted) {
            _isTimerFinished.value = false
            timer = object : CountDownTimer(120000L, 1000L) {
                override fun onTick(millisUntilFinished: Long) {
                    _currentTime.value = millisUntilFinished
                    isTimerStarted = true
                }

                override fun onFinish() {
                    _isTimerFinished.value = true
                }
            }.start()
        }
    }

    fun checkUser(phone: String, id: String) {
        disposables.add(repository.checkUser(phone, id)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                _hideProgressBar.value = false
            }
            .doOnError {
                _hideProgressBar.value = true
            }
            .doOnComplete {
                _hideProgressBar.value = true
            }
            .subscribe(
                {
                    _response.value = it
                },
                {
                    Log.d("TAG", "${it.message}")
                }
            )
        )
    }

    override fun onCleared() {
        disposables.clear()
    }
}