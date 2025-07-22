package com.aiepoissac.busapp.userdata

interface UserDataRepository {

    suspend fun insertJourney(journeyInfo: JourneyInfo)

    suspend fun updateJourney(journeyInfo: JourneyInfo)

    suspend fun deleteJourney(journeyInfo: JourneyInfo)

    suspend fun getAllJourneys(): List<JourneyInfo>

    suspend fun getJourney(journeyID: String): JourneyInfo

    suspend fun insertJourneySegment(journeySegmentInfo: JourneySegmentInfo)

    suspend fun updateJourneySegment(journeySegmentInfo: JourneySegmentInfo)

    suspend fun deleteJourneySegment(journeySegmentInfo: JourneySegmentInfo)

    suspend fun deleteAllJourneySegments(journeyID: String)

    suspend fun getAllJourneySegments(journeyID: String): List<JourneySegmentInfo>

}