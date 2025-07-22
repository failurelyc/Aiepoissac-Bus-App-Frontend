package com.aiepoissac.busapp.data.mrtstation

import com.aiepoissac.busapp.data.businfo.getData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL

interface TrainServiceAlertsGetter {

    suspend fun getFacilitiesMaintenance(): FacilitiesMaintenance

    suspend fun getTrainServiceAlerts(): TrainServiceAlerts

}

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