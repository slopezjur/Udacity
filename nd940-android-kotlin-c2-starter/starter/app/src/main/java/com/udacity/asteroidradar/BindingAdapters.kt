package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.udacity.asteroidradar.api.NasaApiStatus

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    val context = imageView.context
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
        imageView.contentDescription =
            context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
        imageView.contentDescription = context.getString(R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("nasaApiStatus")
fun bindStatus(circularProgressIndicator: CircularProgressIndicator, status: NasaApiStatus?) {
    when (status) {
        NasaApiStatus.LOADING -> {
            circularProgressIndicator.visibility = View.VISIBLE
        }
        NasaApiStatus.ERROR -> {
            circularProgressIndicator.visibility = View.GONE
        }
        NasaApiStatus.DONE -> {
            circularProgressIndicator.visibility = View.GONE
        }
        else -> {
            circularProgressIndicator.visibility = View.GONE
        }
    }
}

@BindingAdapter("pictureOfTheDayDescription")
fun bindDetailsStatusImage(imageView: ImageView, contentDescription: String?) {
    val context = imageView.context
    imageView.contentDescription = context.getString(
        R.string.nasa_picture_of_day_content_description_format,
        contentDescription
    )
}

@BindingAdapter("pictureOfTheDayTitle")
fun bindDetailsStatusImage(textView: TextView, title: String?) {
    val context = textView.context
    title?.let {
        textView.text = context.getString(
            R.string.nasa_picture_of_day_content_description_format,
            it
        )
    }
}
