package com.hellcorp.gpstrackerpet.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellcorp.gpstrackerpet.databinding.FragmentMainBinding
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setOsm() // инициализация open street map обязательно до проприсовки фрагмента
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOsm()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setOsm() {
        Configuration.getInstance()
            .load(requireContext(), activity?.getSharedPreferences(OSM_KEY, Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = BuildConfig.LIBRARY_PACKAGE_NAME
    }

    private fun initOsm() = with(binding){
        map.controller.setZoom(20.0)
        map.controller.animateTo(GeoPoint(40.4167, -3.70325))
    }

    companion object {
        private const val OSM_KEY = "osm_pref"

        @JvmStatic
        fun newInstance() = MainFragment()
    }
}