// com/example/baro/feature/home/HomeFragment.kt
package com.example.baro.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.launchWhenStarted
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baro.R
import com.example.baro.databinding.FragmentHomeBinding
import com.example.baro.feature.party.ui.list.PartyListAdapter
import com.example.baro.feature.party.ui.list.PartyListViewModel
import kotlinx.coroutines.flow.collectLatest

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PartyListViewModel by viewModels()

    // 파티 목록용 어댑터
    private val partyAdapter by lazy {
        PartyListAdapter(
            onClickItem = { partyId ->
                goPartyDetail(partyId)
            },
            onClickAction = { item ->
                // 참여/나가기 처리
                viewModel.handleAction(item.summary)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun goPartyDetail(id: String) {
        val bundle = Bundle().apply { putString("partyId", id) }
        findNavController().navigate(
            R.id.action_navigation_home_to_navigation_party_detail,
            bundle
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 설정 화면으로 이동
        binding.settings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
        binding.myprofile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        // 파티 리스트 RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = partyAdapter
            setHasFixedSize(true)
        }

        // 새로고침 버튼
        binding.btnRefresh.setOnClickListener {
            viewModel.refresh()
        }

        // 파티 생성으로 이동
        binding.createParty.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_partyCreate)
        }

        // 상태 구독
        setupObservers()

        // 최초 로드
        viewModel.refresh()
    }

    private fun setupObservers() {
        // 파티 리스트
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.listItems.collectLatest { list ->
                partyAdapter.submitList(list)
            }
        }

        // 로딩 상태 → 새로고침 버튼 enable/disable
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isLoading.collectLatest { loading ->
                binding.btnRefresh.isEnabled = !loading
            }
        }

        // 에러 메시지
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.errorMessage.collectLatest { msg ->
                msg?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
