package com.aiepoissac.busapp.userdata

/**
 * This interface acts as a way to interact with the User data Database.
 */
interface UserDataRepository {

    /**
     * Insert a journey into the database
     *
     * @param journeyInfo The journey to be inserted
     */
    suspend fun insertJourney(journeyInfo: JourneyInfo)

    /**
     * Update a journey in the database
     *
     * @param journeyInfo The journey with the updated information
     */
    suspend fun updateJourney(journeyInfo: JourneyInfo)

    /**
     * Delete a journey from the database
     *
     * @param journeyInfo The journey to be deleted
     */
    suspend fun deleteJourney(journeyInfo: JourneyInfo)

    /**
     * Get all journeys in the database (or owned by the user)
     *
     * @return A list of journeys
     */
    suspend fun getAllJourneys(): List<JourneyInfo>

    /**
     * Get a journey using the journeyID
     *
     * @return The journey with this journeyID
     */
    suspend fun getJourney(journeyID: String): JourneyInfo

    /**
     * Insert a journey segment into the database
     *
     * @param journeySegmentInfo The journey to be inserted
     */
    suspend fun insertJourneySegment(journeySegmentInfo: JourneySegmentInfo)

    /**
     * Update a journey segment in the database
     *
     * @param journeySegmentInfo The journey segment with the updated information
     */
    suspend fun updateJourneySegment(journeySegmentInfo: JourneySegmentInfo)

    /**
     * Delete a journey from the database
     *
     * @param journeySegmentInfo The journey segment to be deleted
     */
    suspend fun deleteJourneySegment(journeySegmentInfo: JourneySegmentInfo)

    /**
     * Delete all journey segments with this journeyID
     *
     * @param journeyID The journeyID of the journey segments to be deleted
     */
    suspend fun deleteAllJourneySegments(journeyID: String)

    /**
     * Get all journey segments with this journeyID
     *
     * @param journeyID The journeyID of the journey segments
     * @return A list of the journey segments
     */
    suspend fun getAllJourneySegments(journeyID: String): List<JourneySegmentInfo>

}