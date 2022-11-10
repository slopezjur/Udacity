package com.example.android.politicalpreparedness.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    // We always want to Smash that thing!
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElection(election: Election)

    @Query("SELECT * FROM election_table")
    suspend fun getAllElections(): List<Election>

    @Query("SELECT * FROM election_table WHERE id=:electionId")
    suspend fun getElectionById(electionId: Int): Election?

    @Query("DELETE FROM election_table WHERE id=:electionId")
    suspend fun deleteElectionById(electionId: Int)

    @Query("DELETE FROM election_table")
    suspend fun deleteAllElections()
}