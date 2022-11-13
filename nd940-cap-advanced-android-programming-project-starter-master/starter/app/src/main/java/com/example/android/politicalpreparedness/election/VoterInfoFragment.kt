package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.CivicsRepository
import com.google.android.material.snackbar.Snackbar

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

        binding.lifecycleOwner = this
        binding.voterInfoViewModel = voterInfoViewModel

        arguments?.let {

            val division = VoterInfoFragmentArgs.fromBundle(it).argDivision
            val electionId = VoterInfoFragmentArgs.fromBundle(it).argElectionId

            voterInfoViewModel.getVoterInfo(
                VoterInfoDto(
                    address = "${division.country}, ${division.state}",
                    electionId = electionId
                )
            )

            binding.stateLocations.setOnClickListener {
                voterInfoViewModel.votingLocationFinderUrl?.let { url ->
                    openUrl(url)
                }
            }
            binding.stateBallot.setOnClickListener {
                voterInfoViewModel.ballotInfoUrl?.let { url ->
                    openUrl(url)
                }
            }
        }

        return binding.root
    }

    private fun openUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        ContextCompat.startActivity(requireContext(), browserIntent, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        voterInfoViewModel.showLoading.observe(
            viewLifecycleOwner,
            Observer<Boolean> { showLoading ->
                if (showLoading) {
                    binding.voterInfoLoading.visibility = View.VISIBLE
                } else {
                    binding.voterInfoLoading.visibility = View.GONE
                }
            })

        voterInfoViewModel.showError.observe(
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