package com.example.baro.feature.auth.ui.settings

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.baro.databinding.DialogLogoutBinding

class DialogLogoutFragment(
    private val onConfirm: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        val binding = DialogLogoutBinding.inflate(requireActivity().layoutInflater)

        builder.setView(binding.root)

        val dialog = builder.create()

        // 버튼 이벤트 처리
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnLogout.setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }

        return dialog
    }
}
