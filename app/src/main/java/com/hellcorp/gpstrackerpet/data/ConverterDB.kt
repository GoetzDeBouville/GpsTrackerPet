package com.hellcorp.gpstrackerpet.data

import com.hellcorp.gpstrackerpet.data.db.TrackItemEntity
import com.hellcorp.gpstrackerpet.domain.TrackItem

class ConverterDB {
    fun map(track: TrackItem) = TrackItemEntity(
        id = track.id,
        time = track.time,
        date = track.date,
        distance = track.distance,
        averageSpeed = track.averageSpeed,
        geopoint = track.geopoints
    )

    fun map(track: TrackItemEntity) = TrackItem(
        id = track.id,
        time = track.time,
        date = track.date,
        distance = track.distance,
        averageSpeed = track.averageSpeed,
        geopoints = track.geopoint
    )
}