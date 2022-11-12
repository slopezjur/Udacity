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

    //TODO: Create function to fetch representatives from API from a provided address
    fun getRepresentativesByAddress() {
        viewModelScope.launch {

            // TODO - User location data
            _address.value = Address(
                line1 = "Amphitheatre Parkway",
                line2 = "1600",
                city = "Mountain View",
                state = "California",
                zip = "94043"
            )

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
            }
            is ResultState.Error -> {
                // do nothing
            }
            ResultState.Loading -> {
                // do nothing
            }
        }
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields
}
