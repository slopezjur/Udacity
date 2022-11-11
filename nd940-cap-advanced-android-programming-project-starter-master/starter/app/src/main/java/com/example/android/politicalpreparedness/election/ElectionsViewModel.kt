package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsRepository
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

class ElectionsViewModel(
    private val civicsRepository: CivicsRepository,
    private val electionDao: ElectionDao
) : ViewModel() {

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    private val _upcomingSavedElections = MutableLiveData<List<Election>>()
    val upcomingSavedElections: LiveData<List<Election>>
        get() = _upcomingSavedElections

    fun getElections() {
        viewModelScope.launch {
            _upcomingElections.value = civicsRepository.getElections()
        }
    }

    fun getSavedElections() {
        viewModelScope.launch {
            _upcomingSavedElections.value = electionDao.getAllElections()
        }
    }
}