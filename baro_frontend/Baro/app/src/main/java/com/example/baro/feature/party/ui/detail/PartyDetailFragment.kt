package com.example.baro.feature.party.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.baro.R
import com.example.baro.databinding.FragmentPartyDetailBinding
import kotlinx.coroutines.flow.collectLatest

class PartyDetailFragment : Fragment(R.layout.fragment_party_detail) {

    private var _binding: FragmentPartyDetailBinding? = null
    private val binding get() = _binding!!

    private val args: PartyDetailFragmentArgs by navArgs()
    private val viewModel: PartyDetailViewModel by viewModels()

    private lateinit var memberAdapter: PartyMemberAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPartyDetailBinding.bind(view)

        setupRecyclerView()
        setupObservers()
        setupListeners()

        viewModel.loadPartyDetail(args.partyId)
    }

    private fun setupRecyclerView() {
        memberAdapter = PartyMemberAdapter()
        binding.recyclerMembers.adapter = memberAdapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.partyDetail.collectLatest { detail ->
                if (detail != null) {
                    binding.title.text = detail.title
                    binding.place.text = detail.place
                    binding.description.text = detail.description
//                    binding.startTime.text = detail.startTime
//                    binding.endTime.text = detail.endTime
                    binding.time.text = "${detail.startTime} ~ ${detail.endTime}"

                    memberAdapter.submitList(detail.members)

                    binding.btnAction.apply {
                        visibility = if (viewModel.canShowActionButton()) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                        text = viewModel.getActionButtonText()
                        isEnabled = viewModel.isActionButtonEnabled()
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnAction.setOnClickListener {
            viewModel.onClickActionButton()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
