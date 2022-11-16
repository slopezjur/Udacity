package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private lateinit var geofencingClient: GeofencingClient

    private var userRequestLocation = false

    // A PendingIntent for the Broadcast Receiver that handles geofence transitions.
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        // Use FLAG_UPDATE_CURRENT so that you get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.saveReminder.setOnClickListener {
            if (checkLocationPermissions()) {
                val reminderDataItem = getReminderDataItem()

                _viewModel.validateAndSaveReminder(
                    reminderDataItem
                )

                if (!reminderDataItem.title.isNullOrEmpty() && !reminderDataItem.location.isNullOrEmpty()) {
                    addGeofence(reminderDataItem)
                }
            }
        }

        geofencingClient = LocationServices.getGeofencingClient(requireActivity())
    }

    private fun getReminderDataItem(): ReminderDataItem {
        val title = _viewModel.reminderTitle.value
        val description = _viewModel.reminderDescription.value
        val location = _viewModel.reminderSelectedLocationStr.value
        val latitude = _viewModel.latitude.value
        val longitude = _viewModel.longitude.value

        return ReminderDataItem(
            title = title,
            description = description,
            location = location,
            latitude = latitude,
            longitude = longitude
        )
    }

    // --------------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------
    // LOCATION THING
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            addGeofence(getReminderDataItem())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
            grantResults[LOCATION_BACKGROUND_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED
        ) {
            Snackbar.make(
                binding.root,
                getString(R.string.grant_location_background_permission),
                Snackbar.LENGTH_LONG
            ).setAction(getString(R.string.settings)) {
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }.show()
        } else {
            addGeofence(getReminderDataItem())
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                REQUEST_LOCATION_RESULT_CODE
            )
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        return (PackageManager.PERMISSION_GRANTED == requireContext().checkSelfPermission(
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && PackageManager.PERMISSION_GRANTED == requireContext().checkSelfPermission(
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ))
    }

    private fun addGeofence(reminderDataItem: ReminderDataItem) {
        // Build the Geofence Object
        val geofence = Geofence.Builder()
            // Set the request ID, string to identify the geofence.
            .setRequestId(reminderDataItem.id)
            // Set the circular region of this geofence.
            .setCircularRegion(
                // TODO : check values
                reminderDataItem.latitude ?: 0.0,
                reminderDataItem.longitude ?: 0.0,
                GEOFENCE_RADIUS_IN_METERS
            )
            // Set the expiration duration of the geofence. This geofence gets
            // automatically removed after this period of time.
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            // Set the transition types of interest. Alerts are only generated for these
            // transition. We track entry and exit transitions in this sample.
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        // Build the geofence request
        val geofencingRequest = GeofencingRequest.Builder()
            // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
            // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
            // is already inside that geofence.
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)

            // Add the geofences to be monitored by geofencing service.
            .addGeofence(geofence)
            .build()

        // Add the new geofence request with the new geofence
        // TODO - We cannot directly use isPermissionGranted to avoid warning:
        //  "Call requires permission which may be rejected by user: code should explicitly
        //  check to see if permission is available (with checkPermission) or explicitly
        //  handle a potential SecurityException"
        if (PackageManager.PERMISSION_GRANTED == requireContext().checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) && PackageManager.PERMISSION_GRANTED == requireContext().checkSelfPermission(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ) {
            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
                addOnSuccessListener {
                    Log.e("Add Geofence", geofence.requestId)
                }
                addOnFailureListener {
                    // Failed to add geofences.
                    Toast.makeText(
                        binding.root.context, R.string.geofences_not_added,
                        Toast.LENGTH_SHORT
                    ).show()
                    if ((it.message != null)) {
                        Log.w(TAG, it.message.toString())
                    }
                }
            }
        } else {
            Toast.makeText(
                binding.root.context, R.string.grant_location_permission,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }

    companion object {
        private const val TAG = "SaveReminderFragment"
        const val GEOFENCE_RADIUS_IN_METERS = 100f
        internal const val ACTION_GEOFENCE_EVENT =
            "action.ACTION_GEOFENCE_EVENT"
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        private const val REQUEST_LOCATION_RESULT_CODE = 33
        private const val LOCATION_PERMISSION_INDEX = 0
        private const val LOCATION_BACKGROUND_PERMISSION_INDEX = 1
    }
}
