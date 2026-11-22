package com.example.teratrack_ver30.ui.theme

import android.app.Application
import org.osmdroid.config.Configuration
import java.io.File

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Unique User-Agent is REQUIRED by OpenStreetMap
        Configuration.getInstance().userAgentValue = "TeraTrack_StudentApp/1.0 (com.example.teratrack_ver30)"

        val osmDir = File(getExternalFilesDir(null), "osmdroid")
        if (!osmDir.exists()) osmDir.mkdirs()
        Configuration.getInstance().osmdroidBasePath = osmDir
        Configuration.getInstance().osmdroidTileCache = File(osmDir, "tiles")
    }
}