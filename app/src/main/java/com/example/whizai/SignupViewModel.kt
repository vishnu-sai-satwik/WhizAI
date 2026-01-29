package com.example.whizai

import android.app.Activity
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


// STATE: Create a sealed class to represent the different states of the sign-up process.
// This is a robust way to manage UI state.
sealed class SignupState {
    object Idle : SignupState()
    object Loading : SignupState()
    data class CodeSent(val verificationId: String) : SignupState()
    data class Error(val message: String) : SignupState()
    object Success : SignupState()
}



class SignupViewModel : ViewModel() {
    val auth = Firebase.auth

    // STATE: Hold the current state of the sign-up flow.
    private val _signupState = MutableStateFlow<SignupState>(SignupState.Idle)
    val signupState = _signupState.asStateFlow()

    // LOGIC: This function triggers the phone number verification process.
    fun sendOtp(phoneNumber: String, activity: Activity) {
        _signupState.value = SignupState.Loading

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // This is called when the SMS code is successfully sent.
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // We update the state to CodeSent and pass the verificationId,
                // which we'll need to verify the OTP later.
                _signupState.value = SignupState.CodeSent(verificationId)
            }

            // This is called when the verification is failed.
            override fun onVerificationFailed(e: FirebaseException) {
                _signupState.value = SignupState.Error(e.message ?: "Unknown error")
            }

            // This is called for instant verification or if the code has been retrieved automatically.
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // For this flow, we will handle sign-in manually after the user enters the OTP.
                // So we can leave this empty for now or handle auto-retrieval if desired.
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(verificationId: String, otp : String) {
        _signupState.value = SignupState.Loading
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signupState.value = SignupState.Success
                } else {
                    _signupState.value = SignupState.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }
}