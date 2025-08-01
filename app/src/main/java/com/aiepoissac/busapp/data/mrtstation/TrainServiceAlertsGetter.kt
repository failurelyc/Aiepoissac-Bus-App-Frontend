package com.aiepoissac.busapp.data.mrtstation

import com.aiepoissac.busapp.data.businfo.getData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URL

/**
 * This interface provides train service alerts data.
 */
interface TrainServiceAlertsGetter {

    /**
     * Get all lift maintenance data
     *
     * @return Lift maintenance data
     */
    suspend fun getFacilitiesMaintenance(): FacilitiesMaintenance

    /**
     * Get all train service alerts data
     *
     * @return Train service alerts data
     */
    suspend fun getTrainServiceAlerts(): TrainServiceAlerts

}

/**
 * This class provides train service alerts data from the LTA API.
 */
class RealTrainServiceAlertsGetter: TrainServiceAlertsGetter {
    override suspend fun getFacilitiesMaintenance(): FacilitiesMaintenance {
        val json = withContext(Dispatchers.IO) {
            getData(URL("https://datamall2.mytransport.sg/ltaodataservice/v2/FacilitiesMaintenance"))
        }
        return Json.decodeFromString<FacilitiesMaintenance>(json)
    }

    override suspend fun getTrainServiceAlerts(): TrainServiceAlerts {
        val json = withContext(Dispatchers.IO) {
            getData(URL("https://datamall2.mytransport.sg/ltaodataservice/TrainServiceAlerts"))
        }
        return Json.decodeFromString<TrainServiceAlerts>(json)
    }

}