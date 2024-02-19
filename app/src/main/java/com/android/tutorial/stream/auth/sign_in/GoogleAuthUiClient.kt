package com.android.tutorial.stream.auth.sign_in

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.android.tutorial.stream.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

//main sign in for client
class GoogleAuthUiClient(
    private val context : Context,
    private val onTapClient : SignInClient
) {
    private val auth = Firebase.auth

    //sending a sign in request
    @SuppressLint("SuspiciousIndentation")
    suspend fun signIn() : IntentSender? {
        val result = try {
            onTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        }catch (e : Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
                null
        }
        return result?.pendingIntent?.intentSender
    }

    //gets intent sign in data from firebase and signing in using sended request using upper function Intent Sender credentials
    suspend fun getSigningWithIntentResult(intent : Intent) : SignInEndResult {
        val credential = onTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInEndResult(
                data = user?.run {
                    UserData(
                        userId = uid,
                        name = displayName,
                        profilePictureUrl = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        }catch (e : Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInEndResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun signOut(){
        try {
            onTapClient.signOut()
            auth.signOut()
        }catch (e : Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getSignInUser() : UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            name = displayName,
            profilePictureUrl = photoUrl?.toString()
        )
    }



    //building a sign request using buider
    private fun buildSignInRequest() : BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}