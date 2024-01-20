package com.hellcorp.gpstrackerpet.fragments

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.hellcorp.gpstrackerpet.MainViewModel
import com.hellcorp.gpstrackerpet.R
import com.hellcorp.gpstrackerpet.databinding.FragmentMainBinding
import com.hellcorp.gpstrackerpet.domain.TrackItem
import com.hellcorp.gpstrackerpet.location.LocationModel
import com.hellcorp.gpstrackerpet.location.LocationService
import com.hellcorp.gpstrackerpet.utils.DialogManager
import com.hellcorp.gpstrackerpet.utils.TimeUtils
import com.hellcorp.gpstrackerpet.utils.checkPermission
import com.hellcorp.gpstrackerpet.utils.showSnackbar
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Timer
import java.util.TimerTask

class MainFragment : Fragment() {
    private var trackItem: TrackItem? = null
    private var polyline: Polyline? = null
    private var trackIsDrawned = false
    private var isServiceLocationEnabled = false
    private var timer: Timer? = null
    private var startTime = 0L
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setOsm() // инициализация open street map обязательно до прорисовки фрагмента
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        checkLocationPermission()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerPermissions()
        setOnClickListener()
        checkServiceState()
        updateTimeTV()
        registerLocReciever()
        updateLocation()
    }

    override fun onDetach() {
        super.onDetach()
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .unregisterReceiver(broadcastReceiver)
    }

    private fun setOnClickListener() = with(binding) {
        val listener = onClickListener()
        btnCenter.setOnClickListener(listener)
        btnStartStopTrack.setOnClickListener(listener)
    }

    private fun onClickListener() = View.OnClickListener {
        when (it) {
            binding.btnCenter -> Log.i("MyLog", "btnCenter clicked")
            binding.btnStartStopTrack -> startStopService()
        }
    }

    private fun updateTimeTV() {
        viewModel.timeData.observe(viewLifecycleOwner) {
            binding.tvTime.text = it
        }
    }

    private fun updateLocation() = with(binding) {
        viewModel.locationUpdates.observe(viewLifecycleOwner) {
            tvDistance.text = formatDistance(it.distance)
            tvCurrentVelocity.text =
                getString(R.string.speed, String.format("%.1f", it.velocity * 3.6))
            tvAverageVelocity.text = getString(R.string.average_speed, getAverageSpeed(it.distance))
            trackItem = TrackItem(
                null,
                getCurrentTime(),
                TimeUtils.getCurrentDate(),
                formatDistanceShortString(it.distance),
                getAverageSpeed(it.distance) + " km/h",
                ""
            )
            updatePolyline(it.geoPointList)
        }
    }

    private fun formatDistance(distance: Float): String {
        val distanceFormat = if (distance < 1000f) {
            getString(R.string.distance_meter, String.format("%.1f", distance))
        } else {
            getString(R.string.distance_kilometer, String.format("%.1f", distance / 1000))
        }

        return distanceFormat
    }

    private fun formatDistanceShortString(distance: Float): String {
        val distanceFormat = if (distance < 1000f) {
            String.format("%.1f", distance) + " m"
        } else {
            String.format("%.1f", distance / 1000) + " km"
        }
        return distanceFormat
    }


    private fun startTimer() {
        timer?.cancel()
        timer = Timer()
        startTime = LocationService.startTime
        timer?.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    viewModel.timeData.value = getCurrentTime()
                }
            }
        }, 10, 10)
    }

    private fun stopTimer() {
        timer?.cancel()
    }

    private fun getCurrentTime(): String {
        return "Time: ${TimeUtils.getTime(System.currentTimeMillis() - startTime)}"
    }

    private fun getAverageSpeed(distance: Float) = String.format(
        "%.1f",
        3.6f * (distance / ((System.currentTimeMillis() - startTime) / 1000f))
    )


    private fun startStopService() {
        if (isServiceLocationEnabled) {
            activity?.stopService(Intent(activity, LocationService::class.java))
            binding.btnStartStopTrack.setImageResource(R.drawable.ic_start_track_24)
            stopTimer()
            DialogManager.showSaveTrackDialog(requireContext(),
                trackItem,
                binding.root,
                object : DialogManager.Listener {
                    override fun onClick() {
                        showSnackbar(binding.root, "Track saved", requireContext())
                    }
                })
        } else {
            binding.btnStartStopTrack.setImageResource(R.drawable.ic_pause_track_24)
            startLocationService()
            LocationService.startTime = System.currentTimeMillis()
            startTimer()
        }
        isServiceLocationEnabled = !isServiceLocationEnabled
    }

    private fun startLocationService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(Intent(activity, LocationService::class.java))
        } else {
            activity?.startService((Intent(activity, LocationService::class.java)))
        }
    }

    private fun checkServiceState() {
        isServiceLocationEnabled = LocationService.isRuning

        if (isServiceLocationEnabled) {
            binding.btnStartStopTrack.setImageResource(R.drawable.ic_pause_track_24)
            startTimer()
        }
    }

    private fun setOsm() {
        Configuration.getInstance()
            .load(requireContext(), activity?.getSharedPreferences(OSM_KEY, Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME
    }

    private fun initOsm() = with(binding) {
        polyline = Polyline()
        polyline?.outlinePaint?.color = Color.BLUE
        map.controller.setZoom(20.0)
        val locationProvider = GpsMyLocationProvider(activity)
        val locationOverlay = MyLocationNewOverlay(locationProvider, map)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        locationOverlay.runOnFirstFix {
            map.overlays.clear()
            map.overlays.add(locationOverlay)
            map.overlays.add(polyline)
        }
    }

    private fun addTrackPoint(list: List<GeoPoint>) {
        polyline?.addPoint(list.last())
    }

    private fun drawTrackLine(list: List<GeoPoint>) {
        list.forEach {
            polyline?.addPoint(it)
        }
    }

    private fun updatePolyline(list: List<GeoPoint>) {
        if (list.size > 1 && !trackIsDrawned) {
            drawTrackLine(list)
            trackIsDrawned = true
        } else {
            addTrackPoint(list)
        }
    }

    private fun registerPermissions() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                    initOsm()
                } else {
                    showSnackbar(binding.map, "No location access permission!", requireContext())
                }
            }
    }

    private fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkLocationPermissionVersionQPlus()
        } else {
            checkLocationPermissionBelowVersionQ()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkLocationPermissionVersionQPlus() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            initOsm()
            checkLocationEnabled()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        }
    }

    private fun checkLocationPermissionBelowVersionQ() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            initOsm()
            checkLocationEnabled()
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            )
        }
    }

    private fun checkLocationEnabled() {
        val locationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isEnabled) {
            DialogManager.showLocationEnableDialog(requireContext(),
                object : DialogManager.Listener {
                    override fun onClick() {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                })
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == LocationService.LOCATION_MODEL_INTENT) {
                val locationModelJson = intent.getStringExtra(LocationService.LOCATION_MODEL_INTENT)
                val gson = Gson()
                val locationModel = gson.fromJson(locationModelJson, LocationModel::class.java)
                viewModel.locationUpdates.value = locationModel
            }
        }
    }

    private fun registerLocReciever() {
        val intentFilter = IntentFilter(LocationService.LOCATION_MODEL_INTENT)
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .registerReceiver(broadcastReceiver, intentFilter)
    }

    companion object {
        private const val OSM_KEY = "osm_pref"

        @JvmStatic
        fun newInstance() = MainFragment()
    }
}