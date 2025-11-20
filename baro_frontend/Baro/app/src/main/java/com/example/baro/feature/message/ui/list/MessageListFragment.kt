package com.example.baro.feature.message.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.example.baro.R
import com.example.baro.databinding.FragmentMessageBinding
import com.example.baro.feature.message.MessageServiceLocator
import com.example.baro.feature.message.ui.navigation.MessageNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MessageListFragment : Fragment(R.layout.fragment_message) {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    // MessageServiceLocator 를 이용해 실제 UseCase 를 주입하는 팩토리 사용
    private val viewModel: MessageListViewModel by viewModels {
        MessageListViewModel.provideFactory(
            getMessageRoomsUseCase = MessageServiceLocator.getMessageRoomsUseCase
        )
    }

    private lateinit var adapter: MessageListAdapter
    private lateinit var navigator: MessageNavigator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMessageBinding.bind(view)

        navigator = MessageNavigator(findNavController())

        setupRecyclerView()
        collectUiState()
    }

    private fun setupRecyclerView() {
        adapter = MessageListAdapter { room ->
            navigator.navigateToRoom(
                roomId = room.roomId,
                roomName = room.roomName
            )
        }
        binding.rvChats.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChats.adapter = adapter
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                // 필요하면 state.isLoading, state.errorMessage 도 여기서 처리
                adapter.submitList(state.rooms)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
