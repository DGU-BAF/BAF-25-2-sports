package com.example.baro.feature.bot.ui.room

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.baro.R
import com.example.baro.databinding.FragmentBotRoomBinding
import com.example.baro.feature.bot.BotServiceLocator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BotChatRoomFragment : Fragment(R.layout.fragment_bot_room) {

    private var _binding: FragmentBotRoomBinding? = null
    private val binding get() = _binding!!

    private val args: BotChatRoomFragmentArgs by navArgs()

    // BotServiceLocator 사용으로 DI 통일
    private val viewModel: BotChatRoomViewModel by viewModels {
        BotChatRoomViewModelFactory(BotServiceLocator.botRepository)
    }

    private lateinit var adapter: BotMessageAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBotRoomBinding.bind(view)

        setupRecyclerView()
        setupInput()
        setupBackButton()
        observeUiState()

        viewModel.loadRoom(args.roomId)
    }

    private fun setupRecyclerView() {
        adapter = BotMessageAdapter()
        binding.rvChatMessages.layoutManager =
            LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
        binding.rvChatMessages.adapter = adapter
    }

    private fun setupInput() {
        binding.etMessage.doOnTextChanged { text, _, _, _ ->
            viewModel.updateInput(text?.toString().orEmpty())
        }

        binding.btnSend.setOnClickListener {
            viewModel.sendMessage()
        }
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->

                if (state.messages.isEmpty()) {
                    binding.layoutEmptyState.visibility = View.VISIBLE
                    binding.rvChatMessages.visibility = View.GONE
                } else {
                    binding.layoutEmptyState.visibility = View.GONE
                    binding.rvChatMessages.visibility = View.VISIBLE
                    adapter.submitList(state.messages) {
                        binding.rvChatMessages.scrollToPosition(state.messages.size - 1)
                    }
                }

                // EditText 내용과 ViewModel inputText 싱크 맞추기
                if (binding.etMessage.text.toString() != state.inputText) {
                    binding.etMessage.setText(state.inputText)
                    binding.etMessage.setSelection(state.inputText.length)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
