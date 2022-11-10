package com.example.android.politicalpreparedness.election

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

@BindingAdapter("app:followButtonState")
fun bindButtonState(button: Button, follow: Boolean) {
    if (follow) {
        button.text = button.context.getString(R.string.follow_election)
    } else {
        button.text = button.context.getString(R.string.unfollow_election)
    }
}

@BindingAdapter("app:setAddress")
fun bindVoterInfoAddress(textView: TextView, voterInfoResponse: VoterInfoResponse?) {
    voterInfoResponse?.state?.firstOrNull()?.let {
        textView.text = it.electionAdministrationBody.correspondenceAddress?.toFormattedString()
    }
}

@BindingAdapter("app:setGroupVisibility")
fun bindGroupVisibility(group: Group, voterInfoResponse: VoterInfoResponse?) {
    if (voterInfoResponse?.state?.firstOrNull() != null) {
        group.visibility = View.VISIBLE
    } else {
        group.visibility = View.INVISIBLE
    }
}