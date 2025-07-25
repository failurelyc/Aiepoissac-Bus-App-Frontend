package com.aiepoissac.busapp


import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.aiepoissac.busapp.data.busarrival.Bus
import com.aiepoissac.busapp.data.busarrival.BusService
import com.aiepoissac.busapp.data.businfo.BusRouteInfo
import com.aiepoissac.busapp.data.businfo.BusStopInfo
import com.aiepoissac.busapp.ui.BusArrivalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.time.LocalDateTime
import java.time.Month

@OptIn(ExperimentalCoroutinesApi::class)
class BusArrivalViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val busArrivalDataFor18309 = listOf(
        Pair(
            first = Pair(
                first = BusStopInfo(
                    busStopCode = "1000169",
                    roadName = "Research Link",
                    description = "COM 3",
                    latitude = 1.2949181152540041,
                    longitude = 103.77493256351714
                ),
                second = BusStopInfo(
                    busStopCode = "1000169",
                    roadName = "Research Link",
                    description = "COM 3",
                    latitude = 1.2949181152540041,
                    longitude = 103.77493256351714
                )
            ),
            second = BusService(
                serviceNo = "1004",
                operator = "",
                nextBus = Bus(
                    originCode = "1000169",
                    destinationCode = "1000169",
                    estimatedArrival = LocalDateTime
                        .of(2025, Month.AUGUST, 11, 9, 33, 0),
                    monitored = 1,
                    latitude = 1.3035640377827036,
                    longitude = 103.77441333021167,
                    visitNumber = "1",
                    load = "SEA",
                    feature = "WAB",
                    type = "SD"
                ),
                nextBus2 = Bus(
                    originCode = "1000169",
                    destinationCode = "1000169",
                    estimatedArrival = LocalDateTime
                        .of(2025, Month.AUGUST, 11, 9, 38, 0),
                    monitored = 1,
                    latitude = 1.2971385871545937,
                    longitude = 103.7787816322459,
                    visitNumber = "1",
                    load = "SEA",
                    feature = "WAB",
                    type = "SD"
                ),
                nextBus3 = Bus(
                    originCode = "1000169",
                    destinationCode = "1000169",
                    estimatedArrival = LocalDateTime
                        .of(2025, Month.AUGUST, 11, 9, 43, 0),
                    monitored = 1,
                    latitude = 1.2918202990643677,
                    longitude = 103.78042627545256,
                    visitNumber = "1",
                    load = "SEA",
                    feature = "WAB",
                    type = "SD"
                )
            )
        ),
        Pair(
            first = Pair(
                first = BusStopInfo(
                    busStopCode = "1000151",
                    roadName = "Prince George's Pk",
                    description = "PGP",
                    latitude = 1.2918202990643677,
                    longitude = 103.78042627545256
                ),
                second = BusStopInfo(
                    busStopCode = "1000159",
                    roadName = "Prince George's Pk",
                    description = "PGP Foyer",
                    latitude = 1.2909442886549687,
                    longitude = 103.78110146793922
                )
            ),
            second = BusService(
                serviceNo = "1011",
                operator = "",
                nextBus = Bus(
                    originCode = "1000151",
                    destinationCode = "1000159",
                    estimatedArrival = LocalDateTime
                        .of(2025, Month.AUGUST, 11, 9, 35, 0),
                    monitored = 1,
                    latitude = 1.3007452197593086,
                    longitude = 103.769926979676,
                    visitNumber = "1",
                    load = "SEA",
                    feature = "WAB",
                    type = "SD"
                ),
                nextBus2 = Bus(
                    originCode = "1000151",
                    destinationCode = "1000159",
                    estimatedArrival = LocalDateTime
                        .of(2025, Month.AUGUST, 11, 9, 40, 0),
                    monitored = 1,
                    latitude = 1.2989025862071986,
                    longitude = 103.77438028222298,
                    visitNumber = "1",
                    load = "SEA",
                    feature = "WAB",
                    type = "SD"
                ),
                nextBus3 = Bus(
                    originCode = "1000151",
                    destinationCode = "1000159",
                    estimatedArrival = LocalDateTime
                        .of(2025, Month.AUGUST, 11, 9, 50, 0),
                    monitored = 0,
                    latitude = 0.0,
                    longitude = 0.0,
                    visitNumber = "1",
                    load = "SEA",
                    feature = "WAB",
                    type = "SD"
                )
            )

        )
    )

    @Test
    fun initialiseToNonExistentBusStop_BusStopInfoIsNullAndBusRouteListIsEmpty() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusArrivalViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            busStopCode = "Hello"
        )
        advanceUntilIdle()

        val newUIState = viewModel.uiState.value

        assertTrue(newUIState.busStopInfo == null)
        assertTrue(newUIState.busRoutes.isEmpty())
        assertFalse(newUIState.showBusArrival)

        Dispatchers.resetMain()
    }

    @Test
    fun changeToNonExistentBusStop_BusStopInfoIsNullAndBusRouteListIsEmpty() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusArrivalViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            busStopCode = "18309"
        )
        advanceUntilIdle()

        viewModel.updateBusStopCodeInput(busStopCodeInput = "Hello")
        advanceUntilIdle()

        viewModel.updateBusStop()
        advanceUntilIdle()

        val newUIState = viewModel.uiState.value

        assertTrue(newUIState.busStopInfo == null)
        assertTrue(newUIState.busRoutes.isEmpty())
        assertFalse(newUIState.showBusArrival)

        Dispatchers.resetMain()
    }

    @Test
    fun initialiseToNormalBusStop_BusStopInfoIsNullAndBusRouteListIsEmptyAndBusArrivalsIsHidden() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusArrivalViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            busStopCode = "18309"
        )
        advanceUntilIdle()

        val newUIState = viewModel.uiState.value

        assertEquals(
            listOf(
                BusRouteInfo(
                    serviceNo = "1004",
                    direction = 1,
                    stopSequence = 10,
                    busStopCode = "18309",
                    distance = 5.3
                ),
                BusRouteInfo(
                    serviceNo = "1004B",
                    direction = 1,
                    stopSequence = 3,
                    busStopCode = "18309",
                    distance = 1.6
                ),
                BusRouteInfo(
                    serviceNo = "1011",
                    direction = 1,
                    stopSequence = 12,
                    busStopCode = "18309",
                    distance = 4.9
                )
            ),
            newUIState.busRoutes,

        )

        assertEquals(
            BusStopInfo(
                busStopCode = "18309",
                roadName = "Lower Kent Ridge Rd",
                description = "Blk S17",
                latitude = 1.2975513679805142,
                longitude = 103.78072079767756
            ),
            newUIState.busStopInfo
        )

        assertTrue(newUIState.showBusArrival)

        Dispatchers.resetMain()
    }

    @Test
    fun changeToNormalBusStop_BusStopInfoAndBusRouteListUpdatedCorrectly() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusArrivalViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            busStopCode = "Hello"
        )
        advanceUntilIdle()

        viewModel.updateBusStopCodeInput(busStopCodeInput = "18309")
        advanceUntilIdle()

        viewModel.updateBusStop()
        advanceUntilIdle()

        val newUIState = viewModel.uiState.value

        assertEquals(
            newUIState.busRoutes,
            listOf(
                BusRouteInfo(
                    serviceNo = "1004",
                    direction = 1,
                    stopSequence = 10,
                    busStopCode = "18309",
                    distance = 5.3
                ),
                BusRouteInfo(
                    serviceNo = "1004B",
                    direction = 1,
                    stopSequence = 3,
                    busStopCode = "18309",
                    distance = 1.6
                ),
                BusRouteInfo(
                    serviceNo = "1011",
                    direction = 1,
                    stopSequence = 12,
                    busStopCode = "18309",
                    distance = 4.9
                )
            )

        )

        assertEquals(
            newUIState.busStopInfo,
            BusStopInfo(
                busStopCode = "18309",
                roadName = "Lower Kent Ridge Rd",
                description = "Blk S17",
                latitude = 1.2975513679805142,
                longitude = 103.78072079767756
            )
        )

        Dispatchers.resetMain()
    }

    @Test
    fun initialiseToTerminusBusStop_BusStopInfoIsNullAndBusRouteListIsEmptyAndBusArrivalsIsHidden() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusArrivalViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            busStopCode = "1000169"
        )
        advanceUntilIdle()

        val newUIState = viewModel.uiState.value

        assertEquals(
            listOf(
                BusRouteInfo(
                    serviceNo = "1004",
                    direction = 1,
                    stopSequence = 0,
                    busStopCode = "1000169",
                    distance = 0.0
                ),
                BusRouteInfo(
                    serviceNo = "1004",
                    direction = 1,
                    stopSequence = 13,
                    busStopCode = "1000169",
                    distance = 7.5
                ),
                BusRouteInfo(
                    serviceNo = "1004",
                    direction = 1,
                    stopSequence = 26,
                    busStopCode = "1000169",
                    distance = 12.5
                )
            ),
            newUIState.busRoutes

        )

        assertEquals(
            BusStopInfo(
                busStopCode = "1000169",
                roadName = "Research Link",
                description = "COM 3",
                latitude = 1.2949181152540041,
                longitude = 103.77493256351714
            ),
            newUIState.busStopInfo
        )

        assertTrue(newUIState.showBusArrival)

        Dispatchers.resetMain()
    }

    @Test
    fun changeToTerminusBusStop_BusStopInfoAndBusRouteListUpdatedCorrectly() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusArrivalViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter()
        )
        advanceUntilIdle()

        viewModel.updateBusStopCodeInput(busStopCodeInput = "1000169")
        advanceUntilIdle()

        viewModel.updateBusStop()
        advanceUntilIdle()

        val newUIState = viewModel.uiState.value

        assertEquals(
            listOf(
                BusRouteInfo(
                    serviceNo = "1004",
                    direction = 1,
                    stopSequence = 0,
                    busStopCode = "1000169",
                    distance = 0.0
                ),
                BusRouteInfo(
                    serviceNo = "1004",
                    direction = 1,
                    stopSequence = 13,
                    busStopCode = "1000169",
                    distance = 7.5
                ),
                BusRouteInfo(
                    serviceNo = "1004",
                    direction = 1,
                    stopSequence = 26,
                    busStopCode = "1000169",
                    distance = 12.5
                )
            ),
            newUIState.busRoutes

        )

        assertEquals(
            BusStopInfo(
                busStopCode = "1000169",
                roadName = "Research Link",
                description = "COM 3",
                latitude = 1.2949181152540041,
                longitude = 103.77493256351714
            ),
            newUIState.busStopInfo
        )

        Dispatchers.resetMain()
    }

    @Test
    fun initialisedToBusStopWithInternetConnection_BusArrivalUpdatedCorrectly() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusArrivalViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = TestBusArrivalGetter(),
            busStopCode = "18309"
        )
        advanceUntilIdle()

        val newUIState = viewModel.uiState.value

        assertEquals(
            busArrivalDataFor18309,
            newUIState.busArrivalData
        )

        Dispatchers.resetMain()

    }

    @Test
    fun changedBusStop_BusArrivalUpdatedCorrectly() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusArrivalViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = TestBusArrivalGetter(),
            busStopCode = "Hello"
        )
        advanceUntilIdle()

        viewModel.updateBusStopCodeInput(busStopCodeInput = "18309")
        advanceUntilIdle()

        viewModel.updateBusStop()
        advanceUntilIdle()

        val newUIState = viewModel.uiState.value

        assertEquals(
            busArrivalDataFor18309,
            newUIState.busArrivalData
        )

        Dispatchers.resetMain()
    }

    @Test
    fun initialisedToBusStopWithoutInternetConnection_BusArrivalSetToNull() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusArrivalViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            busStopCode = "18309"
        )
        advanceUntilIdle()

        val newUIState = viewModel.uiState.value

        assertEquals(
            newUIState.busArrivalData,
            null
        )

        Dispatchers.resetMain()

    }


    @Test
    fun searchingWithString_SearchResultsOnlyContainBusStopsContainingString() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusArrivalViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter()
        )
        advanceUntilIdle()

        viewModel.updateBusStopCodeInput(busStopCodeInput = "university")
        advanceUntilIdle()

        val newUIState = viewModel.uiState.value

        assertEquals(
            listOf(
                BusStopInfo(
                    busStopCode = "18319",
                    roadName = "Lower Kent Ridge Rd",
                    description = "Opp University Hall",
                    latitude = 1.2975365128314262,
                    longitude = 103.77812636874044
                ),
                BusStopInfo(
                    busStopCode = "18321",
                    roadName = "Lower Kent Ridge Rd",
                    description = "Opp University Health Ctr",
                    latitude = 1.298797868761511,
                    longitude = 103.77561524481003
                ),
                BusStopInfo(
                    busStopCode = "18329",
                    roadName = "Lower Kent Ridge Rd",
                    description = "University Health Ctr",
                    latitude = 1.298934626270826,
                    longitude = 103.77610877124941
                ),
                BusStopInfo(
                    busStopCode = "1000109",
                    roadName = "College Link Rd",
                    description = "University Town",
                    latitude = 1.3035640377827036,
                    longitude = 103.77441333021167
                )
            ),
            newUIState.searchResult
        )
        assertTrue(newUIState.expanded)

        Dispatchers.resetMain()
    }

    @Test
    fun searchingWithUpdatedString_SearchResultsOnlyContainBusStopsContainingUpdatedString() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusArrivalViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter()
        )
        advanceUntilIdle()

        viewModel.updateBusStopCodeInput(busStopCodeInput = "university")
        advanceUntilIdle()

        viewModel.updateBusStopCodeInput(busStopCodeInput = "Kent Ridge")
        advanceUntilIdle()

        val newUIState = viewModel.uiState.value

        assertEquals(
            listOf(
                BusStopInfo(
                    busStopCode = "18331",
                    roadName = "Lower Kent Ridge Rd",
                    description = "Kent Ridge Stn Exit A/NUH",
                    latitude = 1.2948117103061263,
                    longitude = 103.78437918054274
                ),
                BusStopInfo(
                    busStopCode = "18339",
                    roadName = "Lower Kent Ridge Rd",
                    description = "Opp Kent Ridge Stn Exit A",
                    latitude = 1.2950030435540145,
                    longitude = 103.78460883908544
                ),
                BusStopInfo(
                    busStopCode = "16009",
                    roadName = "Clementi Rd",
                    description = "Kent Ridge Ter",
                    latitude = 1.2942547560928301,
                    longitude = 103.76987914606597
                )
            ),
            newUIState.searchResult
        )
        assertTrue(newUIState.expanded)

        Dispatchers.resetMain()
    }

    @Test
    fun clearingSearchString_SearchResultsIsEmpty() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusArrivalViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter()
        )
        advanceUntilIdle()

        viewModel.updateBusStopCodeInput(busStopCodeInput = "university")
        advanceUntilIdle()

        viewModel.updateBusStopCodeInput(busStopCodeInput = "")
        advanceUntilIdle()

        val newUIState = viewModel.uiState.value

        assertEquals(listOf<BusStopInfo>(), newUIState.searchResult)
        assertFalse(newUIState.expanded)

        Dispatchers.resetMain()
    }

    @Test
    fun searchingWithCode_SearchResultsOnlyContainBusStopsStartingWithCode() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusArrivalViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter()
        )
        advanceUntilIdle()

        viewModel.updateBusStopCodeInput(busStopCodeInput = "1833")
        advanceUntilIdle()

        val newUIState = viewModel.uiState.value

        assertEquals(
            listOf(
                BusStopInfo(
                    busStopCode = "18331",
                    roadName = "Lower Kent Ridge Rd",
                    description = "Kent Ridge Stn Exit A/NUH",
                    latitude = 1.2948117103061263,
                    longitude = 103.78437918054274
                ),
                BusStopInfo(
                    busStopCode = "18339",
                    roadName = "Lower Kent Ridge Rd",
                    description = "Opp Kent Ridge Stn Exit A",
                    latitude = 1.2950030435540145,
                    longitude = 103.78460883908544
                )
            ),
            newUIState.searchResult
        )
        assertTrue(newUIState.expanded)

        Dispatchers.resetMain()
    }

    @Test
    fun changingSearchingType_SearchResultsOnlyContainBusStopsStartingWithCode() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusArrivalViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter()
        )
        advanceUntilIdle()

        viewModel.updateBusStopCodeInput(busStopCodeInput = "university")
        advanceUntilIdle()

        viewModel.updateBusStopCodeInput(busStopCodeInput = "1600")
        advanceUntilIdle()

        val newUIState = viewModel.uiState.value

        assertEquals(
            listOf(
                BusStopInfo(
                    busStopCode = "16009",
                    roadName = "Clementi Rd",
                    description = "Kent Ridge Ter",
                    latitude = 1.2942547560928301,
                    longitude = 103.76987914606597
                )
            ),
            newUIState.searchResult
        )
        assertTrue(newUIState.expanded)

        Dispatchers.resetMain()
    }

    @Test
    fun switchingToOppositeStop_BusStopCodeUpdatedToTheOppositeOne() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusArrivalViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            busStopCode = "18331"
        )
        advanceUntilIdle()

        viewModel.switchToOppositeBusStop()
        advanceUntilIdle()

        val newUIState = viewModel.uiState.value

        assertEquals(
            BusStopInfo(
                    busStopCode = "18339",
                    roadName = "Lower Kent Ridge Rd",
                    description = "Opp Kent Ridge Stn Exit A",
                    latitude = 1.2950030435540145,
                    longitude = 103.78460883908544
            ),
            newUIState.busStopInfo
        )

        assertEquals(
            listOf(
                BusStopInfo(
                    busStopCode = "18339",
                    roadName = "Lower Kent Ridge Rd",
                    description = "Opp Kent Ridge Stn Exit A",
                    latitude = 1.2950030435540145,
                    longitude = 103.78460883908544
                )
            ),
            newUIState.searchResult
        )

        assertEquals("18339", newUIState.busStopCodeInput)

        Dispatchers.resetMain()
    }

    @Test
    fun switchingToOppositeStopFailed_BusStopCodeNotUpdated() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusArrivalViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            busStopCode = "hello"
        )
        advanceUntilIdle()

        viewModel.switchToOppositeBusStop()
        advanceUntilIdle()

        val newUIState = viewModel.uiState.value

        assertEquals(
            "hello",
            newUIState.busStopCodeInput
        )

        assertEquals(
            listOf<BusStopInfo>(),
            newUIState.searchResult
        )

        Dispatchers.resetMain()
    }

}