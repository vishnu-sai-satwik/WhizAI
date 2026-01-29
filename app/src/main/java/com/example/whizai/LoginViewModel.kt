package com.example.whizai

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class CodeSent(val verificationId : String) : LoginState()
    data class Error(val message: String) : LoginState()
    object Success : LoginState()
}

class LoginViewModel : ViewModel() {
    private val auth = Firebase.auth

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    fun sendOtp(phoneNumber: String, activity: Activity) {
        _loginState.value = LoginState.Loading
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                _loginState.value = LoginState.CodeSent(verificationId)
            }
            override fun onVerificationFailed(e: FirebaseException) {
                Log.w("LoginViewModel", "onVerificationFailed: ${e.message}")
                _loginState.value = LoginState.Error(e.message ?: "Unknown error")
            }
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                Log.d("LoginViewModel", "onVerificationCompleted: $credential")
            }
        }
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(
        verificationId: String,
        otp: String

    ) {
        _loginState.value = LoginState.Loading
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _loginState.value = LoginState.Success
                    } else {
                        _loginState.value = LoginState.Error(task.exception?.message ?: "Unknown error")
                    }
                }

    }
}