package com.example.baro.feature.auth.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.baro.R
import com.example.baro.databinding.FragmentSettingsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.example.baro.GlobalApplication
import com.example.baro.feature.auth.data.local.AuthLocalDataSource
import com.example.baro.feature.auth.data.local.SessionManager
import com.example.baro.feature.auth.data.remote.AuthApi
import com.example.baro.feature.auth.data.repository.AuthRepositoryImpl
import com.example.baro.core.network.NetworkModule


class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // 간단한 방식: 직접 ViewModelFactory 안 만들고 newInstance 식으로 작성해두고,
    // 실제 코드에서는 Hilt 또는 custom factory로 교체하셔도 됩니다.
    private val viewModel: SettingsViewModel by viewModels {
        val app = requireContext().applicationContext as GlobalApplication
        val dataStore = app.dataStore

        val sessionManager = SessionManager(dataStore)
        val retrofit = NetworkModule.createAuthorizedRetrofit(sessionManager)
        val authApi = retrofit.create(AuthApi::class.java)
        val localDataSource = AuthLocalDataSource(sessionManager)
        val authRepository = AuthRepositoryImpl(authApi, localDataSource)

        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(authRepository) as T
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)


        // 프로필 로드
        viewModel.loadProfile()

        // 스포츠맨십, 닉네임 등 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.user.collectLatest { user ->
                if (user != null) {
                    binding.tvNickname.text = user.nickname
                    binding.tvSportsmanship.text = "${user.sportsmanship}°C"
                    // 필요한 다른 필드도 여기서 바인딩
                }
            }
        }

        // “내 정보 수정” → ProfileEditFragment 로 이동 (NavController 사용)
        binding.btnEditProfile.setOnClickListener {
            Log.d("SettingsFragment", "btnEditProfile 클릭됨")
            findNavController()
                .navigate(R.id.action_settingsFragment_to_profileEditFragment)
        }

        // 내가 참여한 파티 목록
        binding.rowParticipatedParties.setOnClickListener {
            Log.d("SettingsFragment", "rowParticipatedParties 클릭됨")
            findNavController().navigate(
                R.id.action_settingsFragment_to_settingsMyParty
            )
        }

        binding.btnSportsmanship.setOnClickListener {
            DialogSportsmanshipFragment().show(parentFragmentManager, "sportsmanship")
        }


        // 로그아웃
        binding.rowLogout.setOnClickListener {
            DialogLogoutFragment(
                onConfirm = { viewModel.logout() }
            ).show(parentFragmentManager, "logout_dialog")
        }


        // 탈퇴
        binding.rowWithdraw.setOnClickListener {
            DialogWithdrawFragment(
                onConfirm = { viewModel.withdraw() }
            ).show(parentFragmentManager, "withdraw_dialog")
        }

        // 로그아웃/탈퇴 이벤트 처리
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.logoutEvent.collectLatest { logout ->
                if (logout) {
                    // 네비게이션: 로그인/온보딩 화면으로 이동
                    // findNavController().navigate(...)
                    // 이벤트 소비 후 리셋
                    // viewModel.resetLogoutEvent() 같은 함수 만들어도 됨
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.withdrawEvent.collectLatest { withdraw ->
                if (withdraw) {
                    // 탈퇴 후 첫 화면으로 이동
                }
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

