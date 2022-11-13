package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.election.VoterInfoDto
import com.example.android.politicalpreparedness.network.ResultState.Error
import com.example.android.politicalpreparedness.network.ResultState.Success
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

class CivicsRepository {

    suspend fun getElections(): List<Election> {
        val result = CivicsApi.retrofitService.getElections()
        return if (result.isSuccessful && result.body() != null) {
            val body = result.body()
            body?.elections ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun getElectionsResult(): ResultState<List<Election>> {
        val elections = getElections()

        return if (elections.isNotEmpty()) {
            Success(elections)
        } else {
            Error(Exception())
        }
    }

    suspend fun getVoterInfo(voterInfoDto: VoterInfoDto): ResultState<VoterInfoResponse> {
        val result = CivicsApi.retrofitService.getVoterInfo(
            // TODO : Review address - Testing purpose due to a problem when we try to get the address
            //  from the previous API response. API expects a valid parsed address or it will always
            //  return a 400 bad request if the is not a valid address
            "1263 Pacific Ave. Kansas City KS",
            //voterInfoDto.address,
            voterInfoDto.electionId
        )

        return if (result.isSuccessful && result.body() != null) {
            result.body()?.let {
                Success(it)
            } ?: Error(Exception(result.errorBody()?.charStream()?.readText()))
        } else {
            Error(Exception(result.errorBody()?.charStream()?.readText()))
        }
    }

    suspend fun getRepresentatives(address: String): ResultState<RepresentativeResponse> {
        val result = CivicsApi.retrofitService.getRepresentatives(address)
        return if (result.isSuccessful && result.body() != null) {
            result.body()?.let {
                Success(it)
            } ?: Error(Exception(result.errorBody()?.charStream()?.readText()))
        } else {
            Error(Exception(result.errorBody()?.charStream()?.readText()))
        }
    }
}