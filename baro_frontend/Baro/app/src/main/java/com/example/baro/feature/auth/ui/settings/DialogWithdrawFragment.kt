package com.example.baro.feature.auth.ui.settings

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.baro.databinding.DialogWithdrawBinding

class DialogWithdrawFragment(
    private val onConfirm: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        val binding = DialogWithdrawBinding.inflate(requireActivity().layoutInflater)

        builder.setView(binding.root)

        val dialog = builder.create()

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnWithdrawAccount.setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        return dialog
    }
}
