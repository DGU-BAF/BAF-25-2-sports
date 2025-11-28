package com.example.baro.feature.bot.ui.list

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baro.R
import com.example.baro.databinding.FragmentBotBinding
import com.example.baro.feature.bot.BotServiceLocator
import com.example.baro.feature.auth.ui.location.LocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BotChatListFragment : Fragment(R.layout.fragment_bot) {

    private var _binding: FragmentBotBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Bot ViewModel
    private val viewModel: BotChatListViewModel by viewModels {
        BotChatListViewModelFactory(BotServiceLocator.botRepository)
    }

    // 위치 업데이트 ViewModel
    private val locationViewModel: LocationViewModel by viewModels {
        LocationViewModel.Factory()
    }

    private lateinit var adapter: BotChatListAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBotBinding.bind(view)

        // FusedLocation Client 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        setupRecyclerView()
        setupButtons()
        observeUiState()
        observeLocationState()
    }


    private fun setupRecyclerView() {
        adapter = BotChatListAdapter { room ->
            val direction =
                BotChatListFragmentDirections.actionBotChatListFragmentToBotChatRoomFragment(
                    roomId = room.id,
                    roomTitle = room.title
                )
            findNavController().navigate(direction)
        }

        binding.rvChats.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChats.adapter = adapter
    }


    private fun setupButtons() {

        // 신규 챗방
        binding.newChat.setOnClickListener {
            viewModel.createNewRoom { roomId ->
                val direction =
                    BotChatListFragmentDirections.actionBotChatListFragmentToBotChatRoomFragment(
                        roomId = roomId,
                        roomTitle = "새 대화"
                    )
                findNavController().navigate(direction)
            }
        }

        // 위치 업데이트
        binding.btnLocation.setOnClickListener {
            updateWithCurrentLocation()
        }
    }


    private fun updateWithCurrentLocation() {
        val ctx = requireContext()

        // 권한 체크
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2001)
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


    private fun observeLocationState() {
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
    }


    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                adapter.submitList(state.rooms)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 2001) {
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
