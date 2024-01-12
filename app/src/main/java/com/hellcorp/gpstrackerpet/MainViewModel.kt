package com.hellcorp.gpstrackerpet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hellcorp.gpstrackerpet.location.LocationModel

class MainViewModel: ViewModel() {
    val locationUpdates = MutableLiveData<LocationModel>()
    val timeData = MutableLiveData<String>()
}