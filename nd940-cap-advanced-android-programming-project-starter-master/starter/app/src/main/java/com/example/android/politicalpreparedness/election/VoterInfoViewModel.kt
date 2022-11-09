package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsRepository
import com.example.android.politicalpreparedness.network.ResultState
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val civicsRepository: CivicsRepository,
    private val electionDao: ElectionDao
) : ViewModel() {

    private val _voterInfoResponse = MutableLiveData<VoterInfoResponse>()
    val voterInfoResponse: LiveData<VoterInfoResponse>
        get() = _voterInfoResponse

    //TODO: Add var and methods to populate voter info
    fun getVoterInfo(voterInfoDto: VoterInfoDto) {
        viewModelScope.launch {
            val resultState = civicsRepository.getVoterinfo(voterInfoDto)
            getResultState(resultState)

        }
    }

    private fun getResultState(resultState: ResultState<VoterInfoResponse>) {
        when (resultState) {
            is ResultState.Success -> {
                _voterInfoResponse.value = resultState.data
            }
            is ResultState.Error -> {
                // do nothing
            }
            ResultState.Loading -> {
                // do nothing
            }
        }
    }
    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}