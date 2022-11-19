package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment() {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()

    private var _binding: FragmentSelectLocationBinding? = null
    private val binding get() = _binding!!

    private var _map: GoogleMap? = null
    private val map get() = _map!!
    private val TAG = SelectLocationFragment::class.java.simpleName

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        _map = googleMap
        val latitude = 40.417162967071626
        val longitude = -3.683910194746891

        val home = LatLng(latitude, longitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(home, ZOOM_LEVEL))
        map.uiSettings.isZoomControlsEnabled = true
        setMapLongClick(map)
        setPoiClick(map)
        setMapStyle(map)

        map.setOnMarkerClickListener {
            it.remove()
            clearReminderDataItem()
            true
        }
    }

    private lateinit var reminderDataItem: ReminderDataItem

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 60
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

        binding.saveLastReminder.setOnClickListener {
            onLocationSelected()
        }

        clearReminderDataItem()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun onLocationSelected() {
        binding.viewModel?.reminderSelectedLocationStr?.value = reminderDataItem.location
        binding.viewModel?.latitude?.value = reminderDataItem.latitude
        binding.viewModel?.longitude?.value = reminderDataItem.longitude
        binding.viewModel?.navigationCommand?.value = NavigationCommand.Back
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            super.onOptionsItemSelected(item)
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            super.onOptionsItemSelected(item)
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            super.onOptionsItemSelected(item)
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            super.onOptionsItemSelected(item)
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->

            map.clear()
            clearReminderDataItem()

            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
            )

            reminderDataItem = ReminderDataItem(
                null,
                null,
                getString(R.string.custom_marker),
                latLng.latitude,
                latLng.longitude
            )
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->

            map.clear()
            clearReminderDataItem()

            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker?.showInfoWindow()

            reminderDataItem = ReminderDataItem(
                null,
                null,
                poi.name,
                poi.latLng.latitude,
                poi.latLng.longitude
            )
        }
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun clearReminderDataItem() {
        reminderDataItem = ReminderDataItem(null, null, null, null, null)
    }

    override fun onDestroy() {
        _binding = null
        _map = null
        super.onDestroy()
    }

    // --------------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------
    // LOCATION THING
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // LocationCallback to manage when the user turn on-off GPS and get the latest Location
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val locationList = locationResult.locations
                if (locationList.isNotEmpty()) {
                    //The last location in the list is the newest
                    val lastLocation = locationList.last()
                    setUserLocationOnMap(lastLocation)

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
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
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
                getString(R.string.grant_location_permission),
                Snackbar.LENGTH_LONG
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
                map.isMyLocationEnabled = true

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
                    fusedLocationProviderClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            location?.let {
                                setUserLocationOnMap(location)
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
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun setUserLocationOnMap(lastLocation: Location) {
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(lastLocation.latitude, lastLocation.longitude),
                ZOOM_LEVEL
            )
        )
    }

    companion object {
        private const val ZOOM_LEVEL = 15f
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        private const val REQUEST_FOREGROUND_RESULT_CODE = 33
        private const val LOCATION_PERMISSION_INDEX = 0
    }
}