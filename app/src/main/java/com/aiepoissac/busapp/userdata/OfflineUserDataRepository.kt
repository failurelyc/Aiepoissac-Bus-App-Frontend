package com.aiepoissac.busapp.userdata

class OfflineUserDataRepository(
    private val busJourneyListInfoDAO: BusJourneyListInfoDAO,
    private val busJourneyInfoDAO: BusJourneyInfoDAO
): UserDataRepository {

    override suspend fun insertBusJourneyListInfo(busJourneyListInfo: BusJourneyListInfo) {
        busJourneyListInfoDAO.insert(busJourneyListInfo)
    }

    override suspend fun updateBusJourneyListInfo(busJourneyListInfo: BusJourneyListInfo) {
        busJourneyListInfoDAO.update(busJourneyListInfo)
    }

    override suspend fun deleteBusJourneyListInfo(busJourneyListInfo: BusJourneyListInfo) {
        busJourneyListInfoDAO.delete(busJourneyListInfo)
    }

    override suspend fun getAllBusJourneyListInfo(): List<BusJourneyListInfo> {
        return busJourneyListInfoDAO.getAllBusJourneyListInfo()
    }

    override suspend fun getBusJourneyListInfo(journeyID: String): BusJourneyListInfo {
        return busJourneyListInfoDAO.getBusJourneyListInfo(journeyID)
    }

    override suspend fun insertBusJourneyInfo(busJourneyInfo: BusJourneyInfo) {
        busJourneyInfoDAO.insert(busJourneyInfo)
    }

    override suspend fun updateBusJourneyInfo(busJourneyInfo: BusJourneyInfo) {
        busJourneyInfoDAO.update(busJourneyInfo)
    }

    override suspend fun deleteBusJourneyInfo(busJourneyInfo: BusJourneyInfo) {
        busJourneyInfoDAO.delete(busJourneyInfo)
    }

    override suspend fun deleteBusJourneyList(journeyID: String) {
        busJourneyInfoDAO.deleteBusJourneyList(journeyID)
    }

    override suspend fun getBusJourneyList(journeyID: String): List<BusJourneyInfo> {
        return busJourneyInfoDAO.getBusJourneyList(journeyID)
    }

}