package com.gdr.budgetbuddy.userauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.gdr.budgetbuddy.databinding.ActivityPolicyBinding
import com.gdr.budgetbuddy.utils.CommonUtil
import com.gdr.budgetbuddy.utils.Constants
import com.gdr.budgetbuddy.utils.Constants.DATA_PROCESSING_POLICY_AGREEMENT_DATE
import com.gdr.budgetbuddy.utils.Constants.IS_DATA_PROCESSING_POLICY_AGREED
import com.gdr.budgetbuddy.utils.Constants.IS_MARKETING_CONSENT
import com.gdr.budgetbuddy.utils.Constants.IS_PRIVACY_POLICY_AGREED
import com.gdr.budgetbuddy.utils.Constants.IS_TERMS_OF_SERVICE_AGREED
import com.gdr.budgetbuddy.utils.Constants.LAST_DATA_PROCESSING_POLICY_AGREEMENT_DATE
import com.gdr.budgetbuddy.utils.Constants.LAST_MARKETING_CONSENT_DATE
import com.gdr.budgetbuddy.utils.Constants.LAST_PRIVACY_POLICY_AGREEMENT_DATE
import com.gdr.budgetbuddy.utils.Constants.LAST_TERMS_OF_SERVICE_AGREEMENT_DATE
import com.gdr.budgetbuddy.utils.Constants.MARKETING_CONSENT_DATE
import com.gdr.budgetbuddy.utils.Constants.PRIVACY_POLICY_AGREEMENT_DATE
import com.gdr.budgetbuddy.utils.Constants.TERMS_OF_SERVICE_AGREEMENT_DATE
import com.gdr.budgetbuddy.utils.Constants.USER_ACCOUNT_COLLECTION
import com.gdr.budgetbuddy.utils.Constants.USER_GOOGLE_ID
import com.gdr.budgetbuddy.utils.Constants.USER_GOOGLE_TOKEN
import com.gdr.budgetbuddy.utils.Constants.USER_REGISTERATION_DATE
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class PolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPolicyBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var userGoogleId: String = ""
    private var userGoogleToken: String = ""

    // TODO: ActivityForResult 구성 
    private val agreementResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val resultData: Intent? = it.data
            val agreementCode = resultData?.getIntExtra(Constants.KEY_AGREEMENT_CODE, -1) ?: -1
            changeAgreementState(agreementCode)
        } else {
            // TODO: 갑자기 Activity 뒤로가기 눌렀을 때 

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        db = Firebase.firestore

        userGoogleId = intent.getStringExtra(USER_GOOGLE_ID) ?: ""
        userGoogleToken = intent.getStringExtra(USER_GOOGLE_TOKEN) ?: ""
        setEvent()
    }

    // TODO: 버튼 리스너 달기 
    private fun setEvent() {
        // TODO: 보기 누르면 이동 
        binding.termOfServiceContentBtn.setOnClickListener {
            moveToPolicyWebViewActivity(0)
        }

        binding.privacyPolicyContentBtn.setOnClickListener {
            moveToPolicyWebViewActivity(1)
        }

        binding.marketingContentBtn.setOnClickListener {
            moveToPolicyWebViewActivity(2)
        }

        // TODO: 구글 로그인 누르면 필수 체크 박스 all_checked 확인 
        // 약관 동의 히스토리 DB에 저장하기
        binding.signUpBtn.setOnClickListener {
            Log.d(LoginActivity.TAG, "signUpBtn is clicked")
            signUp()
        }
    }

    /**
     * fire Authenciation에 회원 가입
     */
    private fun signUp() {
        Log.d("GoogleLoginTest", "signUp 호출")
        Log.d("GoogleLoginTest", "userGoogleToken: $userGoogleToken")

        val firebaseCredential = GoogleAuthProvider.getCredential(userGoogleToken, null)

        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    saveUserRecord()
                    Log.i(LoginActivity.TAG, "user = $user")
                } else {
                    Log.e(LoginActivity.TAG, "task is not successful ${task.exception}")
                }
            }

        // TODO: 화면 이동 필요
    }

    /**
     * fireStore에 회원 정보 테이블 저장
     */
    private fun saveUserRecord() {
        val userRecord = hashMapOf(
            USER_REGISTERATION_DATE to CommonUtil.getCurrentDateTime(),
            IS_TERMS_OF_SERVICE_AGREED to true,
            IS_PRIVACY_POLICY_AGREED to true,
            IS_DATA_PROCESSING_POLICY_AGREED to true,
            IS_MARKETING_CONSENT to true,
            TERMS_OF_SERVICE_AGREEMENT_DATE to CommonUtil.getCurrentDate(),
            PRIVACY_POLICY_AGREEMENT_DATE to CommonUtil.getCurrentDate(),
            DATA_PROCESSING_POLICY_AGREEMENT_DATE to CommonUtil.getCurrentDate(),
            MARKETING_CONSENT_DATE to CommonUtil.getCurrentDate(),
            LAST_TERMS_OF_SERVICE_AGREEMENT_DATE to CommonUtil.getCurrentDate(),
            LAST_PRIVACY_POLICY_AGREEMENT_DATE to CommonUtil.getCurrentDate(),
            LAST_DATA_PROCESSING_POLICY_AGREEMENT_DATE to CommonUtil.getCurrentDate(),
            LAST_MARKETING_CONSENT_DATE to CommonUtil.getCurrentDate()
        )
        db.collection(USER_ACCOUNT_COLLECTION).document(userGoogleId).set(userRecord)
    }

    // TODO: agreementCode에 따라서 체크...
    private fun changeAgreementState(agreementCode: Int) {
        Log.d("hamcoding", "agreementCode: $agreementCode")
        when (agreementCode) {
            0 -> {

            }

            1 -> {

            }

            2 -> {

            }

            else -> {

            }
        }
    }

    /**
     * 웹 뷰 띄우기
     *
     * @param agreementCode
     */
    private fun moveToPolicyWebViewActivity(agreementCode: Int) {
        val intent = Intent(this, PolicyWebViewActivity::class.java)
        intent.putExtra(Constants.KEY_AGREEMENT_CODE, agreementCode)
        agreementResultLauncher.launch(intent)
    }
}