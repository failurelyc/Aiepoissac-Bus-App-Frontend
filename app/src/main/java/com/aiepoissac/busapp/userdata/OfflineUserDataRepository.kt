package com.aiepoissac.busapp.userdata

/**
 * This class provides the implementation for interacting with the local user data database.
 *
 * @param journeyInfoDAO The Data Access Object for JourneyInfo/Journey_Table
 * @param journeySegmentInfoDAO The Data Access Object for JourneySegmentInfo/Journey_Segment_Table
 */
class OfflineUserDataRepository(
    private val journeyInfoDAO: JourneyInfoDAO,
    private val journeySegmentInfoDAO: JourneySegmentInfoDAO
): UserDataRepository {

    override suspend fun insertJourney(journeyInfo: JourneyInfo) {
        journeyInfoDAO.insert(journeyInfo)
    }

    override suspend fun updateJourney(journeyInfo: JourneyInfo) {
        journeyInfoDAO.update(journeyInfo)
    }

    override suspend fun deleteJourney(journeyInfo: JourneyInfo) {
        journeyInfoDAO.delete(journeyInfo)
    }

    override suspend fun getAllJourneys(): List<JourneyInfo> {
        return journeyInfoDAO.getAllJourneys()
    }

    override suspend fun getJourney(journeyID: String): JourneyInfo {
        return journeyInfoDAO.getJourney(journeyID)
    }

    override suspend fun insertJourneySegment(journeySegmentInfo: JourneySegmentInfo) {
        journeySegmentInfoDAO.insert(journeySegmentInfo)
    }

    override suspend fun updateJourneySegment(journeySegmentInfo: JourneySegmentInfo) {
        journeySegmentInfoDAO.update(journeySegmentInfo)
    }

    override suspend fun deleteJourneySegment(journeySegmentInfo: JourneySegmentInfo) {
        journeySegmentInfoDAO.delete(journeySegmentInfo)
    }

    override suspend fun deleteAllJourneySegments(journeyID: String) {
        journeySegmentInfoDAO.deleteAllJourneySegments(journeyID)
    }

    override suspend fun getAllJourneySegments(journeyID: String): List<JourneySegmentInfo> {
        return journeySegmentInfoDAO.getAllJourneySegments(journeyID)
    }

}