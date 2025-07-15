package com.aiepoissac.busapp

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.aiepoissac.busapp.data.businfo.BusRouteInfo
import com.aiepoissac.busapp.data.businfo.BusServiceInfo
import com.aiepoissac.busapp.ui.BusRouteViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class BusRouteViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val bus1004AFullRoute =
        listOf(
            BusRouteInfo(
                serviceNo = "1004A",
                direction = 1,
                stopSequence = 0,
                busStopCode = "18331",
                distance = 0.0
            ),
            BusRouteInfo(
                serviceNo = "1004A",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18301",
                distance = 0.5
            ),
            BusRouteInfo(
                serviceNo = "1004A",
                direction = 1,
                stopSequence = 2,
                busStopCode = "18311",
                distance = 0.8
            ),
            BusRouteInfo(
                serviceNo = "1004A",
                direction = 1,
                stopSequence = 3,
                busStopCode = "18321",
                distance = 1.2
            ),
            BusRouteInfo(
                serviceNo = "1004A",
                direction = 1,
                stopSequence = 4,
                busStopCode = "1000109",
                distance = 1.9
            )
        )

    private val bus1004AStop0Location = LatLng(1.2948117103061263, 103.78437918054274)

    private val bus1004ARouteFromStop2 =
        listOf(
            BusRouteInfo(
                serviceNo = "1004A",
                direction = 1,
                stopSequence = 0,
                busStopCode = "18311",
                distance = 0.0
            ),
            BusRouteInfo(
                serviceNo = "1004A",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18321",
                distance = 0.4
            ),
            BusRouteInfo(
                serviceNo = "1004A",
                direction = 1,
                stopSequence = 2,
                busStopCode = "1000109",
                distance = 1.1
            )
        )

    private val bus1004AStop2Location = LatLng(1.2971385871545937, 103.7787816322459)

    private val bus1011FullRoute =
        listOf(
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 0,
                busStopCode = "1000151",
                distance = 0.0
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18331",
                distance = 0.8
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 2,
                busStopCode = "18301",
                distance = 1.4
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 3,
                busStopCode = "18311",
                distance = 1.6
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 4,
                busStopCode = "18321",
                distance = 2.0
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 5,
                busStopCode = "16171",
                distance = 2.2
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 6,
                busStopCode = "16181",
                distance = 2.6
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 7,
                busStopCode = "16181",
                distance = 3.1
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 8,
                busStopCode = "16181",
                distance = 3.4
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 9,
                busStopCode = "16161",
                distance = 3.9
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 10,
                busStopCode = "18329",
                distance = 4.3
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 11,
                busStopCode = "18319",
                distance = 4.6
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 12,
                busStopCode = "18309",
                distance = 4.9
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 13,
                busStopCode = "18339",
                distance = 5.5
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 14,
                busStopCode = "1000159",
                distance = 6.2
            )
        )

    private val bus1011RouteFromStop0 =
        listOf(
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 0,
                busStopCode = "1000151",
                distance = 0.0
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18331",
                distance = 0.8
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 2,
                busStopCode = "18301",
                distance = 1.4
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 3,
                busStopCode = "18311",
                distance = 1.6
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 4,
                busStopCode = "18321",
                distance = 2.0
            ),

            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 5,
                busStopCode = "16171",
                distance = 2.2
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 6,
                busStopCode = "16181",
                distance = 2.6
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 7,
                busStopCode = "16181",
                distance = 3.1
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 8,
                busStopCode = "16181",
                distance = 3.4
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 9,
                busStopCode = "16161",
                distance = 3.9
            )
        )


    private val bus1011Stop0Location = LatLng(1.2918202990643677, 103.78042627545256)

    private val bus1011RouteFromStop3 =
        listOf(
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 0,
                busStopCode = "18311",
                distance = 0.0
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18321",
                distance = 0.4
            ),

            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 2,
                busStopCode = "16171",
                distance = 0.6
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 3,
                busStopCode = "16181",
                distance = 1.0
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 4,
                busStopCode = "16181",
                distance = 1.5
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 5,
                busStopCode = "16181",
                distance = 1.8
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 6,
                busStopCode = "16161",
                distance = 2.3
            )
        )

    private val bus1011Stop3Location = LatLng(1.2971385871545937, 103.7787816322459)

    private val bus1011RouteFromStop5 =
        listOf(
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 0,
                busStopCode = "16171",
                distance = 0.0
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 1,
                busStopCode = "16181",
                distance = 0.4
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 2,
                busStopCode = "16181",
                distance = 0.9
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 3,
                busStopCode = "16181",
                distance = 1.2
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 4,
                busStopCode = "16161",
                distance = 1.7
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 5,
                busStopCode = "18329",
                distance = 2.1
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 6,
                busStopCode = "18319",
                distance = 2.4
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 7,
                busStopCode = "18309",
                distance = 2.7
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 8,
                busStopCode = "18339",
                distance = 3.3
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 9,
                busStopCode = "1000159",
                distance = 4.0
            )
        )

    private val bus1011Stop5Location = LatLng(1.2989025862071986, 103.77438028222298)

    private val bus1011RouteFromStop12 =
        listOf(
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 0,
                busStopCode = "18309",
                distance = 0.0
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18339",
                distance = 0.6
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 2,
                busStopCode = "1000159",
                distance = 1.3
            )
        )

    private val bus1011Stop12Location = LatLng(1.2975513679805142, 103.78072079767756)

    private val bus1004FullRoute =
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
                stopSequence = 1,
                busStopCode = "1000151",
                distance = 0.8
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 2,
                busStopCode = "18331",
                distance = 1.6
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 3,
                busStopCode = "18301",
                distance = 2.2
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 4,
                busStopCode = "18311",
                distance = 2.4
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 5,
                busStopCode = "18321",
                distance = 2.8
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 6,
                busStopCode = "16161",
                distance = 3.3
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 7,
                busStopCode = "1000109",
                distance = 3.7
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 8,
                busStopCode = "18329",
                distance = 4.7
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 9,
                busStopCode = "18319",
                distance = 5.0
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 10,
                busStopCode = "18309",
                distance = 5.3
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 11,
                busStopCode = "18339",
                distance = 5.9
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 12,
                busStopCode = "1000159",
                distance = 6.6
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
                stopSequence = 14,
                busStopCode = "1000139",
                distance = 7.8
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 15,
                busStopCode = "1000129",
                distance = 8.1
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 16,
                busStopCode = "1000119",
                distance = 8.5
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 17,
                busStopCode = "16189",
                distance = 8.8
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 18,
                busStopCode = "16179",
                distance = 9.1
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 19,
                busStopCode = "16161",
                distance = 9.5
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 20,
                busStopCode = "1000109",
                distance = 9.9
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 21,
                busStopCode = "16171",
                distance = 10.7
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 22,
                busStopCode = "16181",
                distance = 11.1
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 23,
                busStopCode = "1000111",
                distance = 11.5
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 24,
                busStopCode = "1000121",
                distance = 11.7
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 25,
                busStopCode = "1000131",
                distance = 12.3
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 26,
                busStopCode = "1000169",
                distance = 12.5
            )
        )

    private val bus1004Stop0Location = LatLng(1.2949181152540041, 103.77493256351714)

    private val bus1004RouteFromStop4 =
        listOf(
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 0,
                busStopCode = "18311",
                distance = 0.0
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18321",
                distance = 0.4
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 2,
                busStopCode = "16161",
                distance = 0.9
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 3,
                busStopCode = "1000109",
                distance = 1.3
            )
        )

    private val bus1004Stop4Location = LatLng(1.2971385871545937, 103.7787816322459)

    private val bus1004RouteFromStop10 =
        listOf(
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 0,
                busStopCode = "18309",
                distance = 0.0
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18339",
                distance = 0.6
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 2,
                busStopCode = "1000159",
                distance = 1.3
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 3,
                busStopCode = "1000169",
                distance = 2.2
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 4,
                busStopCode = "1000139",
                distance = 2.5
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 5,
                busStopCode = "1000129",
                distance = 2.8
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 6,
                busStopCode = "1000119",
                distance = 3.2
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 7,
                busStopCode = "16189",
                distance = 3.5
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 8,
                busStopCode = "16179",
                distance = 3.8
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 9,
                busStopCode = "16161",
                distance = 4.2
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 10,
                busStopCode = "1000109",
                distance = 4.6
            )
        )

    private val bus1004Stop10Location = LatLng(1.2975513679805142, 103.78072079767756)

    private val bus1004RouteFromStop17 =
        listOf(
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 0,
                busStopCode = "16189",
                distance = 0.0
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 1,
                busStopCode = "16179",
                distance = 0.3
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 2,
                busStopCode = "16161",
                distance = 0.7
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 3,
                busStopCode = "1000109",
                distance = 1.1
            )
        )

    private val bus1004Stop17Location = LatLng(1.2972176268958733, 103.7726877061283)

    private val bus1004RouteFromStop21 =
        listOf(
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 0,
                busStopCode = "16171",
                distance = 0.0
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 1,
                busStopCode = "16181",
                distance = 0.4
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 2,
                busStopCode = "1000111",
                distance = 0.8
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 3,
                busStopCode = "1000121",
                distance = 1.0
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 4,
                busStopCode = "1000131",
                distance = 1.6
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 5,
                busStopCode = "1000169",
                distance = 1.8
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 5,
                busStopCode = "1000169",
                distance = 1.8
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 6,
                busStopCode = "1000151",
                distance = 2.6
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 7,
                busStopCode = "18331",
                distance = 3.4
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 8,
                busStopCode = "18301",
                distance = 4.0
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 9,
                busStopCode = "18311",
                distance = 4.2
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 10,
                busStopCode = "18321",
                distance = 4.6
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 11,
                busStopCode = "16161",
                distance = 5.1
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 12,
                busStopCode = "1000109",
                distance = 5.5
            )
        )

    private val bus1004Stop21Location = LatLng(1.2989025862071986, 103.77438028222298)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun fullBusRouteNonLoop_fullRouteInBusRouteListAndOriginalRouteList() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004A",
            direction = 1,
            stopSequence = -1,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004AFullRoute,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004AFullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertFalse(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004AStop0Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun routeFrom0thStopNonLoop_fullRouteInBusRouteListAndOriginalRouteList() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004A",
            direction = 1,
            stopSequence = 0,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004AFullRoute,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004AFullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004AStop0Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun routeFrom2ndStopNonLoop_routeStartAt2() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004A",
            direction = 1,
            stopSequence = 2,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004ARouteFromStop2,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004AFullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004AStop2Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun firstStopUpdatedFrom0To2ForNonLoopRoute_routeStartAt2() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004A",
            direction = 1,
            stopSequence = -1,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.setFirstBusStop(2)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004ARouteFromStop2,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004AFullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004AStop2Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun routeRevertedToFullRouteForNonLoopRoute_fullRoute() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004A",
            direction = 1,
            stopSequence = 2,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.setOriginalFirstBusStop()

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004AFullRoute,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004AFullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertFalse(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004AStop0Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun firstStopUpdatedFrom0To1TwiceForNonLoopRoute_routeStartAt2() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004A",
            direction = 1,
            stopSequence = -1,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.setFirstBusStop(1)

        advanceUntilIdle()

        viewModel.setFirstBusStop(1)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004ARouteFromStop2,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004AFullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004AStop2Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun fullBusRouteSingleLoop_fullRouteInBusRouteListAndOriginalRouteList() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1011",
            direction = 1,
            stopSequence = -1,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1011FullRoute,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1011FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertFalse(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1011Stop0Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun routeFrom0thStopSingleLoop_routeEndAtLastStopInLoopingPoint() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1011",
            direction = 1,
            stopSequence = 0,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1011RouteFromStop0,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1011FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1011Stop0Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun singleLoopRouteFromBeforeLoopingPoint_routeEndAtLastStopInLoopingPoint() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1011",
            direction = 1,
            stopSequence = 3,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1011RouteFromStop3,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1011FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1011Stop3Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun singleLoopRouteFromAfterLoopingPoint_routeEndAtDestination() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1011",
            direction = 1,
            stopSequence = 5,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1011RouteFromStop5,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1011FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1011Stop5Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun fullBusRouteAfterLoopingPoint_routeEndAtDestination() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1011",
            direction = 1,
            stopSequence = -1,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.setLoopingPointAsFirstBusStop()

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1011RouteFromStop5,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1011FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertFalse(uiState.truncated)
        assertTrue(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1011Stop5Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun firstStopForBusRouteAfterLoopingPointUpdatedTo7_routeStartAt12() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1011",
            direction = 1,
            stopSequence = -1,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.setLoopingPointAsFirstBusStop()

        advanceUntilIdle()

        viewModel.setFirstBusStop(7)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1011RouteFromStop12,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1011FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1011Stop12Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun routeFromBeforeLoopingPointUpdatedToRouteAfterLoopingPoint_routeEndAtDestination() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1011",
            direction = 1,
            stopSequence = 3,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.setLoopingPointAsFirstBusStop()

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1011RouteFromStop5,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1011FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertFalse(uiState.truncated)
        assertTrue(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1011Stop5Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun routeFromAfterLoopingPointUpdatedToRouteAfterLoopingPoint_routeEndAtDestination() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1011",
            direction = 1,
            stopSequence = 12,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.setLoopingPointAsFirstBusStop()

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1011RouteFromStop5,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1011FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertFalse(uiState.truncated)
        assertTrue(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1011Stop5Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun firstStopUpdatedToBusStopBeforeLoopingPoint_routeEndAtLastStopInLoopingPoint() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1011",
            direction = 1,
            stopSequence = -1,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.setFirstBusStop(3)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1011RouteFromStop3,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1011FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1011Stop3Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun firstStopUpdatedToBusStopAfterLoopingPoint_routeEndAtDestination() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1011",
            direction = 1,
            stopSequence = -1,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.setFirstBusStop(12)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1011RouteFromStop12,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1011FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1011Stop12Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun firstStopUpdatedTo3Then2_routeStartAt5AndEndAtDestination() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1011",
            direction = 1,
            stopSequence = -1,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.setFirstBusStop(3)

        advanceUntilIdle()

        viewModel.setFirstBusStop(2)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1011RouteFromStop5,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1011FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1011Stop5Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun fullBusRouteAfterLoopingPointRevertedToFullRoute_fullRouteInBusRouteListAndOriginalRouteList() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1011",
            direction = 1,
            stopSequence = -1,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.setLoopingPointAsFirstBusStop()

        advanceUntilIdle()

        viewModel.setOriginalFirstBusStop()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1011FullRoute,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1011FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertFalse(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1011Stop0Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun fullBusRouteDualLoopContinuous_fullRouteInBusRouteListAndOriginalRouteList() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004",
            direction = 1,
            stopSequence = -1,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004FullRoute,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertFalse(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004Stop0Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun routeFromBeforeFirstLoopingPoint_routeEndAtLastStopOfFirstLoopingPoint() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004",
            direction = 1,
            stopSequence = 4,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004RouteFromStop4,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004Stop4Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun routeFromAfterFirstLoopingPointAndBeforeOrigin2ndVisit_routeEndAtLastStopOfSecondLoopingPoint() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004",
            direction = 1,
            stopSequence = 10,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004RouteFromStop10,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004Stop10Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun routeFromAfterOrigin2ndVisitAndBeforeSecondLoopingPoint_routeEndAtLastStopOfSecondLoopingPoint() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004",
            direction = 1,
            stopSequence = 17,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004RouteFromStop17,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004Stop17Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun routeFromAfterSecondLoopingPoint_routeEndAtDestination() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004",
            direction = 1,
            stopSequence = 21,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004RouteFromStop21,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004Stop21Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun firstStopUpdatedToBeforeFirstLoopingPoint_routeEndAtLastStopOfFirstLoopingPoint() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004",
            direction = 1,
            stopSequence = -1,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.setFirstBusStop(4)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004RouteFromStop4,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004Stop4Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun firstStopUpdatedFromBeforeFirstToBeforeSecondLoopingPoint_routeEndAtLastStopOfSecondLoopingPoint() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004",
            direction = 1,
            stopSequence = 4,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.setFirstBusStop(13)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004RouteFromStop17,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004Stop17Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun firstStopUpdatedFromBeforeToAfterSecondLoopingPoint_routeEndAtLastStopOfFirstLoopingPoint() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004",
            direction = 1,
            stopSequence = 17,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.setFirstBusStop(4)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004RouteFromStop21,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004Stop21Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun firstStopUpdatedFromAfterSecondToBeforeFirstLoopingPoint_routeEndAtLastStopOfFirstLoopingPoint() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004",
            direction = 1,
            stopSequence = 21,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.setFirstBusStop(9)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004RouteFromStop4,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertTrue(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004Stop4Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun busServiceUpdated_fullRouteInBusRouteListAndOriginalRouteList() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004",
            direction = 1,
            stopSequence = -1,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.updateBusService(
            BusServiceInfo(
                serviceNo = "1004A",
                direction = 1,
                originCode = "18331",
                destinationCode = "1000109"
            )
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004AFullRoute,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004AFullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertFalse(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004AStop0Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun busServiceUpdated2_fullRouteInBusRouteListAndOriginalRouteList() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004A",
            direction = 1,
            stopSequence = 2,
            showMap = false,
            showLiveBuses = false
        )

        advanceUntilIdle()

        viewModel.updateBusService(
            BusServiceInfo(
                serviceNo = "1004",
                direction = 1,
                originCode = "1000169",
                destinationCode = "1000169",
                loopDesc = "UTown"
            )
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            bus1004FullRoute,
            uiState.busRoute.map { it.second.busRouteInfo }
        )

        assertEquals(
            bus1004FullRoute,
            uiState.originalBusRoute.map { it.busRouteInfo }
        )

        assertFalse(uiState.truncated)
        assertFalse(uiState.firstStopIsStartOfLoopingPoint)

        assertEquals(
            bus1004Stop0Location,
            cameraPositionState.position.target
        )

        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun busLocationsSetToTrueButNoInternetConnection_busLocationsSetToFalseAndListIsEmpty() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = BusRouteViewModel(
            busRepository = TestBusRepository(),
            busArrivalGetter = NoInternetBusArrivalGetter(),
            locationRepository = StationaryLocationRepository(),
            serviceNo = "1004A",
            direction = 1,
            stopSequence = 2,
            showMap = true,
            showLiveBuses = true
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value

        assertTrue(uiState.liveBuses.isEmpty())

        assertFalse(uiState.showLiveBuses)

        Dispatchers.resetMain()
    }

}