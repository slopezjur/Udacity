package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.CivicsRepository

class VoterInfoFragment : Fragment() {

    private val voterInfoViewModel: VoterInfoViewModel by lazy {
        ViewModelProvider(
            this, VoterInfoViewModelFactory(
                CivicsRepository(),
                ElectionDatabase.getInstance(requireActivity().application).electionDao
            )
        ).get(VoterInfoViewModel::class.java)
    }

    private var _binding: FragmentVoterInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_voter_info, container, false
        )

        //TODO: Add binding values
        binding.lifecycleOwner = this
        binding.voterInfoViewModel = voterInfoViewModel

        arguments?.let {

            val division = VoterInfoFragmentArgs.fromBundle(it).argDivision
            val electionId = VoterInfoFragmentArgs.fromBundle(it).argElectionId

            //TODO: Populate voter info -- hide views without provided data.
            /**
            Hint: You will need to ensure proper data is provided from previous fragment.
             */
            voterInfoViewModel.getVoterInfo(
                VoterInfoDto(
                    address = "${division.country}, ${division.state}",
                    electionId = electionId
                )
            )


            //TODO: Handle loading of URLs

            //TODO: Handle save button UI state

        }

        //TODO: cont'd Handle save button clicks

        return binding.root
    }

    //TODO: Create method to load URL intents

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}