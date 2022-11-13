package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsRepository
import com.example.android.politicalpreparedness.network.ResultState
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val civicsRepository: CivicsRepository,
    private val electionDao: ElectionDao
) : ViewModel() {

    private val _voterInfoResponse = MutableLiveData<VoterInfoResponse>()
    val voterInfoResponse: LiveData<VoterInfoResponse>
        get() = _voterInfoResponse

    private val _followButtonState = MutableLiveData<Boolean>()
    val followButtonState: LiveData<Boolean>
        get() = _followButtonState

    private var election: Election? = null
    var votingLocationFinderUrl: String? = null
    var ballotInfoUrl: String? = null

    fun getVoterInfo(voterInfoDto: VoterInfoDto) {
        viewModelScope.launch {
            val resultState = civicsRepository.getVoterInfo(voterInfoDto)
            getResultState(resultState)

            // TODO : Don't really like this...
            getElection(voterInfoDto)
        }
    }

    private fun getResultState(resultState: ResultState<VoterInfoResponse>) {
        when (resultState) {
            is ResultState.Success -> {
                _voterInfoResponse.value = resultState.data
                setupUrls()
            }
            is ResultState.Error -> {
                // do nothing
            }
            ResultState.Loading -> {
                // do nothing
            }
        }
    }

    private fun setupUrls() {
        votingLocationFinderUrl =
            _voterInfoResponse.value?.state?.firstOrNull()?.electionAdministrationBody?.votingLocationFinderUrl
        ballotInfoUrl =
            _voterInfoResponse.value?.state?.firstOrNull()?.electionAdministrationBody?.ballotInfoUrl
    }

    private suspend fun getElection(voterInfoDto: VoterInfoDto) {
        election = electionDao.getElectionById(voterInfoDto.electionId)

        if (election != null) {
            _followButtonState.value = false
        } else {
            _followButtonState.value = true
            election = civicsRepository.getElections().firstOrNull {
                it.id == voterInfoDto.electionId
            }
        }
    }

    fun setFollowButtonState() {
        if (_followButtonState.value == false) {
            _followButtonState.value = true
            removeElection()
        } else {
            _followButtonState.value = false
            saveElection()
        }
    }

    private fun saveElection() {
        viewModelScope.launch {
            election?.let {
                electionDao.insertElection(it)
            }
        }
    }

    private fun removeElection() {
        viewModelScope.launch {
            election?.let {
                electionDao.deleteElectionById(it.id)
            }
        }
    }
}