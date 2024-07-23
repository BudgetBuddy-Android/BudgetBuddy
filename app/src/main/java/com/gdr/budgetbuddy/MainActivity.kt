package com.gdr.budgetbuddy

import android.credentials.GetCredentialException
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.gdr.budgetbuddy.databinding.ActivityMainBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val TAG = "GoogleLoginTest"
    }

    @RequiresApi(34)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webClientId = "blank"
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(true)
            .setNonce(null)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(this)

        binding.btnLogin.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = this@MainActivity,
                    )
                    handleSignIn(result)
                } catch (e: GetCredentialException) {
                    Log.d(TAG, "error!")
                }
            }
        }
    }

    // getCredential = 사용자에게 로그인할 수 있는 계정이 있는지 확인!
    // result는 그 결과임
    // TYPE_GOOGIE_ID_TOKEN_CREDENTIAL 상수는 Google Id 토큰 사용자 인증 정보의 유형을 나타냅니다.
    private fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        val credential = result.credential

        when (credential) {

//            // Passkey credential
//            is PublicKeyCredential -> {
//                // Share responseJson such as a GetCredentialResponse on your server to
//                // validate and authenticate
//                responseJson = credential.authenticationResponseJson
//            }
//
//            // Password credential
//            is PasswordCredential -> {
//                // Send ID and password to your server to validate and authenticate.
//                val username = credential.id
//                val password = credential.password
//            }

            // CustomCredential = GoogleIdTokenCredential data 결과임

            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }
}