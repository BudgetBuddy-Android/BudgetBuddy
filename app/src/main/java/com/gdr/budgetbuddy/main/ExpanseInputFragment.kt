package com.gdr.budgetbuddy.main

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gdr.budgetbuddy.databinding.FragmentExpanseInputBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar


class ExpanseInputFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ExpanseInputFragment"
    }

    private lateinit var binding: FragmentExpanseInputBinding
    private lateinit var context: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentExpanseInputBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 데이트피커
        val calendar = Calendar.getInstance()
        val data = DatePickerDialog.OnDateSetListener { view, year, month, day ->
            binding.expanseDate.text = "${year}.${month}.${day}"
        }

        binding.expanseDate.setOnClickListener {
            DatePickerDialog(context, data, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // TODO: 화폐단위 ("," 혹은 "원") 추가
        binding.expanseAmount
    }
}