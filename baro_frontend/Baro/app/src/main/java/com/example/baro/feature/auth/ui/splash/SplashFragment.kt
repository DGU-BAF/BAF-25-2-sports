package com.example.baro.feature.auth.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.baro.GlobalApplication
import com.example.baro.R
import com.example.baro.databinding.FragmentSplashBinding
import com.example.baro.feature.auth.data.local.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSplashBinding.bind(view)

        lifecycleScope.launch {
            decideNext()
        }
    }

    private suspend fun decideNext() {
        val app = requireContext().applicationContext as GlobalApplication
        val dataStore = app.dataStore
        val token = SessionManager(dataStore).accessToken.first()

        if (token.isNullOrBlank()) {
            // 로그인 안 되어 있음
            findNavController().navigate(
                R.id.action_splashFragment_to_loginFragment
            )
        } else {
            // 로그인 되어 있음
            findNavController().navigate(
                R.id.action_splashFragment_to_mainFragment
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
