package com.gdr.budgetbuddy.userauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.gdr.budgetbuddy.databinding.ActivityPolicyBinding
import com.gdr.budgetbuddy.utils.Constants

class PolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPolicyBinding

    // TODO activityResult 구성
    private val agreementResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val resultData: Intent? = it.data
            val agreementCode = resultData?.getIntExtra(Constants.KEY_AGREEMENT_CODE, -1) ?: -1
            changeAgreementState(agreementCode)
        } else {
            // 갑자기 Activity 뒤로가기 눌렀을 때?

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEvent()
    }

    // TODO 버튼 리스너 달기
    private fun setEvent() {
        // TODO 보기 누르면 이동
        binding.termOfServiceContentBtn.setOnClickListener {
            moveToPolicyWebViewActivity(0)
        }

        binding.privacyPolicyContentBtn.setOnClickListener {
            moveToPolicyWebViewActivity(1)
        }

        binding.marketingContentBtn.setOnClickListener {
            moveToPolicyWebViewActivity(2)
        }

        // TODO 구글 로그인 누르면 필수 체크 박스 all_checked 확인
        // TODO 약관 동의 히스토리 DB에 저장하기
    }

    // TODO agreementCode에 따라서 체크...
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

    private fun moveToPolicyWebViewActivity(agreementCode: Int) {
        val intent = Intent(this, PolicyWebViewActivity::class.java)
        intent.putExtra(Constants.KEY_AGREEMENT_CODE, agreementCode)
        agreementResultLauncher.launch(intent)
    }
}