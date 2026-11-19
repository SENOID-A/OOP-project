# Teratrack — XML UI refresh

This is a complete Android Studio project based on the supplied `OOP-project-android-development` backend, with a redesigned traditional Android Views/XML interface.

## Included
- Existing Kotlin domain entities, repositories, services, SQLite database, `AppContainer`, `FleetApp`, and seed data are retained.
- New XML screens: Overview, Fleet, Trips, Regional Map, and Maintenance.
- `FleetViewModel` is the UI-facing layer. `MainActivity` does not call repositories or services directly.
- RecyclerView adapters render existing vehicle, trip, and maintenance records.
- `CalabarzonMapView` uses Android Canvas and a lightweight latitude/longitude projection to plot the existing warehouse source and vehicle coordinates.
- Trip Start/Complete actions call the existing `TripService` through the ViewModel.

## Open in Android Studio
1. Extract this ZIP.
2. Open the extracted `Teratrack-Redesigned-XML` folder (the folder containing `settings.gradle.kts`).
3. Allow Gradle Sync to finish.
4. Run the `app` configuration on an emulator or Android phone.

## Map note
The map is a schematic coordinate visualization, not a surveyed administrative boundary or road-navigation map. Warehouse data continues to come from the project's existing `MockWarehouseSource`; no new map SDK or data module was added.
