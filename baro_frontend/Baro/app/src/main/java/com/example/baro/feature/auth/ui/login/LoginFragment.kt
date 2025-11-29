package com.example.baro.feature.auth.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.baro.GlobalApplication
import com.example.baro.R
import com.example.baro.core.network.NetworkModule
import com.example.baro.databinding.FragmentLoginBinding
import com.example.baro.feature.auth.data.local.AuthLocalDataSource
import com.example.baro.feature.auth.data.local.SessionManager
import com.example.baro.feature.auth.data.remote.AuthApi
import com.example.baro.feature.auth.data.repository.AuthRepositoryImpl
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels {
        // Application 에서 DataStore 가져오기
        val appContext = requireContext().applicationContext as GlobalApplication
        val dataStore = appContext.dataStore

        // SessionManager → Retrofit(AuthInterceptor 포함) → AuthApi → Repository
        val sessionManager = SessionManager(dataStore)
        val retrofit = NetworkModule.createAuthorizedRetrofit(sessionManager)
        val authApi = retrofit.create(AuthApi::class.java)
        val localDataSource = AuthLocalDataSource(sessionManager)
        val authRepository = AuthRepositoryImpl(authApi, localDataSource)

        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(authRepository) as T
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        // 카카오 로그인 버튼 클릭
        binding.btnKakaoLogin.setOnClickListener {
            val context = requireContext()

            // 1) 카카오톡 로그인 가능하면 먼저 시도
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    handleKakaoLoginResult(token, error)
                }
            } else {
                // 2) 카카오톡이 없으면 바로 계정 로그인(웹뷰/브라우저 방식)
                UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                    handleKakaoLoginResult(token, error)
                }
            }
        }

        // 에러 메시지
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { msg ->
                if (msg != null) {
                    android.util.Log.e("Login", "errorMessage from ViewModel = $msg")
                    // TODO: Snackbar / Toast 등
                    viewModel.clearError()
                }
            }
        }

        // 1. 로그인 성공(기존 회원) → 메인 화면으로 이동
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loginSuccessEvent.collectLatest { success ->
                if (success) {
                    findNavController().navigate(
                        R.id.action_loginFragment_to_homeFragment   // ← 본인 그래프에 맞게 수정
                    )
                    viewModel.consumeLoginSuccessEvent()
                }
            }
        }

// 2. 신규 회원 → 회원가입 화면으로 이동
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToSignupEvent.collectLatest { go ->
                if (go) {
                    findNavController().navigate(
                        R.id.action_loginFragment_to_signUpFragment  // ← 본인 그래프에 맞게 수정
                    )
                    viewModel.consumeNavigateToSignupEvent()
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleKakaoLoginResult(
        token: com.kakao.sdk.auth.model.OAuthToken?,
        error: Throwable?
    ) {
        if (error != null) {
            android.util.Log.d("Login", "카카오 로그인 실패: $error")
            // TODO: Toast나 Snackbar로 에러 안내
            return
        }

        if (token != null) {
            android.util.Log.d("Login", "카카오 로그인 성공, accessToken=${token.accessToken}")
            val kakaoAccessToken = token.accessToken
            viewModel.loginWithKakaoToken(kakaoAccessToken)
        }
    }

}
