package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsRepository
import com.example.android.politicalpreparedness.network.ResultState
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

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean>
        get() = _showLoading

    private val _showError = MutableLiveData<String>()
    val showError: LiveData<String>
        get() = _showError

    fun getElections() {
        getResultState(ResultState.Loading)
        viewModelScope.launch {
            getResultState(civicsRepository.getElectionsResult())
        }
    }

    private fun getResultState(resultState: ResultState<List<Election>>) {
        when (resultState) {
            is ResultState.Success -> {
                _upcomingElections.value = resultState.data
                _showLoading.value = false
            }
            is ResultState.Error -> {
                _showLoading.value = false
                _showError.value = resultState.exception.message
            }
            ResultState.Loading -> {
                _showLoading.value = true
            }
        }
    }

    fun getSavedElections() {
        viewModelScope.launch {
            _upcomingSavedElections.value = electionDao.getAllElections()
        }
    }
}