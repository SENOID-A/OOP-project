
package com.example.teratrack_ver30

import android.app.Application
import org.osmdroid.config.Configuration
import java.io.File

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = Configuration.getInstance()

        // Identify TeraTrack to OpenStreetMap.
        config.userAgentValue =
            "TeraTrack/1.0 (com.example.teratrack_ver30)"

        // Add the Referer header for the tile requests.
        config.additionalHttpRequestProperties["Referer"] =
            "https://github.com/SENOID-A/OOP-project/"

        // Configure map tile storage.
        val osmDir = File(
            getExternalFilesDir(null),
            "osmdroid"
        )

        if (!osmDir.exists()) {
            osmDir.mkdirs()
        }

        config.osmdroidBasePath = osmDir
        config.osmdroidTileCache = File(osmDir, "tiles")
    }
}
