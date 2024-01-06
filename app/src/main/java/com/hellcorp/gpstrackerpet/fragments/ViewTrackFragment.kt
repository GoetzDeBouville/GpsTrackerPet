package com.hellcorp.gpstrackerpet.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellcorp.gpstrackerpet.databinding.FragmentViewTrackBinding

class ViewTrackFragment : Fragment() {
    private var _bindng : FragmentViewTrackBinding? = null
    private val binding get() = _bindng!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bindng = FragmentViewTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _bindng = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ViewTrackFragment()
    }
}