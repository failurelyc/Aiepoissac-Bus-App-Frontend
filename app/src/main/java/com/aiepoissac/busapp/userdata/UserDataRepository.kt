package com.aiepoissac.busapp.userdata

interface UserDataRepository {

    suspend fun insertBusJourneyListInfo(busJourneyListInfo: BusJourneyListInfo)

    suspend fun updateBusJourneyListInfo(busJourneyListInfo: BusJourneyListInfo)

    suspend fun deleteBusJourneyListInfo(busJourneyListInfo: BusJourneyListInfo)

    suspend fun getAllBusJourneyListInfo(): List<BusJourneyListInfo>

    suspend fun getBusJourneyListInfo(journeyID: String): BusJourneyListInfo

    suspend fun insertBusJourneyInfo(busJourneyInfo: BusJourneyInfo)

    suspend fun updateBusJourneyInfo(busJourneyInfo: BusJourneyInfo)

    suspend fun deleteBusJourneyInfo(busJourneyInfo: BusJourneyInfo)

    suspend fun deleteBusJourneyList(journeyID: String)

    suspend fun getBusJourneyList(journeyID: String): List<BusJourneyInfo>

}