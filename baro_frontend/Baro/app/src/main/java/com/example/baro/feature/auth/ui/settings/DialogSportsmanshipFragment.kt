package com.example.baro.feature.auth.ui.settings

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.baro.R

class DialogSportsmanshipFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_sportsmanship, null)

        builder.setView(view)

        return builder.create()
    }
}
