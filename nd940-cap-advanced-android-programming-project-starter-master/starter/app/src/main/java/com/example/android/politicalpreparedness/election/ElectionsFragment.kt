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

    private lateinit var electionListAdapter: ElectionListAdapter

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

        electionListAdapter = ElectionListAdapter(
            ElectionClickListener { election ->
                this.findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        election.id,
                        election.division
                    )
                )
            }
        )

        binding.upcomingElectionsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.upcomingElectionsRv.adapter = electionListAdapter

        //TODO: Populate recycler adapters

        electionsViewModel.getElections()

        return binding.root
    }

    //TODO: Refresh adapters when fragment loads
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        electionsViewModel.upcomingElection.observe(
            viewLifecycleOwner,
            Observer<List<Election>> { elections ->
                elections?.apply {
                    electionListAdapter.submitList(elections)
                    electionListAdapter.notifyDataSetChanged()

                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}