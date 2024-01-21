package com.hellcorp.gpstrackerpet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hellcorp.gpstrackerpet.data.ConverterDB
import com.hellcorp.gpstrackerpet.data.MainDB
import com.hellcorp.gpstrackerpet.data.db.TrackItemEntity
import com.hellcorp.gpstrackerpet.domain.TrackItem
import com.hellcorp.gpstrackerpet.location.LocationModel
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class MainViewModel(db: MainDB, converterDB: ConverterDB): ViewModel() {
    val trackDAO = db.getDao()
    val converter = converterDB
    val locationUpdates = MutableLiveData<LocationModel>()
    val timeData = MutableLiveData<String>()
    val trackList = trackDAO.getTrackList().asLiveData()

    class VMFactory(private val db: MainDB, private val converterDb: ConverterDB) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(db, converterDb) as T
            }
            throw  IllegalArgumentException("MainViewModel: Unknow ViewModel class")
        }
    }

    fun saveTrackToDb(trackItem: TrackItem) {
        viewModelScope.launch {
            trackDAO.insertTrack(converter.map(trackItem))
        }
    }
}