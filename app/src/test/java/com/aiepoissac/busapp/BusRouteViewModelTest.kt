package com.aiepoissac.busapp

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.aiepoissac.busapp.data.businfo.BusRouteInfo
import com.aiepoissac.busapp.data.businfo.BusServiceInfo
import com.aiepoissac.busapp.ui.BusRouteViewModel
import com.google.android.gms.maps.model.LatLng
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
                distance = 0.0,
                wdFirstBus = "0700",
                wdLastBus = "1400",
                satFirstBus = "2350",
                satLastBus = "0010"
            ),
            BusRouteInfo(
                serviceNo = "1004A",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18301",
                distance = 0.5,
                wdFirstBus = "0702",
                wdLastBus = "1402",
                satFirstBus = "2352",
                satLastBus = "0012"
            ),
            BusRouteInfo(
                serviceNo = "1004A",
                direction = 1,
                stopSequence = 2,
                busStopCode = "18311",
                distance = 0.8,
                wdFirstBus = "0703",
                wdLastBus = "1403",
                satFirstBus = "2353",
                satLastBus = "0013"
            ),
            BusRouteInfo(
                serviceNo = "1004A",
                direction = 1,
                stopSequence = 3,
                busStopCode = "18321",
                distance = 1.2,
                wdFirstBus = "0704",
                wdLastBus = "1404",
                satFirstBus = "2354",
                satLastBus = "0014"
            ),
            BusRouteInfo(
                serviceNo = "1004A",
                direction = 1,
                stopSequence = 4,
                busStopCode = "1000109",
                distance = 1.9,
                wdFirstBus = "0706",
                wdLastBus = "1406",
                satFirstBus = "2356",
                satLastBus = "0016"
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
                distance = 0.0,
                wdFirstBus = "0703",
                wdLastBus = "1403",
                satFirstBus = "2353",
                satLastBus = "0013"
            ),
            BusRouteInfo(
                serviceNo = "1004A",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18321",
                distance = 0.4,
                wdFirstBus = "0704",
                wdLastBus = "1404",
                satFirstBus = "2354",
                satLastBus = "0014"
            ),
            BusRouteInfo(
                serviceNo = "1004A",
                direction = 1,
                stopSequence = 2,
                busStopCode = "1000109",
                distance = 1.1,
                wdFirstBus = "0706",
                wdLastBus = "1406",
                satFirstBus = "2356",
                satLastBus = "0016"
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
                distance = 0.0,
                wdFirstBus = "0700",
                wdLastBus = "2100",
                satFirstBus = "0900",
                satLastBus = "1500"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18331",
                distance = 0.8,
                wdFirstBus = "0704",
                wdLastBus = "2104",
                satFirstBus = "0904",
                satLastBus = "1504"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 2,
                busStopCode = "18301",
                distance = 1.4,
                wdFirstBus = "0706",
                wdLastBus = "2106",
                satFirstBus = "0906",
                satLastBus = "1506"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 3,
                busStopCode = "18311",
                distance = 1.6,
                wdFirstBus = "0707",
                wdLastBus = "2107",
                satFirstBus = "0907",
                satLastBus = "1507"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 4,
                busStopCode = "18321",
                distance = 2.0,
                wdFirstBus = "0708",
                wdLastBus = "2108",
                satFirstBus = "0908",
                satLastBus = "1508"
            ),

            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 5,
                busStopCode = "16171",
                distance = 2.2,
                wdFirstBus = "0709",
                wdLastBus = "2109",
                satFirstBus = "0909",
                satLastBus = "1509"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 6,
                busStopCode = "16181",
                distance = 2.6,
                wdFirstBus = "0711",
                wdLastBus = "2111",
                satFirstBus = "0911",
                satLastBus = "1511"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 7,
                busStopCode = "16141",
                distance = 3.1,
                wdFirstBus = "0713",
                wdLastBus = "2113",
                satFirstBus = "0913",
                satLastBus = "1513"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 8,
                busStopCode = "16151",
                distance = 3.4,
                wdFirstBus = "0714",
                wdLastBus = "2114",
                satFirstBus = "0914",
                satLastBus = "1514"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 9,
                busStopCode = "16161",
                distance = 3.9,
                wdFirstBus = "0717",
                wdLastBus = "2117",
                satFirstBus = "0917",
                satLastBus = "1517"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 10,
                busStopCode = "18329",
                distance = 4.3,
                wdFirstBus = "0719",
                wdLastBus = "2119",
                satFirstBus = "0919",
                satLastBus = "1519"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 11,
                busStopCode = "18319",
                distance = 4.6,
                wdFirstBus = "0720",
                wdLastBus = "2120",
                satFirstBus = "0920",
                satLastBus = "1520"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 12,
                busStopCode = "18309",
                distance = 4.9,
                wdFirstBus = "0721",
                wdLastBus = "2121",
                satFirstBus = "0921",
                satLastBus = "1521"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 13,
                busStopCode = "18339",
                distance = 5.5,
                wdFirstBus = "0723",
                wdLastBus = "2123",
                satFirstBus = "0923",
                satLastBus = "1523"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 14,
                busStopCode = "1000159",
                distance = 6.2,
                wdFirstBus = "0726",
                wdLastBus = "2126",
                satFirstBus = "0926",
                satLastBus = "1526"
            )
        )

    private val bus1011RouteFromStop0 =
        listOf(
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 0,
                busStopCode = "1000151",
                distance = 0.0,
                wdFirstBus = "0700",
                wdLastBus = "2100",
                satFirstBus = "0900",
                satLastBus = "1500"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18331",
                distance = 0.8,
                wdFirstBus = "0704",
                wdLastBus = "2104",
                satFirstBus = "0904",
                satLastBus = "1504"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 2,
                busStopCode = "18301",
                distance = 1.4,
                wdFirstBus = "0706",
                wdLastBus = "2106",
                satFirstBus = "0906",
                satLastBus = "1506"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 3,
                busStopCode = "18311",
                distance = 1.6,
                wdFirstBus = "0707",
                wdLastBus = "2107",
                satFirstBus = "0907",
                satLastBus = "1507"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 4,
                busStopCode = "18321",
                distance = 2.0,
                wdFirstBus = "0708",
                wdLastBus = "2108",
                satFirstBus = "0908",
                satLastBus = "1508"
            ),

            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 5,
                busStopCode = "16171",
                distance = 2.2,
                wdFirstBus = "0709",
                wdLastBus = "2109",
                satFirstBus = "0909",
                satLastBus = "1509"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 6,
                busStopCode = "16181",
                distance = 2.6,
                wdFirstBus = "0711",
                wdLastBus = "2111",
                satFirstBus = "0911",
                satLastBus = "1511"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 7,
                busStopCode = "16141",
                distance = 3.1,
                wdFirstBus = "0713",
                wdLastBus = "2113",
                satFirstBus = "0913",
                satLastBus = "1513"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 8,
                busStopCode = "16151",
                distance = 3.4,
                wdFirstBus = "0714",
                wdLastBus = "2114",
                satFirstBus = "0914",
                satLastBus = "1514"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 9,
                busStopCode = "16161",
                distance = 3.9,
                wdFirstBus = "0717",
                wdLastBus = "2117",
                satFirstBus = "0917",
                satLastBus = "1517"
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
                distance = 0.0,
                wdFirstBus = "0707",
                wdLastBus = "2107",
                satFirstBus = "0907",
                satLastBus = "1507"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18321",
                distance = 0.4,
                wdFirstBus = "0708",
                wdLastBus = "2108",
                satFirstBus = "0908",
                satLastBus = "1508"
            ),

            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 2,
                busStopCode = "16171",
                distance = 0.6,
                wdFirstBus = "0709",
                wdLastBus = "2109",
                satFirstBus = "0909",
                satLastBus = "1509"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 3,
                busStopCode = "16181",
                distance = 1.0,
                wdFirstBus = "0711",
                wdLastBus = "2111",
                satFirstBus = "0911",
                satLastBus = "1511"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 4,
                busStopCode = "16141",
                distance = 1.5,
                wdFirstBus = "0713",
                wdLastBus = "2113",
                satFirstBus = "0913",
                satLastBus = "1513"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 5,
                busStopCode = "16151",
                distance = 1.8,
                wdFirstBus = "0714",
                wdLastBus = "2114",
                satFirstBus = "0914",
                satLastBus = "1514"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 6,
                busStopCode = "16161",
                distance = 2.3,
                wdFirstBus = "0717",
                wdLastBus = "2117",
                satFirstBus = "0917",
                satLastBus = "1517"
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
                distance = 0.0,
                wdFirstBus = "0709",
                wdLastBus = "2109",
                satFirstBus = "0909",
                satLastBus = "1509"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 1,
                busStopCode = "16181",
                distance = 0.4,
                wdFirstBus = "0711",
                wdLastBus = "2111",
                satFirstBus = "0911",
                satLastBus = "1511"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 2,
                busStopCode = "16141",
                distance = 0.9,
                wdFirstBus = "0713",
                wdLastBus = "2113",
                satFirstBus = "0913",
                satLastBus = "1513"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 3,
                busStopCode = "16151",
                distance = 1.2,
                wdFirstBus = "0714",
                wdLastBus = "2114",
                satFirstBus = "0914",
                satLastBus = "1514"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 4,
                busStopCode = "16161",
                distance = 1.7,
                wdFirstBus = "0717",
                wdLastBus = "2117",
                satFirstBus = "0917",
                satLastBus = "1517"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 5,
                busStopCode = "18329",
                distance = 2.1,
                wdFirstBus = "0719",
                wdLastBus = "2119",
                satFirstBus = "0919",
                satLastBus = "1519"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 6,
                busStopCode = "18319",
                distance = 2.4,
                wdFirstBus = "0720",
                wdLastBus = "2120",
                satFirstBus = "0920",
                satLastBus = "1520"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 7,
                busStopCode = "18309",
                distance = 2.7,
                wdFirstBus = "0721",
                wdLastBus = "2121",
                satFirstBus = "0921",
                satLastBus = "1521"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 8,
                busStopCode = "18339",
                distance = 3.3,
                wdFirstBus = "0723",
                wdLastBus = "2123",
                satFirstBus = "0923",
                satLastBus = "1523"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 9,
                busStopCode = "1000159",
                distance = 4.0,
                wdFirstBus = "0726",
                wdLastBus = "2126",
                satFirstBus = "0926",
                satLastBus = "1526"
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
                distance = 0.0,
                wdFirstBus = "0721",
                wdLastBus = "2121",
                satFirstBus = "0921",
                satLastBus = "1521"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18339",
                distance = 0.6,
                wdFirstBus = "0723",
                wdLastBus = "2123",
                satFirstBus = "0923",
                satLastBus = "1523"
            ),
            BusRouteInfo(
                serviceNo = "1011",
                direction = 1,
                stopSequence = 2,
                busStopCode = "1000159",
                distance = 1.3,
                wdFirstBus = "0726",
                wdLastBus = "2126",
                satFirstBus = "0926",
                satLastBus = "1526"
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
                distance = 0.0,
                wdFirstBus = "0600",
                wdLastBus = "2330",
                satFirstBus = "0700",
                satLastBus = "2330",
                sunFirstBus = "0900",
                sunLastBus = "2230"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 1,
                busStopCode = "1000151",
                distance = 0.8,
                wdFirstBus = "0603",
                wdLastBus = "2333",
                satFirstBus = "0703",
                satLastBus = "2333",
                sunFirstBus = "0903",
                sunLastBus = "2233"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 2,
                busStopCode = "18331",
                distance = 1.6,
                wdFirstBus = "0606",
                wdLastBus = "2336",
                satFirstBus = "0706",
                satLastBus = "2336",
                sunFirstBus = "0906",
                sunLastBus = "2236"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 3,
                busStopCode = "18301",
                distance = 2.2,
                wdFirstBus = "0608",
                wdLastBus = "2338",
                satFirstBus = "0708",
                satLastBus = "2338",
                sunFirstBus = "0908",
                sunLastBus = "2238"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 4,
                busStopCode = "18311",
                distance = 2.4,
                wdFirstBus = "0609",
                wdLastBus = "2339",
                satFirstBus = "0709",
                satLastBus = "2339",
                sunFirstBus = "0909",
                sunLastBus = "2239"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 5,
                busStopCode = "18321",
                distance = 2.8,
                wdFirstBus = "0610",
                wdLastBus = "2340",
                satFirstBus = "0710",
                satLastBus = "2340",
                sunFirstBus = "0910",
                sunLastBus = "2240"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 6,
                busStopCode = "16161",
                distance = 3.3,
                wdFirstBus = "0612",
                wdLastBus = "2342",
                satFirstBus = "0712",
                satLastBus = "2342",
                sunFirstBus = "0912",
                sunLastBus = "2242"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 7,
                busStopCode = "1000109",
                distance = 3.7,
                wdFirstBus = "0613",
                wdLastBus = "2343",
                satFirstBus = "0713",
                satLastBus = "2343",
                sunFirstBus = "0913",
                sunLastBus = "2243"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 8,
                busStopCode = "18329",
                distance = 4.7,
                wdFirstBus = "0616",
                wdLastBus = "2346",
                satFirstBus = "0716",
                satLastBus = "2346",
                sunFirstBus = "0916",
                sunLastBus = "2246"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 9,
                busStopCode = "18319",
                distance = 5.0,
                wdFirstBus = "0617",
                wdLastBus = "2347",
                satFirstBus = "0717",
                satLastBus = "2347",
                sunFirstBus = "0917",
                sunLastBus = "2247"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 10,
                busStopCode = "18309",
                distance = 5.3,
                wdFirstBus = "0718",
                wdLastBus = "2348",
                satFirstBus = "0618",
                satLastBus = "2348",
                sunFirstBus = "0918",
                sunLastBus = "2248"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 11,
                busStopCode = "18339",
                distance = 5.9,
                wdFirstBus = "0620",
                wdLastBus = "2350",
                satFirstBus = "0720",
                satLastBus = "2350",
                sunFirstBus = "0920",
                sunLastBus = "2250"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 12,
                busStopCode = "1000159",
                distance = 6.6,
                wdFirstBus = "0624",
                wdLastBus = "2354",
                satFirstBus = "0724",
                satLastBus = "2354",
                sunFirstBus = "0924",
                sunLastBus = "2254"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 13,
                busStopCode = "1000169",
                distance = 7.5,
                wdFirstBus = "0627",
                wdLastBus = "2357",
                satFirstBus = "0727",
                satLastBus = "2357",
                sunFirstBus = "0927",
                sunLastBus = "2257"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 14,
                busStopCode = "1000139",
                distance = 7.8,
                wdFirstBus = "0630",
                wdLastBus = "0000",
                satFirstBus = "0730",
                satLastBus = "0000",
                sunFirstBus = "0930",
                sunLastBus = "2300"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 15,
                busStopCode = "1000129",
                distance = 8.1,
                wdFirstBus = "0632",
                wdLastBus = "0002",
                satFirstBus = "0732",
                satLastBus = "0002",
                sunFirstBus = "0932",
                sunLastBus = "2302"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 16,
                busStopCode = "1000119",
                distance = 8.5,
                wdFirstBus = "0633",
                wdLastBus = "0003",
                satFirstBus = "0733",
                satLastBus = "0003",
                sunFirstBus = "0933",
                sunLastBus = "2303"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 17,
                busStopCode = "16189",
                distance = 8.8,
                wdFirstBus = "0635",
                wdLastBus = "0005",
                satFirstBus = "0735",
                satLastBus = "0005",
                sunFirstBus = "0935",
                sunLastBus = "2305"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 18,
                busStopCode = "16179",
                distance = 9.1,
                wdFirstBus = "0636",
                wdLastBus = "0006",
                satFirstBus = "0736",
                satLastBus = "0006",
                sunFirstBus = "0936",
                sunLastBus = "2306"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 19,
                busStopCode = "16161",
                distance = 9.5,
                wdFirstBus = "0638",
                wdLastBus = "0008",
                satFirstBus = "0738",
                satLastBus = "0008",
                sunFirstBus = "0938",
                sunLastBus = "2308"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 20,
                busStopCode = "1000109",
                distance = 9.9,
                wdFirstBus = "0610",
                wdLastBus = "0010",
                satFirstBus = "0740",
                satLastBus = "0010",
                sunFirstBus = "0940",
                sunLastBus = "2310"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 21,
                busStopCode = "16171",
                distance = 10.7,
                wdFirstBus = "0613",
                wdLastBus = "0013",
                satFirstBus = "0743",
                satLastBus = "0013",
                sunFirstBus = "0943",
                sunLastBus = "2313"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 22,
                busStopCode = "16181",
                distance = 11.1,
                wdFirstBus = "0614",
                wdLastBus = "0014",
                satFirstBus = "0744",
                satLastBus = "0014",
                sunFirstBus = "0944",
                sunLastBus = "2314"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 23,
                busStopCode = "1000111",
                distance = 11.5,
                wdFirstBus = "0616",
                wdLastBus = "0016",
                satFirstBus = "0746",
                satLastBus = "0016",
                sunFirstBus = "0946",
                sunLastBus = "2316"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 24,
                busStopCode = "1000121",
                distance = 11.7,
                wdFirstBus = "0617",
                wdLastBus = "0017",
                satFirstBus = "0747",
                satLastBus = "0017",
                sunFirstBus = "0947",
                sunLastBus = "2317"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 25,
                busStopCode = "1000131",
                distance = 12.3,
                wdFirstBus = "0619",
                wdLastBus = "0019",
                satFirstBus = "0749",
                satLastBus = "0019",
                sunFirstBus = "0949",
                sunLastBus = "2319"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 26,
                busStopCode = "1000169",
                distance = 12.5,
                wdFirstBus = "0620",
                wdLastBus = "0020",
                satFirstBus = "0750",
                satLastBus = "0020",
                sunFirstBus = "0950",
                sunLastBus = "2320"
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
                distance = 0.0,
                wdFirstBus = "0609",
                wdLastBus = "2339",
                satFirstBus = "0709",
                satLastBus = "2339",
                sunFirstBus = "0909",
                sunLastBus = "2239"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18321",
                distance = 0.4,
                wdFirstBus = "0610",
                wdLastBus = "2340",
                satFirstBus = "0710",
                satLastBus = "2340",
                sunFirstBus = "0910",
                sunLastBus = "2240"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 2,
                busStopCode = "16161",
                distance = 0.9,
                wdFirstBus = "0612",
                wdLastBus = "2342",
                satFirstBus = "0712",
                satLastBus = "2342",
                sunFirstBus = "0912",
                sunLastBus = "2242",
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 3,
                busStopCode = "1000109",
                distance = 1.3,
                wdFirstBus = "0613",
                wdLastBus = "2343",
                satFirstBus = "0713",
                satLastBus = "2343",
                sunFirstBus = "0913",
                sunLastBus = "2243"
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
                distance = 0.0,
                wdFirstBus = "0718",
                wdLastBus = "2348",
                satFirstBus = "0618",
                satLastBus = "2348",
                sunFirstBus = "0918",
                sunLastBus = "2248"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18339",
                distance = 0.6,
                wdFirstBus = "0620",
                wdLastBus = "2350",
                satFirstBus = "0720",
                satLastBus = "2350",
                sunFirstBus = "0920",
                sunLastBus = "2250"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 2,
                busStopCode = "1000159",
                distance = 1.3,
                wdFirstBus = "0624",
                wdLastBus = "2354",
                satFirstBus = "0724",
                satLastBus = "2354",
                sunFirstBus = "0924",
                sunLastBus = "2254"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 3,
                busStopCode = "1000169",
                distance = 2.2,
                wdFirstBus = "0627",
                wdLastBus = "2357",
                satFirstBus = "0727",
                satLastBus = "2357",
                sunFirstBus = "0927",
                sunLastBus = "2257"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 4,
                busStopCode = "1000139",
                distance = 2.5,
                wdFirstBus = "0630",
                wdLastBus = "0000",
                satFirstBus = "0730",
                satLastBus = "0000",
                sunFirstBus = "0930",
                sunLastBus = "2300"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 5,
                busStopCode = "1000129",
                distance = 2.8,
                wdFirstBus = "0632",
                wdLastBus = "0002",
                satFirstBus = "0732",
                satLastBus = "0002",
                sunFirstBus = "0932",
                sunLastBus = "2302"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 6,
                busStopCode = "1000119",
                distance = 3.2,
                wdFirstBus = "0633",
                wdLastBus = "0003",
                satFirstBus = "0733",
                satLastBus = "0003",
                sunFirstBus = "0933",
                sunLastBus = "2303"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 7,
                busStopCode = "16189",
                distance = 3.5,
                wdFirstBus = "0635",
                wdLastBus = "0005",
                satFirstBus = "0735",
                satLastBus = "0005",
                sunFirstBus = "0935",
                sunLastBus = "2305"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 8,
                busStopCode = "16179",
                distance = 3.8,
                wdFirstBus = "0636",
                wdLastBus = "0006",
                satFirstBus = "0736",
                satLastBus = "0006",
                sunFirstBus = "0936",
                sunLastBus = "2306"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 9,
                busStopCode = "16161",
                distance = 4.2,
                wdFirstBus = "0638",
                wdLastBus = "0008",
                satFirstBus = "0738",
                satLastBus = "0008",
                sunFirstBus = "0938",
                sunLastBus = "2308"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 10,
                busStopCode = "1000109",
                distance = 4.6,
                wdFirstBus = "0610",
                wdLastBus = "0010",
                satFirstBus = "0740",
                satLastBus = "0010",
                sunFirstBus = "0940",
                sunLastBus = "2310"
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
                distance = 0.0,
                wdFirstBus = "0635",
                wdLastBus = "0005",
                satFirstBus = "0735",
                satLastBus = "0005",
                sunFirstBus = "0935",
                sunLastBus = "2305"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 1,
                busStopCode = "16179",
                distance = 0.3,
                wdFirstBus = "0636",
                wdLastBus = "0006",
                satFirstBus = "0736",
                satLastBus = "0006",
                sunFirstBus = "0936",
                sunLastBus = "2306"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 2,
                busStopCode = "16161",
                distance = 0.7,
                wdFirstBus = "0638",
                wdLastBus = "0008",
                satFirstBus = "0738",
                satLastBus = "0008",
                sunFirstBus = "0938",
                sunLastBus = "2308"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 3,
                busStopCode = "1000109",
                distance = 1.1,
                wdFirstBus = "0610",
                wdLastBus = "0010",
                satFirstBus = "0740",
                satLastBus = "0010",
                sunFirstBus = "0940",
                sunLastBus = "2310"
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
                distance = 0.0,
                wdFirstBus = "0613",
                wdLastBus = "0013",
                satFirstBus = "0743",
                satLastBus = "0013",
                sunFirstBus = "0943",
                sunLastBus = "2313"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 1,
                busStopCode = "16181",
                distance = 0.4,
                wdFirstBus = "0614",
                wdLastBus = "0014",
                satFirstBus = "0744",
                satLastBus = "0014",
                sunFirstBus = "0944",
                sunLastBus = "2314"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 2,
                busStopCode = "1000111",
                distance = 0.8,
                wdFirstBus = "0616",
                wdLastBus = "0016",
                satFirstBus = "0746",
                satLastBus = "0016",
                sunFirstBus = "0946",
                sunLastBus = "2316"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 3,
                busStopCode = "1000121",
                distance = 1.0,
                wdFirstBus = "0617",
                wdLastBus = "0017",
                satFirstBus = "0747",
                satLastBus = "0017",
                sunFirstBus = "0947",
                sunLastBus = "2317"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 4,
                busStopCode = "1000131",
                distance = 1.6,
                wdFirstBus = "0619",
                wdLastBus = "0019",
                satFirstBus = "0749",
                satLastBus = "0019",
                sunFirstBus = "0949",
                sunLastBus = "2319"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 5,
                busStopCode = "1000169",
                distance = 1.8,
                wdFirstBus = "0620",
                wdLastBus = "0020",
                satFirstBus = "0750",
                satLastBus = "0020",
                sunFirstBus = "0950",
                sunLastBus = "2320"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 5,
                busStopCode = "1000169",
                distance = 1.8,
                wdFirstBus = "0600",
                wdLastBus = "2330",
                satFirstBus = "0700",
                satLastBus = "2330",
                sunFirstBus = "0900",
                sunLastBus = "2230"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 6,
                busStopCode = "1000151",
                distance = 2.6,
                wdFirstBus = "0603",
                wdLastBus = "2333",
                satFirstBus = "0703",
                satLastBus = "2333",
                sunFirstBus = "0903",
                sunLastBus = "2233"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 7,
                busStopCode = "18331",
                distance = 3.4,
                wdFirstBus = "0606",
                wdLastBus = "2336",
                satFirstBus = "0706",
                satLastBus = "2336",
                sunFirstBus = "0906",
                sunLastBus = "2236"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 8,
                busStopCode = "18301",
                distance = 4.0,
                wdFirstBus = "0608",
                wdLastBus = "2338",
                satFirstBus = "0708",
                satLastBus = "2338",
                sunFirstBus = "0908",
                sunLastBus = "2238"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 9,
                busStopCode = "18311",
                distance = 4.2,
                wdFirstBus = "0609",
                wdLastBus = "2339",
                satFirstBus = "0709",
                satLastBus = "2339",
                sunFirstBus = "0909",
                sunLastBus = "2239"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 10,
                busStopCode = "18321",
                distance = 4.6,
                wdFirstBus = "0610",
                wdLastBus = "2340",
                satFirstBus = "0710",
                satLastBus = "2340",
                sunFirstBus = "0910",
                sunLastBus = "2240"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 11,
                busStopCode = "16161",
                distance = 5.1,
                wdFirstBus = "0612",
                wdLastBus = "2342",
                satFirstBus = "0712",
                satLastBus = "2342",
                sunFirstBus = "0912",
                sunLastBus = "2242"
            ),
            BusRouteInfo(
                serviceNo = "1004",
                direction = 1,
                stopSequence = 12,
                busStopCode = "1000109",
                distance = 5.5,
                wdFirstBus = "0613",
                wdLastBus = "2343",
                satFirstBus = "0713",
                satLastBus = "2343",
                sunFirstBus = "0913",
                sunLastBus = "2243"
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