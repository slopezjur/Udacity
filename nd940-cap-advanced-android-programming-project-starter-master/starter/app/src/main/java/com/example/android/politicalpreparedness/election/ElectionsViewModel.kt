package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsRepository
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(
    private val civicsRepository: CivicsRepository,
    private val electionDao: ElectionDao
) : ViewModel() {

    //TODO: Create live data val for upcoming elections
    private val _upcomingElection = MutableLiveData<List<Election>>()
    val upcomingElection: LiveData<List<Election>>
        get() = _upcomingElection

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    fun getElections() {
        viewModelScope.launch {
            _upcomingElection.value = civicsRepository.getElections()
        }
    }

    fun navigateToElectionDetail(election: Election) {
        TODO("Not yet implemented")
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info

}