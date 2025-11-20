package com.example.baro.feature.message.ui.room

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baro.R
import com.example.baro.databinding.FragmentMessageRoomBinding
import com.example.baro.feature.message.ui.navigation.MessageNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MessageRoomFragment : Fragment(R.layout.fragment_message_room) {

    private var _binding: FragmentMessageRoomBinding? = null
    private val binding get() = _binding!!

    // Safe Args 사용 가정: roomId, roomName 전달
    private val args: MessageRoomFragmentArgs by navArgs()

    // DI(Hilt) 사용 시 @AndroidEntryPoint + @HiltViewModel 로 교체 가능
    private val viewModel: MessageRoomViewModel by viewModels {
        // TODO: ViewModelProvider.Factory 구현 또는 Hilt로 교체
        error("MessageRoomViewModel factory is not implemented yet.")
    }

    private lateinit var chatAdapter: ChatMessageAdapter
    private lateinit var navigator: MessageNavigator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMessageRoomBinding.bind(view)

        setupToolbar()
        setupRecyclerView()
        setupInput()
        collectUiState()

        viewModel.init(args.roomId)

        navigator = MessageNavigator(findNavController())

        binding.btnBack.setOnClickListener {
            navigator.navigateBack()
        }
    }

    private fun setupToolbar() {
        binding.tvPartyTitle.text = args.roomName

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatMessageAdapter()
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }

    private fun setupInput() {
        binding.btnSend.setOnClickListener {
            sendCurrentInput()
        }

        binding.etInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendCurrentInput()
                true
            } else {
                false
            }
        }
    }

    private fun sendCurrentInput() {
        val text = binding.etInput.text?.toString()?.trim().orEmpty()
        if (text.isNotEmpty()) {
            viewModel.sendMessage(text)
            binding.etInput.text?.clear()
        }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                // 로딩/에러 처리 필요 시 여기에서 추가
                chatAdapter.submitList(state.messages) {
                    // 새 메시지 도착 시 맨 아래로 스크롤
                    if (state.messages.isNotEmpty()) {
                        binding.rvMessages.scrollToPosition(state.messages.lastIndex)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
