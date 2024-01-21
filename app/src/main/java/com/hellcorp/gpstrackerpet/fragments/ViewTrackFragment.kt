package com.hellcorp.gpstrackerpet.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hellcorp.gpstrackerpet.App
import com.hellcorp.gpstrackerpet.MainViewModel
import com.hellcorp.gpstrackerpet.data.ConverterDB
import com.hellcorp.gpstrackerpet.databinding.FragmentViewTrackBinding
import com.hellcorp.gpstrackerpet.fragments.adapters.TrackAdapter

class ViewTrackFragment : Fragment() {
    private var _bindng : FragmentViewTrackBinding? = null
    private val binding get() = _bindng!!
    private lateinit var adapter: TrackAdapter
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.VMFactory((requireContext().applicationContext as App).database, ConverterDB())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bindng = FragmentViewTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.trackList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        initAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        _bindng = null
    }

    private fun initAdapter() = with(binding) {
        adapter = TrackAdapter()
        rvTracklist.layoutManager = LinearLayoutManager(requireContext())
        rvTracklist.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = ViewTrackFragment()
    }
}