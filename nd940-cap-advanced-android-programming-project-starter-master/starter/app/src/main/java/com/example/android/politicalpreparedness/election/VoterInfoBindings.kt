package com.example.android.politicalpreparedness.election

import android.widget.Button
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R

@BindingAdapter("app:followButtonState")
fun setButtonState(button: Button, follow: Boolean) {
    if (follow) {
        button.text = button.context.getString(R.string.follow_election)
    } else {
        button.text = button.context.getString(R.string.unfollow_election)
    }
}