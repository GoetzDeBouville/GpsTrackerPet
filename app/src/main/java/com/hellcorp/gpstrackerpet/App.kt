package com.hellcorp.gpstrackerpet

import android.app.Application
import com.hellcorp.gpstrackerpet.data.MainDB

class App : Application() {
    val database by lazy { MainDB.getDatabase(this) }
}