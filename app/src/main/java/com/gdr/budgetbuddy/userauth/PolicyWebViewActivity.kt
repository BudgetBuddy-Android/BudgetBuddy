package com.gdr.budgetbuddy.userauth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gdr.budgetbuddy.databinding.ActivityPolicyWebViewBinding
import com.gdr.budgetbuddy.utils.Constants

class PolicyWebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPolicyWebViewBinding
    private var agreementCode = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        agreementCode = intent.getIntExtra(Constants.KEY_AGREEMENT_CODE, -1)
        binding = ActivityPolicyWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEvent()
    }

    private fun setEvent() {
        binding.agreenBtn.setOnClickListener {
            val intent = Intent().apply {
                putExtra(Constants.KEY_AGREEMENT_CODE, agreementCode)
            }
            setResult(RESULT_OK, intent)
            if (!isFinishing) finish()
        }
    }
}