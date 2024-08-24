package com.gdr.budgetbuddy.userauth

import android.content.Intent
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
import com.gdr.budgetbuddy.main.MainActivity
import com.gdr.budgetbuddy.databinding.ActivityLoginBinding
import com.gdr.budgetbuddy.utils.Constants
import com.gdr.budgetbuddy.utils.Constants.USER_GOOGLE_ID
import com.gdr.budgetbuddy.utils.Constants.USER_GOOGLE_TOKEN
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

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

        // 파이어베이스 객체 초기화
        FirebaseApp.initializeApp(this);
        auth = Firebase.auth
        db = Firebase.firestore

        // 구글 아이디 옵션 설정
        val webClientId = Constants.WEB_CLIENT_ID
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(true)
            .setNonce(null)
            .build()

        // 구글 로그인 리퀘스트 설정
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
                    Log.d(TAG, "credentialManager error!", e)
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

    /**
     * 구글 로그인 핸들링
     *
     * @param result
     */
    private fun handleSignIn(result: GetCredentialResponse) {
        // getCredential = 사용자에게 로그인할 수 있는 계정이 있는지 확인!
        // result는 그 결과임
        // TYPE_GOOGIE_ID_TOKEN_CREDENTIAL 상수는 Google Id 토큰 사용자 인증 정보의 유형을 나타냅니다.

        when (val credential = result.credential) {

            // GoogleIdToken credential
            // CustomCredential = GoogleIdTokenCredential data 결과임
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
                                val userExistenceHandler = object : UserExistenceHandler {
                                    override fun onExistUser() {
                                        // 유저 존재 =>> 메인 화면
                                        Log.d(TAG, "onExistUser")
                                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                    }

                                    override fun onNotExistUser() {
                                        // 유저 존재하지 않음 =>> 동의 화면
                                        Log.d(TAG, "onNotExistUser")
                                        val intent = Intent(this@LoginActivity, PolicyActivity::class.java).apply {
                                            putExtra(USER_GOOGLE_ID, googleIdTokenCredential.id)
                                            putExtra(USER_GOOGLE_TOKEN, googleIdTokenCredential.idToken)
                                        }
                                        startActivity(intent)
                                    }
                                }
                                checkSignUp(googleIdTokenCredential.id, userExistenceHandler)

                                // ============================== 여기부터 회원가입 ==============================
                                // =============================================================================
                                // TODO: 이 위치가 맞나?
                                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                                Log.d(TAG, "firebaseCredential = $firebaseCredential")

                                auth.signInWithCredential(firebaseCredential)
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            //Sign in success, update UI with the signed-in user's information
                                            val user = auth.currentUser
                                            Log.i(TAG, "user = $user")
                                        } else {
                                            Log.e(TAG, "task is not successful ${task.exception}")
                                        }
                                    }
                                // =============================================================================
                            }

                            else -> Log.w(TAG, "idToken is null")
                        }

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

    /**
     * fireStore에 email이 있는지 체크
     *
     * @param userId
     * @param userExistenceHandler
     */
    private fun checkSignUp(userId: String, userExistenceHandler: UserExistenceHandler) {
        Log.i(TAG, "checkSignUp $userId")

        // 비동기 동작
        val docRef = db.collection(Constants.USER_ACCOUNT_COLLECTION).document(userId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    Log.d(TAG, "$userId Info = ${document.data}")
                    userExistenceHandler.onExistUser()
                } else {
                    Log.e(TAG, "No such document. $userId isn't existed.")
                    userExistenceHandler.onNotExistUser()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "get failed with ", exception)
            }
    }

    /**
     * fetchSignInMethodsForEmail =>> deprecated
     */
}