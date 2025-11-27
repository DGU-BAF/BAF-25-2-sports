package com.example.baro.feature.auth.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.baro.R
import com.example.baro.GlobalApplication
import com.example.baro.core.network.NetworkModule
import com.example.baro.databinding.FragmentSettingsBinding
import com.example.baro.feature.auth.data.local.AuthLocalDataSource
import com.example.baro.feature.auth.data.local.SessionManager
import com.example.baro.feature.auth.data.remote.AuthApi
import com.example.baro.feature.auth.data.repository.AuthRepositoryImpl
import com.example.baro.feature.auth.ui.location.LocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    // 기존 SettingsViewModel
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

    // 위치 업데이트 전용 ViewModel
    private val locationViewModel: LocationViewModel by viewModels {
        LocationViewModel.Factory()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        // FusedLocation 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // 프로필 로드
        viewModel.loadProfile()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.user.collectLatest { user ->
                if (user != null) {
                    binding.tvNickname.text = user.nickname
                    binding.tvSportsmanship.text = "${user.sportsmanship}°C"
                }
            }
        }

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_profileEditFragment)
        }

        binding.rowParticipatedParties.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_settingsMyParty)
        }

        binding.btnSportsmanship.setOnClickListener {
            DialogSportsmanshipFragment().show(parentFragmentManager, "sportsmanship")
        }


        // -----------------------------
        // 현재 위치로 업데이트
        // -----------------------------
        binding.layoutSetCurrentLocation.setOnClickListener {
            updateWithCurrentLocation()
        }


        // ViewModel 상태 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            locationViewModel.locationState.collectLatest { state ->
                when (state) {
                    is LocationViewModel.LocationUiState.Success ->
                        Toast.makeText(requireContext(), "위치를 저장했습니다.", Toast.LENGTH_SHORT).show()

                    is LocationViewModel.LocationUiState.Error ->
                        Toast.makeText(requireContext(), "위치 저장 실패: ${state.message}", Toast.LENGTH_SHORT).show()

                    else -> Unit
                }
            }
        }


        binding.rowLogout.setOnClickListener {
            DialogLogoutFragment(
                onConfirm = { viewModel.logout() }
            ).show(parentFragmentManager, "logout_dialog")
        }

        binding.rowWithdraw.setOnClickListener {
            DialogWithdrawFragment(
                onConfirm = { viewModel.withdraw() }
            ).show(parentFragmentManager, "withdraw_dialog")
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    // ===============================
    // GPS 현재 위치 가져오기
    // ===============================

    private fun updateWithCurrentLocation() {
        val ctx = requireContext()

        // 권한 체크
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lng = location.longitude
                locationViewModel.updateLocation(lat, lng)
            } else {
                Toast.makeText(ctx, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(ctx, "위치 조회 실패", Toast.LENGTH_SHORT).show()
        }
    }

    // 권한 요청 콜백
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                updateWithCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
