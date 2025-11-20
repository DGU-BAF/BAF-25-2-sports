// feature/feedback/ui/rating/FeedbackFragment.kt
package com.example.baro.feature.feedback.ui.rating

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.baro.R
import com.example.baro.databinding.FragmentSettingsRatePartyBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FeedbackFragment : Fragment(R.layout.fragment_settings_rate_party) {

    private var _binding: FragmentSettingsRatePartyBinding? = null
    private val binding get() = _binding!!

    private val args: FeedbackFragmentArgs by navArgs()
    private val viewModel: FeedbackViewModel by viewModels()

    private lateinit var adapter: RatingMemberAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsRatePartyBinding.bind(view)

        adapter = RatingMemberAdapter { userId, rating ->
            viewModel.updateRating(userId, rating)
        }
        binding.rvMembers.adapter = adapter

        binding.backToHome.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSubmitRating.setOnClickListener {
            viewModel.submit()
        }

        setupObservers()
        viewModel.loadTargets(args.partyId)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.members.collectLatest { adapter.submitList(it) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { msg ->
                if (msg != null) {
                    toast(msg)
                    viewModel.clearError()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.submitSuccess.collectLatest { ok ->
                if (ok) {
                    toast("후기를 제출했습니다.")
                    viewModel.consumeSubmitSuccess()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loading.collectLatest { loading ->
                binding.btnSubmitRating.isEnabled = !loading
                binding.btnSubmitRating.alpha = if (loading) 0.5f else 1f
            }
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
