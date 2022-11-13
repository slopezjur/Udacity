package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsRepository
import com.example.android.politicalpreparedness.network.ResultState
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(
    private val civicsRepository: CivicsRepository
) : ViewModel() {

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _userRequestLocationSearch = MutableLiveData<Boolean>()
    val userRequestLocationSearch: LiveData<Boolean>
        get() = _userRequestLocationSearch

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean>
        get() = _showLoading

    private val _showError = MutableLiveData<String>()
    val showError: LiveData<String>
        get() = _showError

    fun getRepresentativesByAddress() {
        getResultState(ResultState.Loading)
        viewModelScope.launch {
            address.value?.let {
                val resultState =
                    civicsRepository.getRepresentatives(address = it.toFormattedString())
                getResultState(resultState)
            }
        }
    }

    private fun getResultState(resultState: ResultState<RepresentativeResponse>) {
        when (resultState) {
            is ResultState.Success -> {
                val (offices, officials) = resultState.data
                _representatives.value =
                    offices.flatMap { office -> office.getRepresentatives(officials) }
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

    fun setAddressFromGeoLocation(address: Address) {
        _address.value = address
        setUserRequestLocationSearch(true)
    }

    fun setUserRequestLocationSearch(userRequestLocationSearch: Boolean) {
        _userRequestLocationSearch.value = userRequestLocationSearch
    }
}
