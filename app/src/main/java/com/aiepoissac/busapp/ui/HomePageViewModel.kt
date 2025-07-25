package com.aiepoissac.busapp.ui

import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aiepoissac.busapp.BusApplication
import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.populateBusRoutes
import com.aiepoissac.busapp.data.businfo.populateBusServices
import com.aiepoissac.busapp.data.businfo.populateBusStops
import com.aiepoissac.busapp.data.businfo.populateMRTStations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import androidx.core.content.edit
import com.aiepoissac.busapp.data.businfo.populatePlannedBusRoutes
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class HomePageViewModelFactory(
    private val busRepository: BusRepository = BusApplication.instance.container.busRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomePageViewModel::class.java)) {
            HomePageViewModel(
                busRepository = busRepository
            ) as T
        } else {
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }

}

class HomePageViewModel(
    private val busRepository: BusRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomePageUIState())
    val uiState = _uiState.asStateFlow()

    var downloaded by mutableStateOf(false)
        private set

    var failedDownload by mutableStateOf(false)
        private set

    fun updateBusStopCodeInput(busStopCodeInput: String) {
        _uiState.update {
            it.copy(busStopCodeInput = busStopCodeInput)
        }
        viewModelScope.launch {
            if (busStopCodeInput.isNotEmpty()) {
                if (busStopCodeInput.first() < '0' || busStopCodeInput.first() > '9') {
                    _uiState.update {
                        it.copy(
                            busStopSearchResult = busRepository.getBusStopsContaining(busStopCodeInput),
                            busStopSearchBarExpanded = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            busStopSearchResult = busRepository.getBusStopsWithPrefixCode(busStopCodeInput),
                            busStopSearchBarExpanded = true
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        busStopSearchResult = listOf(),
                        busStopSearchBarExpanded = false
                    )
                }
            }
        }
    }

    fun updateBusStopRoadInput(busStopRoadInput: String) {
        _uiState.update {
            it.copy(busStopRoadInput = busStopRoadInput)
        }

        viewModelScope.launch {
            if (busStopRoadInput.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        busStopRoadSearchResult = busRepository.getBusStopsWithPartialRoadName(busStopRoadInput),
                        busStopRoadSearchBarExpanded = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        busStopRoadSearchResult = listOf(),
                        busStopRoadSearchBarExpanded = false
                    )
                }
            }
        }
    }

    fun updateBusServiceInput(busServiceInput: String) {
        _uiState.update {
            it.copy(busServiceInput = busServiceInput)
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    busServiceSearchResult = busRepository.getBusService(busServiceInput),
                    busServiceSearchBarExpanded = true
                )
            }
        }
    }

    fun updateMRTStationInput(mrtStationInput: String) {
        _uiState.update {
            it.copy(mrtStationInput = mrtStationInput)
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    mrtStationSearchResult = busRepository.getMRTStationsContaining(mrtStationInput),
                    mrtStationSearchBarExpanded = true
                )
            }
        }
    }

    fun setBusStopSearchBarExpanded(expanded: Boolean) {
        _uiState.update { it.copy(busStopSearchBarExpanded = expanded) }
    }

    fun setBusServiceSearchBarExpanded(expanded: Boolean) {
        _uiState.update { it.copy(busServiceSearchBarExpanded = expanded) }
    }

    fun setBusStopRoadSearchBarExpanded(expanded: Boolean) {
        _uiState.update { it.copy(busStopRoadSearchBarExpanded = expanded) }
    }

    fun setMRTStationSearchBarExpanded(expanded: Boolean) {
        _uiState.update { it.copy(mrtStationSearchBarExpanded = expanded) }
    }

    init {
        reInitialiseData()
    }

    fun reInitialiseData() {
        failedDownload = false
        downloaded = false
        viewModelScope.launch {
            initialiseOfflineData()
            initialiseOnlineData()
        }
    }

    private suspend fun initialiseOfflineData() = withContext(Dispatchers.IO) {
        if (busRepository.getMRTStationCount() == 0) {
            populateMRTStations(busRepository)
        }
    }

    private suspend fun initialiseOnlineData() = withContext(Dispatchers.IO) {
        //downloading bus services, routes and stops data
        if (checkIfBusDataOutdated() ||
            busRepository.getBusRoutesCount() == 0 ||
            busRepository.getBusStopsCount() == 0 ||
            busRepository.getBusServicesCount() == 0
            ) {
            try {
                Log.d(Pages.HomePage.title, "Started Downloading Bus Data")
                populateBusServices(busRepository)
                populateBusRoutes(busRepository)
                populateBusStops(busRepository)
                if (busRepository.getBusServicesCount() != 0 &&
                    busRepository.getBusRoutesCount() != 0 &&
                    busRepository.getBusStopsCount() != 0) {
                    withContext(Dispatchers.Main) {
                        downloaded = true
                    }
                    saveLastTimeBusDataDownloaded()
                } else {
                    throw IOException("Nothing was downloaded")
                }
                Log.d(Pages.HomePage.title, "Downloaded Bus Data")
            } catch (e: IOException) {
                Log.e(Pages.HomePage.title, "Failed to download bus data: ${e.message}")
                withContext(Dispatchers.Main) {
                    failedDownload = true
                }
            }
        } else {
            Log.d(Pages.HomePage.title, "Bus Data already downloaded")
            withContext(Dispatchers.Main) {
                downloaded = true
            }
            saveLastTimeBusDataDownloaded()
        }

        //downloading planned (future) bus routes
        if (checkIfPlannedBusRoutesOutdated()
            || busRepository.getPlannedBusRoutesCount() == 0) {
            try {
                Log.d(Pages.HomePage.title, "Started Downloading Planned Bus Routes")
                populatePlannedBusRoutes(busRepository)
                Log.d(Pages.HomePage.title, "Downloaded Planned Bus Routes")
                saveLastTimePlannedBusRoutesDownloaded()
            } catch (e: IOException) {
                Log.e(Pages.HomePage.title, "Failed to download planned future bus routes: ${e.message}")
                busRepository.deleteAllPlannedBusRoutes()
            }
        }




    }

    private fun saveLastTimePlannedBusRoutesDownloaded() {
        val sharedPreferences = BusApplication.instance
            .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit {
            val now = System.currentTimeMillis()
            putLong("planned_bus_routes_downloaded_time", now)
        }
    }

    private fun saveLastTimeBusDataDownloaded() {
        val sharedPreferences = BusApplication.instance
            .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit {
            val now = System.currentTimeMillis()
            putLong("last_opened_time", now)
        }
    }

    private fun getLastTimePlannedBusRoutesDownloaded(): Long {
        val sharedPreferences = BusApplication.instance
            .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("planned_bus_routes_downloaded_time", 0)
    }

    private fun getLastTimeBusDataDownloaded(): Long {
        val sharedPreferences = BusApplication.instance
            .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("last_opened_time", 0)
    }

    private fun checkIfPlannedBusRoutesOutdated(): Boolean {
        val lastDownloadTime = getLastTimePlannedBusRoutesDownloaded()
        return System.currentTimeMillis() - lastDownloadTime >= 604800000L
    }

    private fun checkIfBusDataOutdated(): Boolean {
        val lastDownloadTime = getLastTimeBusDataDownloaded()
        val lastDate = Calendar.getInstance()
        lastDate.timeInMillis = lastDownloadTime
        val thisDate = Calendar.getInstance()
        thisDate.timeInMillis = System.currentTimeMillis()
        val dayOfLastOpenedDate = lastDate.get(Calendar.DAY_OF_WEEK)
        if (dayOfLastOpenedDate == Calendar.SUNDAY) {
            val hourOfLastOpenTime = lastDate.get(Calendar.HOUR_OF_DAY)
            lastDate.set(Calendar.HOUR_OF_DAY, 4)
            lastDate.set(Calendar.MINUTE, 0)
            lastDate.set(Calendar.SECOND, 0)
            if (hourOfLastOpenTime >= 4) {
                lastDate.add(Calendar.DAY_OF_YEAR, 1) // Set to coming Monday 4am
            }
        } else if (dayOfLastOpenedDate == Calendar.MONDAY) {
            val hourOfLastOpenTime = lastDate.get(Calendar.HOUR_OF_DAY)
            lastDate.set(Calendar.HOUR_OF_DAY, 4)
            lastDate.set(Calendar.MINUTE, 0)
            lastDate.set(Calendar.SECOND, 0)
            if (hourOfLastOpenTime >= 4) {
                lastDate.add(Calendar.DAY_OF_YEAR, 6) // Set to coming Sunday 4am
            }
        } else { //dayOfLastDate is not Sunday or Monday
            val daysFromLastDateToSunday = (Calendar.SUNDAY - dayOfLastOpenedDate + 7) % 7
            lastDate.add(Calendar.DAY_OF_YEAR, daysFromLastDateToSunday)
            lastDate.set(Calendar.HOUR_OF_DAY, 4)
            lastDate.set(Calendar.MINUTE, 0)
            lastDate.set(Calendar.SECOND, 0)
        }
        return thisDate.after(lastDate)
    }

}


