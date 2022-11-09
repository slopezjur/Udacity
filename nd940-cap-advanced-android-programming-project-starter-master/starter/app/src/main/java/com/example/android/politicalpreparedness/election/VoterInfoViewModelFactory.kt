package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsRepository

class VoterInfoViewModelFactory(
    private val civicsRepository: CivicsRepository,
    private val electionDao: ElectionDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VoterInfoViewModel(civicsRepository, electionDao) as T
    }
}