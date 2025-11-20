package com.example.baro.feature.bot.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baro.R
import com.example.baro.databinding.FragmentBotBinding
import com.example.baro.feature.bot.BotServiceLocator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BotChatListFragment : Fragment(R.layout.fragment_bot) {

    private var _binding: FragmentBotBinding? = null
    private val binding get() = _binding!!

    // BotServiceLocator 에서 만든 Repository 주입
    private val viewModel: BotChatListViewModel by viewModels {
        BotChatListViewModelFactory(BotServiceLocator.botRepository)
    }

    private lateinit var adapter: BotChatListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBotBinding.bind(view)

        setupRecyclerView()
        setupButtons()
        observeUiState()
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

        binding.btnLocation.setOnClickListener {
            // TODO: 위치 설정 화면으로 이동 또는 위치 권한 요청
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                adapter.submitList(state.rooms)
                // 로딩/에러 처리 필요하면 여기서 추가
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
