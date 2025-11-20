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
            // TODO: 실제 카카오 SDK 호출 후, accessToken 얻어서 아래 함수에 넣어주면 됨
            // 예시: viewModel.loginWithKakaoToken(kakaoAccessToken)
        }

//        // 로딩 상태
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.isLoading.collectLatest { loading ->
//                binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
//            }
//        }

        // 에러 메시지
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { msg ->
                if (msg != null) {
                    // TODO: Snackbar / Toast 등으로 표시
                    // Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }
        }

        // 로그인 성공 시 홈/온보딩 등으로 이동
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loginSuccessEvent.collectLatest { success ->
                if (success) {
                    // TODO: 실제 목적지로 수정 (예: 홈 화면)
                    // findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    viewModel.consumeLoginSuccessEvent()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
