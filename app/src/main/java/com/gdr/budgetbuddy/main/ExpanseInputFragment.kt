package com.gdr.budgetbuddy.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gdr.budgetbuddy.databinding.FragmentExpanseInputBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ExpanseInputFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentExpanseInputBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentExpanseInputBinding.inflate(inflater)
        return binding.root
    }
}