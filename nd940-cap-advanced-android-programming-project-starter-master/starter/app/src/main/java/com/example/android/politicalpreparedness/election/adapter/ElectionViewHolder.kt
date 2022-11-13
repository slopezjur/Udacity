package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ViewholderElectionBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionViewHolder(private val viewHolderElectionBinding: ViewholderElectionBinding) :
    RecyclerView.ViewHolder(viewHolderElectionBinding.root) {

    fun bind(listener: ElectionClickListener, election: Election) {
        viewHolderElectionBinding.election = election
        viewHolderElectionBinding.clickListener = listener
        viewHolderElectionBinding.executePendingBindings()
    }

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