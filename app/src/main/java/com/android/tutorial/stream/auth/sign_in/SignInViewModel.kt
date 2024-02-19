package com.android.tutorial.stream.auth.sign_in

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel : ViewModel(){
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSigninResult(result : SignInEndResult){
        _state.update {
            it.copy(
                isSignedIn = result.data != null,
                signInError = result.errorMessage
            )
        }
    }

    fun resetState(){
        _state.update { SignInState() }
    }
}