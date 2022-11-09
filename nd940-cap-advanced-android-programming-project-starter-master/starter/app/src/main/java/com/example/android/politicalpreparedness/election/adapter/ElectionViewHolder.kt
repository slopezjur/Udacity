package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ViewholderElectionBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionViewHolder(private val viewholderElectionBinding: ViewholderElectionBinding) :
    RecyclerView.ViewHolder(viewholderElectionBinding.root) {

    fun bind(listener: ElectionListener, election: Election) {
        viewholderElectionBinding.election = election
        viewholderElectionBinding.clickListener = listener
        viewholderElectionBinding.executePendingBindings()
    }

    //TODO: Add companion object to inflate ViewHolder (from)
    companion object {
        fun from(parent: ViewGroup): ElectionViewHolder {
            val withDataBinding: ViewholderElectionBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.viewholder_election,
                parent,
                false
            )
            return ElectionViewHolder(withDataBinding)
        }
    }
}