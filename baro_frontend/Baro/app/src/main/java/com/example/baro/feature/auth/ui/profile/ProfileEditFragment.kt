package com.example.baro.feature.auth.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.baro.GlobalApplication
import com.example.baro.R
import com.example.baro.core.network.NetworkModule
import com.example.baro.databinding.FragmentSettingsProfileBinding
import com.example.baro.feature.auth.data.local.AuthLocalDataSource
import com.example.baro.feature.auth.data.local.SessionManager
import com.example.baro.feature.auth.data.remote.AuthApi
import com.example.baro.feature.auth.data.repository.AuthRepositoryImpl
import com.example.baro.feature.select.SelectSportsActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileEditFragment : Fragment(R.layout.fragment_settings_profile) {

    private var _binding: FragmentSettingsProfileBinding? = null
    private val binding get() = _binding!!

    private val selectedSports = arrayListOf<String>()

    private val viewModel: ProfileEditViewModel by viewModels {
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
                return ProfileEditViewModel(authRepository) as T
            }
        }
    }

    private val pickSportsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val list = result.data
                    ?.getStringArrayListExtra(SelectSportsActivity.EXTRA_SELECTED)
                    ?: arrayListOf()
                selectedSports.clear()
                selectedSports.addAll(list)

                val tvValue = binding.rowSelect.findViewById<TextView>(R.id.tvValue)
                tvValue.text =
                    if (list.isEmpty()) getString(R.string.choose) else list.joinToString(", ")

                viewModel.setFavoriteSports(selectedSports)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsProfileBinding.bind(view)

        setupSelectSportsRow()
        setupFormWatchers()

        // 프로필 불러오기
        viewModel.loadProfile()

        // 유저 정보 관찰해서 초기값 세팅
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.user.collectLatest { user ->
                if (user != null) {
                    binding.etNickname.setText(user.nickname)
                    binding.etHeight.setText(user.height.toInt().toString())
                    binding.etWeight.setText(user.weight.toInt().toString())
                    binding.etMuscle.setText(user.muscleMass?.toInt()?.toString().orEmpty())

                    selectedSports.clear()
                    selectedSports.addAll(user.favoriteSports)
                    val tvValue = binding.rowSelect.findViewById<TextView>(R.id.tvValue)
                    tvValue.text =
                        if (user.favoriteSports.isEmpty()) getString(R.string.choose)
                        else user.favoriteSports.joinToString(", ")

                    when (user.skillLevel) {
                        "advanced", "high" -> binding.rbLevelHigh.isChecked = true
                        "intermediate", "mid" -> binding.rbLevelMid.isChecked = true
                        "beginner", "low" -> binding.rbLevelLow.isChecked = true
                    }
                }
            }
        }

        // 로딩
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { loading ->
                binding.btnSave.isEnabled = !loading
                // 필요하면 프로그레스바 추가해서 여기서 제어
            }
        }

        // 에러
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { msg ->
                if (msg != null) {
                    toast(msg)
                    viewModel.clearError()
                }
            }
        }

        // 성공 시 뒤로 가기
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateSuccessEvent.collectLatest { success ->
                if (success) {
                    findNavController().popBackStack()
                    viewModel.consumeUpdateSuccessEvent()
                }
            }
        }

        // 저장 버튼
        binding.btnSave.setOnClickListener {
            if (!validateForm()) return@setOnClickListener
            viewModel.updateProfile()
        }

        // 뒤로가기(툴바 버튼 같은 것)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupSelectSportsRow() = with(binding) {
        val openPicker = {
            val intent = Intent(requireContext(), SelectSportsActivity::class.java)
                .putStringArrayListExtra(
                    SelectSportsActivity.EXTRA_PRESELECTED,
                    ArrayList(selectedSports)
                )
            pickSportsLauncher.launch(intent)
        }
        rowSelect.setOnClickListener { openPicker() }
        rowSelect.findViewById<View>(R.id.ivChevron).setOnClickListener { openPicker() }
    }

    private fun setupFormWatchers() = with(binding) {
        etNickname.doAfterTextChanged {
            viewModel.nickname.value = it.toString().trim()
        }
        etHeight.doAfterTextChanged {
            viewModel.height.value = it.toString()
        }
        etWeight.doAfterTextChanged {
            viewModel.weight.value = it.toString()
        }
        etMuscle.doAfterTextChanged {
            viewModel.muscleMass.value = it.toString()
        }
        with(binding) {
            rgLevel.setOnCheckedChangeListener { _, checkedId ->
                viewModel.skillLevel.value = when (checkedId) {
                    R.id.rbLevelHigh -> "상"   // 혹은 "high"
                    R.id.rbLevelMid  -> "중" // 혹은 "mid"
                    R.id.rbLevelLow  -> "하"  // 혹은 "low"
                    else -> viewModel.skillLevel.value
                }
            }
        }

    }

    // 간단 검증 (이상한 값 막기)
    private fun validateForm(): Boolean {
        val nickname = binding.etNickname.text?.toString()?.trim()
        val h = binding.etHeight.text.toString().toIntOrNull()
        val w = binding.etWeight.text.toString().toIntOrNull()
        val m = binding.etMuscle.text.toString().toIntOrNull()

        if (nickname.isNullOrEmpty()) {
            toast("닉네임을 입력해 주세요.")
            return false
        }
        if (h != null && h !in 100..250) {
            toast("키는 100~250cm 범위로 입력해 주세요.")
            return false
        }
        if (w != null && w !in 30..250) {
            toast("몸무게는 30~250kg 범위로 입력해 주세요.")
            return false
        }
        if (m != null && (m < 0 || m > 200)) {
            toast("골격근량 값이 비정상적입니다.")
            return false
        }
        return true
    }

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
