package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.CivicsRepository
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.model.Representative
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import java.util.*

class DetailFragment : Fragment() {

    companion object {
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        private const val REQUEST_FOREGROUND_RESULT_CODE = 33
        private const val LOCATION_PERMISSION_INDEX = 0
    }

    private val representativeViewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(
            this, RepresentativesViewModelFactory(
                CivicsRepository()
            )
        ).get(RepresentativeViewModel::class.java)
    }

    private var _binding: FragmentRepresentativeBinding? = null
    private val binding get() = _binding!!

    private lateinit var representativeListAdapter: RepresentativeListAdapter

    private val locationRequest = LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 60
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var userRequestLocation = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_representative, container, false
        )

        binding.lifecycleOwner = this
        binding.representativeViewModel = representativeViewModel

        representativeListAdapter = RepresentativeListAdapter()

        binding.representativesRv.layoutManager = LinearLayoutManager(requireContext())
        binding.representativesRv.adapter = representativeListAdapter

        representativeViewModel.getRepresentativesByAddress()

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()

            representativeViewModel.getRepresentativesByAddress()
            if (representativeViewModel.address.value != null) {
                //check new address entered
                representativeViewModel.getRepresentativesByAddress()
            }
        }
        binding.buttonLocation.setOnClickListener {
            userRequestLocation = true
            getLocation()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        representativeViewModel.representatives.observe(
            viewLifecycleOwner,
            Observer<List<Representative>> { representatives ->
                representatives?.apply {
                    representativeListAdapter.submitList(representatives)
                }
            })

        representativeViewModel.userRequestLocationSearch.observe(
            viewLifecycleOwner,
            Observer<Boolean> { userRequestLocationSearch ->
                if (userRequestLocationSearch) {
                    representativeViewModel.setUserRequestLocationSearch(false)
                    representativeViewModel.getRepresentativesByAddress()
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val locationList = locationResult.locations
                if (locationList.isNotEmpty()) {
                    //The last location in the list is the newest
                    val lastLocation = locationList.last()
                    /*representativeViewModel.setAddressFromGeoLocation(
                        geoCodeLocation(
                            lastLocation
                        )
                    )*/
                    stopLocationUpdates()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkLocationPermissions()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() || grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED) {
            Snackbar.make(
                binding.root,
                R.string.grant_location_permission,
                Snackbar.LENGTH_INDEFINITE
            ).setAction(getString(R.string.settings)) {
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }.show()
        } else {
            getLocation()
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            getLocation()
            true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_RESULT_CODE
            )
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        return (PackageManager.PERMISSION_GRANTED == requireContext().checkSelfPermission(
            Manifest.permission.ACCESS_FINE_LOCATION
        ))
    }

    private fun getLocation() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                        requireActivity(),
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(
                        "LOCATION: ",
                        "Error getting location settings resolution: " + sendEx.message
                    )
                }
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.grant_location_permission), Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    getLocation()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                // TODO - We cannot directly use isPermissionGranted to avoid warning:
                //  "Call requires permission which may be rejected by user: code should explicitly
                //  check to see if permission is available (with checkPermission) or explicitly
                //  handle a potential SecurityException"
                if (requireContext().checkSelfPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    checkLocationPermissions()
                } else {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            location?.let {
                                if (userRequestLocation) {
                                    representativeViewModel.setAddressFromGeoLocation(
                                        geoCodeLocation(
                                            location
                                        )
                                    )
                                    userRequestLocation = false
                                }
                            } ?: getLocationFromLocationCallBack()
                        }
                }
            }
        }
    }

    private fun getLocationFromLocationCallBack() {
        if (requireContext().checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}