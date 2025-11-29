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
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.util.Log

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
        val sessionManager = SessionManager(dataStore)



        // 1) 먼저 카카오 토큰이 유효한지 확인
        //    (카카오톡 설정에서 이 앱 회원탈퇴 했으면 여기서 에러/ null 떨어짐)
        val kakaoTokenValid = suspendCancellableKakaoCheck()

        Log.d("Splash", "kakaoTokenValid = $kakaoTokenValid")

        if (!kakaoTokenValid) {
            Log.d("Splash", "Kakao invalid → clearAccessToken + go Login")
            // 카카오 쪽에서 이미 토큰 끊겨 있음 → 우리 토큰도 지우고 로그인으로
            sessionManager.clearAccessToken()
            findNavController().navigate(
                R.id.action_splashFragment_to_loginFragment
            )
            return
        }

        // 2) 카카오 토큰은 살아 있음 → 우리 서버 토큰(accessToken) 확인
        val token = sessionManager.accessToken.first()
        Log.d("Splash", "backend accessToken = $token")

        if (token.isNullOrBlank()) {
            Log.d("Splash", "No backend token → go Login")
            // 우리 서버 기준으로 로그인 안 되어 있음
            findNavController().navigate(
                R.id.action_splashFragment_to_loginFragment
            )
        } else {
            Log.d("Splash", "Has backend token → go Main")
            // 카카오 + 우리 서버 둘 다 로그인 상태 → 메인으로
            findNavController().navigate(
                R.id.action_splashFragment_to_homeFragment
            )
        }
    }

    // Kakao SDK 의 accessTokenInfo 를 suspend로 감싼 헬퍼
    private suspend fun suspendCancellableKakaoCheck(): Boolean {
        return kotlinx.coroutines.suspendCancellableCoroutine { cont ->
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null || tokenInfo == null) {
                    cont.resume(false, onCancellation = null)
                } else {
                    cont.resume(true, onCancellation = null)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
