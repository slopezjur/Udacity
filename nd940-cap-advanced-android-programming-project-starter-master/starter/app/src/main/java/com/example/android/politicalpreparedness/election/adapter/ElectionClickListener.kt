package com.example.android.politicalpreparedness.election.adapter

import com.example.android.politicalpreparedness.network.models.Election

class ElectionClickListener(val clickListener: (election: Election) -> Unit) {
    fun onClickListener(election: Election) = clickListener(election)
}