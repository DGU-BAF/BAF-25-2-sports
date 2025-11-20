// com/example/baro/feature/party/ui/create/PartyCreateFragment.kt
package com.example.baro.feature.party.ui.create

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.baro.R
import com.example.baro.databinding.FragmentPartyCreateBinding
import com.example.baro.feature.select.SelectSportsActivity
import com.example.baro.feature.select.SelectPlaceActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class PartyCreateFragment : Fragment(R.layout.fragment_party_create) {

    private var _binding: FragmentPartyCreateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PartyCreateViewModel by viewModels()

    // 선택된 값들
    private var selectedSport: String? = null

    private var selectedPlaceName: String? = null

    // ISO 형식 (백엔드용)
    private var selectedDateIso: String? = null      // "YYYY-MM-DD"
    private var selectedStartTime: String? = null    // "HH:mm"
    private var selectedEndTime: String? = null      // "HH:mm"

    // 운동 선택
    private val pickSportLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val list = result.data
                    ?.getStringArrayListExtra(SelectSportsActivity.EXTRA_SELECTED)
                    ?: arrayListOf()

                selectedSport = list.firstOrNull()
                binding.createPartySport.text = selectedSport ?: getString(R.string.choose)

            }
        }

    // 장소 선택 (EXTRA 이름은 실제 정의에 맞게 조정)
    // 장소 선택 런처
    private val pickPlaceLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val list = result.data
                    ?.getStringArrayListExtra(SelectPlaceActivity.EXTRA_SELECTED)
                    ?: arrayListOf()

                // 여러 개 가운데 첫 번째만 사용
                selectedPlaceName = list.firstOrNull()

                binding.createPartyPlace.text = selectedPlaceName.orEmpty()

            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPartyCreateBinding.bind(view)

        setupPickers()
        setupListeners()
        setupObservers()
    }

    private fun setupPickers() = with(binding) {
        // 운동 종목 선택
        inputSport.setOnClickListener {
            val intent = Intent(requireContext(), SelectSportsActivity::class.java)
            pickSportLauncher.launch(intent)
        }

        // 장소 선택
        inputPlace.setOnClickListener {
            val intent = Intent(requireContext(), SelectPlaceActivity::class.java)
            pickPlaceLauncher.launch(intent)
        }

        // 날짜 선택
        inputDate.setOnClickListener {
            showDatePicker()
        }

        // 시작 시간
        inputStartTime.setOnClickListener {
            showTimePicker(isStart = true)
        }

        // 종료 시간
        inputEndTime.setOnClickListener {
            showTimePicker(isStart = false)
        }
    }

    private fun setupListeners() {
        binding.btnCreate.setOnClickListener {
            val title = binding.inputTitle.text.toString().trim()
            val description = binding.inputDescription.text.toString().trim()
            val capacity = binding.inputCapacity.text.toString().toIntOrNull() ?: 0

            val sport = selectedSport
            val placeName = selectedPlaceName
            val dateIso = selectedDateIso
            val startTime = selectedStartTime
            val endTime = selectedEndTime

            // 최소 검증
            if (title.isEmpty()
                || sport.isNullOrEmpty()
                || placeName.isNullOrEmpty()
                || dateIso.isNullOrEmpty()
                || startTime.isNullOrEmpty()
                || endTime.isNullOrEmpty()
                || capacity <= 0
            ) {
                toast("모든 필수 항목을 입력해 주세요.")
                return@setOnClickListener
            }

            viewModel.createParty(
                title = title,
                sport = sport,
                place = placeName,
                description = description,
                date = dateIso,
                startTime = startTime,
                endTime = endTime,
                capacity = capacity
            )
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.createdParty.collectLatest { created ->
                    if (created != null) {
                        toast("파티가 생성되었습니다.")
                        requireActivity()
                            .onBackPressedDispatcher
                            .onBackPressed()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.errorMessage.collectLatest { msg ->
                if (msg != null) {
                    toast(msg)
                    viewModel.clearError()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isSubmitting.collectLatest { submitting ->
                binding.btnCreate.isEnabled = !submitting
                binding.btnCreate.alpha = if (submitting) 0.5f else 1f
            }
        }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) // 0-based
        val day = cal.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, y, m, d ->
            selectedDateIso = "%04d-%02d-%02d".format(y, m + 1, d)
            val display = "${m + 1}월 ${d}일"
            binding.inputDate.setText(display)
        }, year, month, day).show()
    }

    private fun showTimePicker(isStart: Boolean) {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, h, m ->
            val timeStr = "%02d:%02d".format(h, m)
            if (isStart) {
                selectedStartTime = timeStr
                binding.inputStartTime.setText(timeStr)
            } else {
                selectedEndTime = timeStr
                binding.inputEndTime.setText(timeStr)
            }
        }, hour, minute, true).show()
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
