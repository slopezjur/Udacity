package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment(), MenuProvider {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private var asteroidAdapter: AsteroidAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        asteroidAdapter = AsteroidAdapter(
            AsteroidClick {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
            }
        )

        binding.asteroidRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = asteroidAdapter
        }

        setupImageOfTheDay(binding)
        setupAsteroids()

        return binding.root
    }

    private fun setupImageOfTheDay(binding: FragmentMainBinding) {
        viewModel.imageOfTheDay.observe(viewLifecycleOwner) { imageOfTheDayDatabase ->
            imageOfTheDayDatabase?.let {
                val imageOfTheDay = it.asDomainModel()
                Picasso.with(context)
                    .load(imageOfTheDay.url)
                    .placeholder(R.drawable.placeholder_picture_of_day)
                    .error(R.drawable.ic_connection_error)
                    .into(binding.activityMainImageOfTheDay)
                binding.activityMainImageOfTheDay.contentDescription = context?.getString(
                    R.string.nasa_picture_of_day_content_description_format,
                    imageOfTheDay.title
                )
            }
        }
    }

    private fun setupAsteroids() {
        viewModel.asteroids.observe(viewLifecycleOwner) { asteroidDatabase ->
            asteroidDatabase?.let {
                asteroidAdapter?.asteroids = it.asDomainModel()
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_overflow_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return true
    }
}
