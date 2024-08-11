package com.gdr.budgetbuddy.userauth

import android.credentials.GetCredentialException
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.gdr.budgetbuddy.databinding.ActivityLoginBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    companion object {
        const val TAG = "GoogleLoginTest"
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    @RequiresApi(34)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this);
        auth = Firebase.auth

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


        binding.loginBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = this@LoginActivity,
                    )
                    handleSignIn(result)
                } catch (e: GetCredentialException) {
                    Log.d(TAG, "error!")
                }
            }
        }

        /** 알람 런타임 권한 요청 */
        //askNotificationPermission()
    }

    /*    private fun askNotificationPermission() {
            // This is only necessary for API level >= 33 (TIRAMISU)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    // FCM SDK (and your app) can post notifications.
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    // TODO: display an educational UI explaining to the user the features that will be enabled
                    //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                    //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                    //       If the user selects "No thanks," allow the user to continue without notifications.
                } else {
                    // Directly ask for the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }*/

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

                        val idToken = googleIdTokenCredential.idToken
                        when {
                            idToken != null -> {
                                // TODO mail 주소가 FirebaseUser 목록에 있는지 확인 후 없으면 화면 이동, 있으면 HomeActivity로 이동
                                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                                auth.signInWithCredential(firebaseCredential)
                                    .addOnCompleteListener(this) { task ->

                                        if (task.isSuccessful) {
                                            //Sign in success, update UI with the signed-in user's information
                                            val user = auth.currentUser
                                        } else {
                                            Log.e(TAG, "task is not successful ${task.exception}")
                                        }
                                    }
                            }

                            else -> Log.w(TAG, "idToken is null")
                        }

//                        val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
//                        val idToken = googleCredential.googleIdToken
//                        when {
//                            idToken != null -> {
//                                // Got an ID token from Google. Use it to authenticate
//                                // with Firebase.
//                                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
//                                auth.signInWithCredential(firebaseCredential)
//                                    .addOnCompleteListener(this) { task ->
//                                        if (task.isSuccessful) {
//                                            // Sign in success, update UI with the signed-in user's information
//                                            Log.d(TAG, "signInWithCredential:success")
//                                            val user = auth.currentUser
//                                            updateUI(user)
//                                        } else {
//                                            // If sign in fails, display a message to the user.
//                                            Log.w(TAG, "signInWithCredential:failure", task.exception)
//                                            updateUI(null)
//                                        }
//                                    }
//                            }
//                            else -> {
//                                // Shouldn't happen.
//                                Log.d(TAG, "No ID token!")
//                            }
//                        }

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