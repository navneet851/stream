package com.android.tutorial.stream.auth.sign_in

data class SignInEndResult(
    val data : UserData?,
    val errorMessage : String?
)

data class UserData(
    val userId : String,
    val name : String?,
    val profilePictureUrl : String?
)
