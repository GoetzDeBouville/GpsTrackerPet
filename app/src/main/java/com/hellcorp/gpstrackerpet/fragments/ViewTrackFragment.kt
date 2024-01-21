package com.hellcorp.gpstrackerpet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hellcorp.gpstrackerpet.databinding.FragmentViewTrackBinding

class ViewTrackFragment : Fragment() {
    private var _bindng: FragmentViewTrackBinding? = null
    private val binding get() = _bindng!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bindng = FragmentViewTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ViewTrackFragment().apply {

            }
    }
}