package com.example.baro.feature.auth.ui.signup

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
import com.example.baro.GlobalApplication
import com.example.baro.R
import com.example.baro.core.network.NetworkModule
import com.example.baro.databinding.FragmentSignupBinding
import com.example.baro.feature.auth.data.local.AuthLocalDataSource
import com.example.baro.feature.auth.data.local.SessionManager
import com.example.baro.feature.auth.data.remote.AuthApi
import com.example.baro.feature.auth.data.repository.AuthRepositoryImpl
import com.example.baro.feature.select.SelectSportsActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class SignUpFragment : Fragment(R.layout.fragment_signup) {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    // 선택된 종목들
    private val selectedSports = arrayListOf<String>()

    private val viewModel: SignUpViewModel by viewModels {
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
                return SignUpViewModel(authRepository) as T
            }
        }
    }

    // 운동 선택 Activity 결과
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
                updateButtonState()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignupBinding.bind(view)

        setupSelectSportsRow()
        setUpFormWatchers()
        attachFieldGuards()
        updateButtonState()

        binding.btnComplete.setOnClickListener {
            if (!isFormValid()) {
                toast("필수 항목을 올바르게 입력해 주세요.")
                return@setOnClickListener
            }

            val y = binding.birthYear.text.toString().toInt()
            val m = binding.birthMonth.text.toString().toInt()
            val d = binding.birthDay.text.toString().toInt()
            val birthStr = "%04d-%02d-%02d".format(y, m, d)
            viewModel.birthDate.value = birthStr

            val nickname = binding.nickname.text?.toString()?.trim().orEmpty()
            viewModel.nickname.value = nickname

            val gender = when (binding.genderGroup.checkedRadioButtonId) {
                R.id.rbMale -> "male"
                R.id.rbFemale -> "female"
                else -> ""
            }
            viewModel.gender.value = gender

            // 키/몸무게/근육량은 텍스트 변경 시 이미 viewModel 쪽으로 넣어두었다고 가정
            viewModel.setFavoriteSports(selectedSports)

            viewModel.signUp()
        }

//        // 로딩 / 에러 / 성공 관찰
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.isLoading.collectLatest { loading ->
//                binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
//            }
//        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { msg ->
                if (msg != null) {
                    toast(msg)
                    viewModel.clearError()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.signUpSuccessEvent.collectLatest { success ->
                if (success) {
                    // TODO: 회원가입 성공 후 이동할 목적지로 교체
                    // findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                    viewModel.consumeSignUpSuccessEvent()
                }
            }
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

    // 텍스트 변경 감지 → 버튼 상태 업데이트 + ViewModel 값 연동
    private fun setUpFormWatchers() = with(binding) {
        birthYear.doAfterTextChanged { updateButtonState() }
        birthMonth.doAfterTextChanged { updateButtonState() }
        birthDay.doAfterTextChanged { updateButtonState() }

        nickname.doAfterTextChanged {
            viewModel.nickname.value = it.toString().trim()
            updateButtonState()
        }
        height.doAfterTextChanged {
            viewModel.height.value = it.toString()
            updateButtonState()
        }
        weight.doAfterTextChanged {
            viewModel.weight.value = it.toString()
            updateButtonState()
        }
        muscle.doAfterTextChanged {
            viewModel.muscleMass.value = it.toString()
        }

        genderGroup.setOnCheckedChangeListener { _, _ ->
            updateButtonState()
        }

        // 🔹 운동 실력 라디오 선택 → skillLevel 설정
        rgLevel.setOnCheckedChangeListener { _, checkedId ->
            viewModel.skillLevel.value = when (checkedId) {
                R.id.rbLevelHigh -> "상"      // 백엔드에서 쓰는 값에 맞춰서
                R.id.rbLevelMid -> "중"
                R.id.rbLevelLow -> "하"
                else -> ""
            }
            updateButtonState()
        }
    }

    // 포커스 잃을 때 값 범위 체크
    private fun attachFieldGuards() = with(binding) {
        birthYear.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val v = birthYear.text.toString().toIntOrNull()
                if (v == null || v !in 1900..currentYear()) {
                    toast("출생 연도는 1900~${currentYear()} 사이로 입력해 주세요.")
                }
            }
        }
        birthMonth.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val v = birthMonth.text.toString().toIntOrNull()
                if (v == null || v !in 1..12) {
                    toast("월은 1~12 사이로 입력해 주세요.")
                }
            }
        }
        birthDay.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val y = birthYear.text.toString().toIntOrNull()
                val m = birthMonth.text.toString().toIntOrNull()
                val d = birthDay.text.toString().toIntOrNull()
                if (y != null && m != null) {
                    val maxDay = maxDayOf(y, m)
                    if (d == null || d !in 1..maxDay) {
                        toast("해당 연·월의 일자는 1~$maxDay 사이입니다.")
                    }
                } else if (d != null) {
                    toast("먼저 연도와 월을 입력해 주세요.")
                }
            }
        }
        height.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val v = height.text.toString().toIntOrNull()
                if (v == null || v !in 100..250) {
                    toast("키는 100~250cm 범위로 입력해 주세요.")
                }
            }
        }
        weight.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val v = weight.text.toString().toIntOrNull()
                if (v == null || v !in 30..250) {
                    toast("몸무게는 30~250kg 범위로 입력해 주세요.")
                }
            }
        }
    }

    private fun updateButtonState() {
        val enabled = isFormValid()
        binding.btnComplete.isEnabled = enabled
        binding.btnComplete.alpha = if (enabled) 1f else 0.4f
    }

    private fun isFormValid(): Boolean {
        val y = binding.birthYear.text.toString().toIntOrNull()
        val m = binding.birthMonth.text.toString().toIntOrNull()
        val d = binding.birthDay.text.toString().toIntOrNull()
        val nm = binding.nickname.text?.toString()?.trim()
        val h = binding.height.text.toString().toIntOrNull()
        val w = binding.weight.text.toString().toIntOrNull()

        val genderChecked =
            (binding.genderGroup.checkedRadioButtonId == R.id.rbMale
                    || binding.genderGroup.checkedRadioButtonId == R.id.rbFemale)
        val sportsOk = selectedSports.isNotEmpty()

        val yearOk = y != null && y in 1900..currentYear()
        val monthOk = m != null && m in 1..12
        val dayOk = d != null && y != null && m != null && d in 1..maxDayOf(y, m)
        val nameOk = !nm.isNullOrEmpty()
        val heightOk = h != null && h in 100..250
        val weightOk = w != null && w in 30..250

        return yearOk && monthOk && dayOk &&
                nameOk && heightOk && weightOk &&
                genderChecked && sportsOk
    }

    private fun currentYear(): Int =
        Calendar.getInstance().get(Calendar.YEAR)

    private fun isLeap(y: Int): Boolean =
        (y % 4 == 0 && y % 100 != 0) || (y % 400 == 0)

    private fun maxDayOf(y: Int, m: Int): Int = when (m) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeap(y)) 29 else 28
        else -> 31
    }

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
