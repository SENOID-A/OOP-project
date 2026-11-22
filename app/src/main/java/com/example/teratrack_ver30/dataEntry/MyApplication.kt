package com.example.teratrack_ver30.dataEntry

import android.app.Application
import org.osmdroid.config.Configuration
import java.io.File

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = Configuration.getInstance()
        config.isDebugMode = true
        config.isDebugMapTileDownloader = true
        
        // Clearly identify the TeraTrack Android application.
        config.userAgentValue =
            "TeraTrack/1.0 (+https://github.com/SENOID-A/OOP-project)"

        // Configure OSMDroid tile storage.
        val osmDir = File(
            getExternalFilesDir(null),
            "osmdroid"
        )

        if (!osmDir.exists()) {
            osmDir.mkdirs()
        }

        val tileCache = File(osmDir, "tiles")

        if (!tileCache.exists()) {
            tileCache.mkdirs()
        }

        config.osmdroidBasePath = osmDir
        config.osmdroidTileCache = tileCache
    }
}
