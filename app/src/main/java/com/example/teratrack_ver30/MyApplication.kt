
package com.example.teratrack_ver30

import android.app.Application
import org.osmdroid.config.Configuration
import java.io.File

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = Configuration.getInstance()

        // Identify the application to OpenStreetMap.
        config.userAgentValue =
            "TeraTrack/1.0 (com.example.teratrack_ver30)"

        // Set the map tile cache directory.
        val osmDir = File(getExternalFilesDir(null), "osmdroid")

        if (!osmDir.exists()) {
            osmDir.mkdirs()
        }

        config.osmdroidBasePath = osmDir
        config.osmdroidTileCache = File(osmDir, "tiles")
    }
}
