package com.android.tutorial.stream.auth.sign_in

data class SignInState(
    val isSignedIn : Boolean = false,
    val signInError : String? = null
)
