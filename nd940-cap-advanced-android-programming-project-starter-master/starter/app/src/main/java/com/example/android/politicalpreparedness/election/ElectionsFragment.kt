package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionClickListener
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.CivicsRepository
import com.example.android.politicalpreparedness.network.models.Election
import com.google.android.material.snackbar.Snackbar

class ElectionsFragment : Fragment() {

    private val electionsViewModel: ElectionsViewModel by lazy {
        ViewModelProvider(
            this, ElectionsViewModelFactory(
                CivicsRepository(),
                ElectionDatabase.getInstance(requireActivity().application).electionDao
            )
        ).get(ElectionsViewModel::class.java)
    }

    private var _binding: FragmentElectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var electionsListAdapter: ElectionListAdapter
    private lateinit var savedElectionsListAdapter: ElectionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_election, container, false
        )

        binding.lifecycleOwner = this
        binding.electionsViewModel = electionsViewModel

        electionsListAdapter = ElectionListAdapter(
            ElectionClickListener { election ->
                navigateToToVoterInfoFragment(election)
            }
        )

        savedElectionsListAdapter = ElectionListAdapter(
            ElectionClickListener { election ->
                navigateToToVoterInfoFragment(election)
            }
        )

        binding.upcomingElectionsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.upcomingElectionsRv.adapter = electionsListAdapter

        binding.savedElectionsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.savedElectionsRv.adapter = savedElectionsListAdapter

        electionsViewModel.getElections()
        electionsViewModel.getSavedElections()

        return binding.root
    }

    private fun navigateToToVoterInfoFragment(election: Election) {
        this.findNavController().navigate(
            ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                election.id,
                election.division
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        electionsViewModel.upcomingElections.observe(
            viewLifecycleOwner,
            Observer<List<Election>> { elections ->
                elections?.apply {
                    electionsListAdapter.submitList(elections)

                }
            })

        electionsViewModel.upcomingSavedElections.observe(
            viewLifecycleOwner,
            Observer<List<Election>> { elections ->
                elections?.apply {
                    savedElectionsListAdapter.submitList(elections)

                }
            })

        electionsViewModel.showLoading.observe(
            viewLifecycleOwner,
            Observer<Boolean> { showLoading ->
                if (showLoading) {
                    binding.electionsLoading.visibility = View.VISIBLE
                } else {
                    binding.electionsLoading.visibility = View.GONE
                }
            })

        electionsViewModel.showError.observe(
            viewLifecycleOwner,
            Observer<String> { errorMessage ->
                if (errorMessage.isNotEmpty()) {
                    Snackbar.make(
                        binding.root,
                        errorMessage,
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    Snackbar.make(
                        binding.root,
                        R.string.error_api,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}