package app

import android.app.Application

// Runs once when the app process starts. Screens reach the services through
// (applicationContext as FleetApp).container
class FleetApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        SeedData.seedIfEmpty(container)
    }
}
