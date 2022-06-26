# DroneFlyCheckerApp
A simple android app that checks the current weather and forms a list based off the weather safety (for drone take off).
# Features
- Support for 3 types of weather safety: safe, warning, danger
    - Tap each element for more information
- Show the current and future weather (up to 5 days) of the current location
- Fully customizable (time, temperature, wind, etc.)
- Dynamic gps location (load the current location from google services, from gps or from the cache)

# Technologies Used
- Splash Screen
- SharedPreferences
- Bi-lingual support (English and Greek)
- Google Fused Location API
- Google Geocoding API 
- LocationManager
### UI
- RecyclerView
- SettingsFragment
- Swipe to refresh
