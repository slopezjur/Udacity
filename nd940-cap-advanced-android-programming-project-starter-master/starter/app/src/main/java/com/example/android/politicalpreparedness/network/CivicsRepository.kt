package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.election.VoterInfoDto
import com.example.android.politicalpreparedness.network.ResultState.Error
import com.example.android.politicalpreparedness.network.ResultState.Success
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import retrofit2.Response

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

    suspend fun getVoterinfo(voterInfoDto: VoterInfoDto): ResultState<VoterInfoResponse> {
        val result = CivicsApi.retrofitService.getVoterinfo(
            // TODO : Review address
            "1263 Pacific Ave. Kansas City KS", //voterInfoDto.address,
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

    suspend fun getVoterinfo(ocdId: String): Response<RepresentativeResponse> {
        TODO("Not yet implemented")
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