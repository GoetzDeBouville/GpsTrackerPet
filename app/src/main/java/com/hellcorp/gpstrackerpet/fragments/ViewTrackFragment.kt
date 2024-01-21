package com.hellcorp.gpstrackerpet.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hellcorp.gpstrackerpet.App
import com.hellcorp.gpstrackerpet.MainViewModel
import com.hellcorp.gpstrackerpet.data.ConverterDB
import com.hellcorp.gpstrackerpet.databinding.FragmentViewTrackBinding
import com.hellcorp.gpstrackerpet.domain.TrackItem
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline

class ViewTrackFragment : Fragment() {
    private var _bindng: FragmentViewTrackBinding? = null
    private val binding get() = _bindng!!
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.VMFactory(
            (requireContext().applicationContext as App).database,
            ConverterDB()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setOsm()
        _bindng = FragmentViewTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.currentTrack.observe(viewLifecycleOwner) {
            fetchData(it)
        }
    }

    private fun setOsm() {
        Configuration.getInstance()
            .load(
                requireContext(),
                activity?.getSharedPreferences(MainFragment.OSM_KEY, Context.MODE_PRIVATE)
            )
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME
    }

    private fun fetchData(trackItem: TrackItem) = with(binding) {
        with(trackItem) {
            tvDate.text = date
            tvAverageVelocity.text = averageSpeed
            tvDistance.text = distance
            tvTime.text = time
        }
        val polyline = getPolyline(trackItem.geopoints)
        map.overlays.add(polyline)
        goToStartPosition(polyline.actualPoints[0])
    }

    private fun goToStartPosition(startPosition: GeoPoint) {
        binding.map.controller.zoomTo(18.0)
        binding.map.controller.animateTo(startPosition)
    }

    private fun getPolyline(geopoints: String) : Polyline {
        val polyline = Polyline()
        val list = geopoints.split("/")
        list.forEach {
            if (it.isNotEmpty()) {
                val point = it.split(";")
                polyline.addPoint(GeoPoint(point[0].toDouble(), point[1].toDouble()))
            }
        }
        return polyline
    }

    companion object {
        @JvmStatic
        fun newInstance() = ViewTrackFragment()
    }
}