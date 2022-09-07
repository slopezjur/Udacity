package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.AsteroidItemBinding

class AsteroidAdapter(
    private val callback: AsteroidClick
) : RecyclerView.Adapter<AsteroidViewHolder>() {

    var asteroids: List<Asteroid> = emptyList()
        set(value) {
            field = value
            // TODO : Improve this horrible thing
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        val withDataBinding: AsteroidItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            AsteroidViewHolder.LAYOUT,
            parent,
            false
        )
        return AsteroidViewHolder(withDataBinding)
    }

    override fun getItemCount() = asteroids.size

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.asteroid = asteroids[position]
            it.asteroidCallBack = callback
        }
    }
}