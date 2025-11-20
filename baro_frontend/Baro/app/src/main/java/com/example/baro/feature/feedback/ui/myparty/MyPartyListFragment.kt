// feature/feedback/ui/myparty/MyPartyListFragment.kt
package com.example.baro.feature.feedback.ui.myparty

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.baro.R
import com.example.baro.databinding.FragmentSettingsMypartyBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyPartyListFragment : Fragment(R.layout.fragment_settings_myparty) {

    private var _binding: FragmentSettingsMypartyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyPartyListViewModel by viewModels()
    private lateinit var adapter: MyPartyListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsMypartyBinding.bind(view)

        adapter = MyPartyListAdapter { item ->
            val bundle = Bundle().apply { putString("partyId", item.partyId) }
            findNavController().navigate(
                R.id.action_settingsMyParty_to_settingsRateParty,
                bundle
            )
        }
        binding.rvMyParties.adapter = adapter

        binding.backToSettings.setOnClickListener {
            findNavController().navigateUp()
        }

        setupObservers()
        viewModel.load()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.items.collectLatest { adapter.submitList(it) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { msg ->
                if (msg != null) {
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
