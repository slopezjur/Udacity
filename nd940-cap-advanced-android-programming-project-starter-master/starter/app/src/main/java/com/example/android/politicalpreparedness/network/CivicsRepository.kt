package com.example.android.politicalpreparedness.network

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

    suspend fun getVoterinfo(): Response<VoterInfoResponse> {
        TODO("Not yet implemented")
    }

    suspend fun getVoterinfo(ocdId: String): Response<RepresentativeResponse> {
        TODO("Not yet implemented")
    }

    suspend fun getRepresentatives(): Response<RepresentativeResponse> {
        TODO("Not yet implemented")
    }

}