package com.hellcorp.gpstrackerpet.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.hellcorp.gpstrackerpet.R
import com.hellcorp.gpstrackerpet.databinding.FragmentMainBinding
import com.hellcorp.gpstrackerpet.location.LocationService
import com.hellcorp.gpstrackerpet.utils.DialogManager
import com.hellcorp.gpstrackerpet.utils.TimeUtils
import com.hellcorp.gpstrackerpet.utils.checkPermission
import com.hellcorp.gpstrackerpet.utils.showSnackbar
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Timer
import java.util.TimerTask

class MainFragment : Fragment() {
    private var isServiceLocationEnabled = false
    private var timer: Timer? = null
    private var startTime = 0L
    private val timeData = MutableLiveData<String>()
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
        timeData.observe(viewLifecycleOwner) {
            binding.tvTime.text = it
        }
    }

    private fun startTimer() {
        timer?.cancel()
        timer = Timer()
        startTime = LocationService.startTime
        timer?.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    timeData.value = getCurrentTime()
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

    private fun startStopService() {
        if (isServiceLocationEnabled) {
            activity?.stopService(Intent(activity, LocationService::class.java))
            binding.btnStartStopTrack.setImageResource(R.drawable.ic_start_track_24)
            stopTimer()
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
        map.controller.setZoom(20.0)
        val locationProvider = GpsMyLocationProvider(activity)
        val locationOverlay = MyLocationNewOverlay(locationProvider, map)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        locationOverlay.runOnFirstFix {
            map.overlays.clear()
            map.overlays.add(locationOverlay)
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

    companion object {
        private const val OSM_KEY = "osm_pref"

        @JvmStatic
        fun newInstance() = MainFragment()
    }
}