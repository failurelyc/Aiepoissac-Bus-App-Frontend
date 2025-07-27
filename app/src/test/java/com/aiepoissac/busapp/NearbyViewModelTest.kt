package com.aiepoissac.busapp

import android.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import com.aiepoissac.busapp.data.bicycle.BicycleParking
import com.aiepoissac.busapp.data.businfo.BusRouteInfo
import com.aiepoissac.busapp.data.businfo.BusStopInfo
import com.aiepoissac.busapp.data.businfo.BusStopInfoWithBusRoutesInfo
import com.aiepoissac.busapp.ui.NearbyViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
import java.time.DayOfWeek

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class NearbyViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val busStop16181 =
        BusStopInfo(
            busStopCode = "16181",
            roadName = "Kent Ridge Cres",
            description = "Ctrl Lib",
            latitude = 1.2965671166736066,
            longitude = 103.77254247576647
        )

    private val busStop16181Location = LatLng(1.2965671166736066, 103.77254247576647)

    private val firstEightBusStopsNear16181 =
        listOf(
            BusStopInfo(
                busStopCode = "16181",
                roadName = "Kent Ridge Cres",
                description = "Ctrl Lib",
                latitude = 1.2965671166736066,
                longitude = 103.77254247576647
            ),
            BusStopInfo(
                busStopCode = "16189",
                roadName = "Kent Ridge Cres",
                description = "Information Technology",
                latitude = 1.2972176268958733,
                longitude = 103.7726877061283
            ),
            BusStopInfo(
                busStopCode = "1000119",
                roadName = "Kent Ridge Dr",
                description = "Ventus",
                latitude = 1.2953861326858667,
                longitude = 103.77053721002584
            ),
            BusStopInfo(
                busStopCode = "1000111",
                roadName = "Kent Ridge Dr",
                description = "LT13",
                latitude = 1.294752883527641,
                longitude = 103.7705872839438
            ),
            BusStopInfo(
                busStopCode = "16149",
                roadName = "Clementi Rd",
                description = "SDE3",
                latitude = 1.2978219573176526,
                longitude = 103.76993979514482
            ),
            BusStopInfo(
                busStopCode = "1000169",
                roadName = "Research Link",
                description = "COM 3",
                latitude = 1.2949181152540041,
                longitude = 103.77493256351714
            ),
            BusStopInfo(
                busStopCode = "16179",
                roadName = "Kent Ridge Cres",
                description = "Opp Yusof Ishak Hse",
                latitude = 1.2989822232795727,
                longitude = 103.77417544854725
            ),
            BusStopInfo(
                busStopCode = "16171",
                roadName = "Kent Ridge Cres",
                description = "Yusof Ishak Hse",
                latitude = 1.2989025862071986,
                longitude = 103.77438028222298
            )
        )

    private val firstEightBusStopsWithBusRoutesNear16181 =
        listOf(
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "16181",
                        roadName = "Kent Ridge Cres",
                        description = "Ctrl Lib",
                        latitude = 1.2965671166736066,
                        longitude = 103.77254247576647
                    ),
                busRoutesInfo =
                    listOf(
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
                            serviceNo = "1011",
                            direction = 1,
                            stopSequence = 6,
                            busStopCode = "16181",
                            distance = 2.6,
                            wdFirstBus = "0711",
                            wdLastBus = "2111",
                            satFirstBus = "0911",
                            satLastBus = "1511"
                        )
                    )
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "16189",
                        roadName = "Kent Ridge Cres",
                        description = "Information Technology",
                        latitude = 1.2972176268958733,
                        longitude = 103.7726877061283
                    ),
                busRoutesInfo =
                    listOf(
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
                        )
                    )
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "1000119",
                        roadName = "Kent Ridge Dr",
                        description = "Ventus",
                        latitude = 1.2953861326858667,
                        longitude = 103.77053721002584
                    ),
                busRoutesInfo =
                    listOf(
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
                        )
                    )
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "1000111",
                        roadName = "Kent Ridge Dr",
                        description = "LT13",
                        latitude = 1.294752883527641,
                        longitude = 103.7705872839438
                    ),
                busRoutesInfo =
                    listOf(
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
                        )
                    )
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo = BusStopInfo(
                    busStopCode = "16149",
                    roadName = "Clementi Rd",
                    description = "SDE3",
                    latitude = 1.2978219573176526,
                    longitude = 103.76993979514482
                ),
                busRoutesInfo =
                    listOf()
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "1000169",
                        roadName = "Research Link",
                        description = "COM 3",
                        latitude = 1.2949181152540041,
                        longitude = 103.77493256351714
                    ),
                busRoutesInfo =
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
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "16179",
                        roadName = "Kent Ridge Cres",
                        description = "Opp Yusof Ishak Hse",
                        latitude = 1.2989822232795727,
                        longitude = 103.77417544854725
                    ),
                busRoutesInfo =
                    listOf(
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
                        )
                    )
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "16171",
                        roadName = "Kent Ridge Cres",
                        description = "Yusof Ishak Hse",
                        latitude = 1.2989025862071986,
                        longitude = 103.77438028222298
                    ),
                busRoutesInfo =
                    listOf(
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
                            serviceNo = "1011",
                            direction = 1,
                            stopSequence = 5,
                            busStopCode = "16171",
                            distance = 2.2,
                            wdFirstBus = "0709",
                            wdLastBus = "2109",
                            satFirstBus = "0909",
                            satLastBus = "1509"
                        )
                    )
            )
        )

    private val firstFourBusStopsWithBusRoutesNear16181AtWeekday0010 =
        listOf(
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "16181",
                        roadName = "Kent Ridge Cres",
                        description = "Ctrl Lib",
                        latitude = 1.2965671166736066,
                        longitude = 103.77254247576647
                    ),
                busRoutesInfo =
                    listOf(
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
                        )
                    )
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "1000111",
                        roadName = "Kent Ridge Dr",
                        description = "LT13",
                        latitude = 1.294752883527641,
                        longitude = 103.7705872839438
                    ),
                busRoutesInfo =
                    listOf(
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
                        )
                    )
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "1000169",
                        roadName = "Research Link",
                        description = "COM 3",
                        latitude = 1.2949181152540041,
                        longitude = 103.77493256351714
                    ),
                busRoutesInfo =
                    listOf(
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
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "16171",
                        roadName = "Kent Ridge Cres",
                        description = "Yusof Ishak Hse",
                        latitude = 1.2989025862071986,
                        longitude = 103.77438028222298
                    ),
                busRoutesInfo =
                    listOf(
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
                        )
                    )
            )
        )

    private val firstSevenBusStopsWithBusRoutesNear16181AtSunday1200 =
        listOf(
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "16181",
                        roadName = "Kent Ridge Cres",
                        description = "Ctrl Lib",
                        latitude = 1.2965671166736066,
                        longitude = 103.77254247576647
                    ),
                busRoutesInfo =
                    listOf(
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
                        )
                    )
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "16189",
                        roadName = "Kent Ridge Cres",
                        description = "Information Technology",
                        latitude = 1.2972176268958733,
                        longitude = 103.7726877061283
                    ),
                busRoutesInfo =
                    listOf(
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
                        )
                    )
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "1000119",
                        roadName = "Kent Ridge Dr",
                        description = "Ventus",
                        latitude = 1.2953861326858667,
                        longitude = 103.77053721002584
                    ),
                busRoutesInfo =
                    listOf(
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
                        )
                    )
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "1000111",
                        roadName = "Kent Ridge Dr",
                        description = "LT13",
                        latitude = 1.294752883527641,
                        longitude = 103.7705872839438
                    ),
                busRoutesInfo =
                    listOf(
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
                        )
                    )
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "1000169",
                        roadName = "Research Link",
                        description = "COM 3",
                        latitude = 1.2949181152540041,
                        longitude = 103.77493256351714
                    ),
                busRoutesInfo =
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
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "16179",
                        roadName = "Kent Ridge Cres",
                        description = "Opp Yusof Ishak Hse",
                        latitude = 1.2989822232795727,
                        longitude = 103.77417544854725
                    ),
                busRoutesInfo =
                    listOf(
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
                        )
                    )
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "16171",
                        roadName = "Kent Ridge Cres",
                        description = "Yusof Ishak Hse",
                        latitude = 1.2989025862071986,
                        longitude = 103.77438028222298
                    ),
                busRoutesInfo =
                    listOf(
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
                        )
                    )
            )
        )


    private val busStop18331 =
        BusStopInfo(
            busStopCode = "18331",
            roadName = "Lower Kent Ridge Rd",
            description = "Kent Ridge Stn Exit A/NUH",
            latitude = 1.2948117103061263,
            longitude = 103.78437918054274
        )

    private val busStop18331Location = LatLng(1.2948117103061263, 103.78437918054274)

    private val firstFourBusStopsNear18331 =
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
                busStopCode = "18301",
                roadName = "Lower Kent Ridge Rd",
                description = "Lim Seng Tjoe Bldg (LT27)",
                latitude = 1.297360034928917,
                longitude = 103.78095045621575
            ),
            BusStopInfo(
                busStopCode = "18309",
                roadName = "Lower Kent Ridge Rd",
                description = "Blk S17",
                latitude = 1.2975513679805142,
                longitude = 103.78072079767756
            )
        )

    private val firstFourBusStopsWithBusRoutesNear18331 =
        listOf(
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "18331",
                        roadName = "Lower Kent Ridge Rd",
                        description = "Kent Ridge Stn Exit A/NUH",
                        latitude = 1.2948117103061263,
                        longitude = 103.78437918054274
                    ),
                busRoutesInfo =
                    listOf(
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
                            serviceNo = "1011",
                            direction = 1,
                            stopSequence = 1,
                            busStopCode = "18331",
                            distance = 0.8,
                            wdFirstBus = "0704",
                            wdLastBus = "2104",
                            satFirstBus = "0904",
                            satLastBus = "1504"
                        )

                    )
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "18339",
                        roadName = "Lower Kent Ridge Rd",
                        description = "Opp Kent Ridge Stn Exit A",
                        latitude = 1.2950030435540145,
                        longitude = 103.78460883908544
                    ),
                busRoutesInfo =
                    listOf(
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
                            serviceNo = "1004B",
                            direction = 1,
                            stopSequence = 4,
                            busStopCode = "18339",
                            distance = 2.1,
                            wdFirstBus = "1308",
                            wdLastBus = "2008"
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
                        )
                    )
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "18301",
                        roadName = "Lower Kent Ridge Rd",
                        description = "Lim Seng Tjoe Bldg (LT27)",
                        latitude = 1.297360034928917,
                        longitude = 103.78095045621575
                    ),
                busRoutesInfo =
                    listOf(
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
                            serviceNo = "1011",
                            direction = 1,
                            stopSequence = 2,
                            busStopCode = "18301",
                            distance = 1.4,
                            wdFirstBus = "0706",
                            wdLastBus = "2106",
                            satFirstBus = "0906",
                            satLastBus = "1506"
                        )
                    )
            ),
            BusStopInfoWithBusRoutesInfo(
                busStopInfo =
                    BusStopInfo(
                        busStopCode = "18309",
                        roadName = "Lower Kent Ridge Rd",
                        description = "Blk S17",
                        latitude = 1.2975513679805142,
                        longitude = 103.78072079767756
                    ),
                busRoutesInfo =
                    listOf(
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
                            serviceNo = "1004B",
                            direction = 1,
                            stopSequence = 3,
                            busStopCode = "18309",
                            distance = 1.6,
                            wdFirstBus = "1306",
                            wdLastBus = "2006"
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
                        )
                    )
            )
        )

    private val bicycleParkingNear18331 =
        listOf(
            BicycleParking(
                description = "BUS STOP 18339",
                latitude = 1.294861,
                longitude = 103.784685,
                rackType = "Yellow Box",
                rackCount = 13,
                shelterIndicator = "N"
            ),
            BicycleParking(
                description = "KENT RIDGE MRT A",
                latitude = 1.294169,
                longitude = 103.78445,
                rackType = "MRT_RACKS",
                rackCount = 40,
                shelterIndicator = "N"
            ),
            BicycleParking(
                description = "BUS STOP 15139",
                latitude = 1.292593,
                longitude = 103.784866,
                rackType = "Yellow Box",
                rackCount = 13,
                shelterIndicator = "N"
            ),
            BicycleParking(
                description = "KENT RIDGE MRT B",
                latitude = 1.292575,
                longitude = 103.78504,
                rackType = "MRT_RACKS",
                rackCount = 40,
                shelterIndicator = "N"
            )
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun initialiseToLocation_NearbyBusStopWithBusRoutesListIsCorrect() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = NearbyViewModel(
            busRepository = TestBusRepository(),
            locationRepository = StationaryLocationRepository(),
            bicycleParkingGetter = NoInternetBicycleParkingGetter(),
            point = busStop16181,
            isLiveLocation = false,
            distanceThreshold = 10000,
            busStopListLimit = 100
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            firstEightBusStopsNear16181,
            uiState.originalBusStopList.take(8).map { it.second }
        )

        assertEquals(
            firstEightBusStopsWithBusRoutesNear16181,
            uiState.busStopList.take(8).map { it.second }
        )

        assertEquals(
            busStop16181Location,
            cameraPositionState.position.target
        )

        assertTrue(uiState.showNearbyBusStopsOnMap)

        assertFalse(uiState.showBicycleParkingOnMap)

        assertFalse(uiState.showOnlyOperatingBusServices)

        assertFalse(uiState.searchingForBusStops)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun changeLocation_NearbyBusStopWithBusRoutesListIsCorrect() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = NearbyViewModel(
            busRepository = TestBusRepository(),
            locationRepository = StationaryLocationRepository(),
            bicycleParkingGetter = NoInternetBicycleParkingGetter(),
            point = busStop16181,
            isLiveLocation = false,
            distanceThreshold = 10000,
            busStopListLimit = 100
        )

        advanceUntilIdle()

        viewModel.updateLocation(busStop18331Location)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            firstFourBusStopsNear18331,
            uiState.originalBusStopList.take(4).map { it.second }
        )

        assertEquals(
            firstFourBusStopsWithBusRoutesNear18331,
            uiState.busStopList.take(4).map { it.second }
        )

        assertEquals(
            busStop18331Location,
            cameraPositionState.position.target
        )

        assertTrue(uiState.showNearbyBusStopsOnMap)

        assertFalse(uiState.showBicycleParkingOnMap)

        assertFalse(uiState.showOnlyOperatingBusServices)

        assertFalse(uiState.searchingForBusStops)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun switchOnBicycleParkingButNoInternet_BicycleParkingIsSwitchedOffAndListIsNull() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = NearbyViewModel(
            busRepository = TestBusRepository(),
            locationRepository = StationaryLocationRepository(),
            bicycleParkingGetter = NoInternetBicycleParkingGetter(),
            point = busStop18331,
            isLiveLocation = false,
            distanceThreshold = 10000,
            busStopListLimit = 100
        )

        advanceUntilIdle()

        viewModel.setShowBicycleParkingOnMap(true)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            firstFourBusStopsNear18331,
            uiState.originalBusStopList.take(4).map { it.second }
        )

        assertEquals(
            firstFourBusStopsWithBusRoutesNear18331,
            uiState.busStopList.take(4).map { it.second }
        )

        assertEquals(
            busStop18331Location,
            cameraPositionState.position.target
        )

        assertEquals(null, uiState.bicycleParkingList)

        assertTrue(uiState.showNearbyBusStopsOnMap)

        assertFalse(uiState.showBicycleParkingOnMap)

        assertFalse(uiState.showOnlyOperatingBusServices)

        assertFalse(uiState.searchingForBusStops)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun switchOnBicycleParking_BicycleParkingIsSwitchedOnAndListIsCorrect() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = NearbyViewModel(
            busRepository = TestBusRepository(),
            locationRepository = StationaryLocationRepository(),
            bicycleParkingGetter = TestBicycleParkingGetter(),
            point = busStop18331,
            isLiveLocation = false,
            distanceThreshold = 10000,
            busStopListLimit = 100
        )

        advanceUntilIdle()

        viewModel.setShowBicycleParkingOnMap(true)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            firstFourBusStopsNear18331,
            uiState.originalBusStopList.take(4).map { it.second }
        )

        assertEquals(
            firstFourBusStopsWithBusRoutesNear18331,
            uiState.busStopList.take(4).map { it.second }
        )

        assertEquals(
            busStop18331Location,
            cameraPositionState.position.target
        )

        assertEquals(
            bicycleParkingNear18331,
            uiState.bicycleParkingList?.map { it.second }
        )

        assertTrue(uiState.showNearbyBusStopsOnMap)

        assertTrue(uiState.showBicycleParkingOnMap)

        assertFalse(uiState.showOnlyOperatingBusServices)

        assertFalse(uiState.searchingForBusStops)

    }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class)
    @Test
    fun setTheTimeToATimePastOperatingHoursOfSomeServices_NearbyBusStopWithBusRoutesListIsCorrect() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = NearbyViewModel(
            busRepository = TestBusRepository(),
            locationRepository = StationaryLocationRepository(),
            bicycleParkingGetter = NoInternetBicycleParkingGetter(),
            point = busStop16181,
            isLiveLocation = false,
            distanceThreshold = 10000,
            busStopListLimit = 100
        )

        advanceUntilIdle()

        viewModel.setShowOnlyOperatingBusServices(true)
        viewModel.updateTime(
            TimePickerState(
                initialHour = 0,
                initialMinute = 10,
                is24Hour = true
            )
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            firstEightBusStopsNear16181,
            uiState.originalBusStopList.take(8).map { it.second }
        )

        assertEquals(
            firstFourBusStopsWithBusRoutesNear16181AtWeekday0010,
            uiState.busStopList.take(4).map { it.second }
        )

        assertEquals(
            busStop16181Location,
            cameraPositionState.position.target
        )

        assertTrue(uiState.showNearbyBusStopsOnMap)

        assertFalse(uiState.showBicycleParkingOnMap)

        assertTrue(uiState.showOnlyOperatingBusServices)

        assertFalse(uiState.searchingForBusStops)
    }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class)
    @Test
    fun setTheTimeToATimePastOperatingHoursOfAllServices_NearbyBusStopWithBusRoutesListIsEmpty() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = NearbyViewModel(
            busRepository = TestBusRepository(),
            locationRepository = StationaryLocationRepository(),
            bicycleParkingGetter = NoInternetBicycleParkingGetter(),
            point = busStop16181,
            isLiveLocation = false,
            distanceThreshold = 10000,
            busStopListLimit = 100
        )

        advanceUntilIdle()

        viewModel.setShowOnlyOperatingBusServices(true)
        viewModel.updateTime(
            TimePickerState(
                initialHour = 2,
                initialMinute = 0,
                is24Hour = true
            )
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            firstEightBusStopsNear16181,
            uiState.originalBusStopList.take(8).map { it.second }
        )

        assertEquals(
            listOf<Pair<Int, BusStopInfoWithBusRoutesInfo>>(),
            uiState.busStopList
        )

        assertEquals(
            busStop16181Location,
            cameraPositionState.position.target
        )

        assertTrue(uiState.showNearbyBusStopsOnMap)

        assertFalse(uiState.showBicycleParkingOnMap)

        assertTrue(uiState.showOnlyOperatingBusServices)

        assertFalse(uiState.searchingForBusStops)
    }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class)
    @Test
    fun setTheDayToADayWhereSomeServicesDoNotOperate_NearbyBusStopWithBusRoutesListIsCorrect() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = NearbyViewModel(
            busRepository = TestBusRepository(),
            locationRepository = StationaryLocationRepository(),
            bicycleParkingGetter = NoInternetBicycleParkingGetter(),
            point = busStop16181,
            isLiveLocation = false,
            distanceThreshold = 10000,
            busStopListLimit = 100
        )

        viewModel.setShowOnlyOperatingBusServices(true)

        advanceUntilIdle()

        viewModel.setDayOfWeek()

        advanceUntilIdle()

        viewModel.setDayOfWeek()

        advanceUntilIdle()

        viewModel.updateTime(
            TimePickerState(
                initialHour = 12,
                initialMinute = 0,
                is24Hour = true
            )
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            DayOfWeek.SUNDAY,
            uiState.dayOfWeek
        )

        assertEquals(
            firstEightBusStopsNear16181,
            uiState.originalBusStopList.take(8).map { it.second }
        )

        assertEquals(
            firstSevenBusStopsWithBusRoutesNear16181AtSunday1200,
            uiState.busStopList.take(7).map { it.second }
        )

        assertEquals(
            busStop16181Location,
            cameraPositionState.position.target
        )

        assertTrue(uiState.showNearbyBusStopsOnMap)

        assertFalse(uiState.showBicycleParkingOnMap)

        assertTrue(uiState.showOnlyOperatingBusServices)

        assertFalse(uiState.searchingForBusStops)
    }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class)
    @Test
    fun setTheTimeThenTurnOffTimeFilter_NearbyBusStopWithBusRoutesListIsCorrect() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = NearbyViewModel(
            busRepository = TestBusRepository(),
            locationRepository = StationaryLocationRepository(),
            bicycleParkingGetter = NoInternetBicycleParkingGetter(),
            point = busStop16181,
            isLiveLocation = false,
            distanceThreshold = 10000,
            busStopListLimit = 100
        )

        advanceUntilIdle()

        viewModel.setShowOnlyOperatingBusServices(true)
        viewModel.updateTime(
            TimePickerState(
                initialHour = 0,
                initialMinute = 10,
                is24Hour = true
            )
        )

        advanceUntilIdle()

        viewModel.setShowOnlyOperatingBusServices(false)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            firstEightBusStopsNear16181,
            uiState.originalBusStopList.take(8).map { it.second }
        )

        assertEquals(
            firstEightBusStopsWithBusRoutesNear16181,
            uiState.busStopList.take(8).map { it.second }
        )

        assertEquals(
            busStop16181Location,
            cameraPositionState.position.target
        )

        assertTrue(uiState.showNearbyBusStopsOnMap)

        assertFalse(uiState.showBicycleParkingOnMap)

        assertFalse(uiState.showOnlyOperatingBusServices)

        assertFalse(uiState.searchingForBusStops)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun cameraPositionChangedToNearbyBusStop_CameraPositionCorrect() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val viewModel = NearbyViewModel(
            busRepository = TestBusRepository(),
            locationRepository = StationaryLocationRepository(),
            bicycleParkingGetter = NoInternetBicycleParkingGetter(),
            point = busStop16181,
            isLiveLocation = false,
            distanceThreshold = 10000,
            busStopListLimit = 100
        )

        advanceUntilIdle()

        viewModel.setCameraPositionToLocation(point = busStop18331)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val cameraPositionState = viewModel.cameraPositionState.value

        assertEquals(
            firstEightBusStopsNear16181,
            uiState.originalBusStopList.take(8).map { it.second }
        )

        assertEquals(
            firstEightBusStopsWithBusRoutesNear16181,
            uiState.busStopList.take(8).map { it.second }
        )

        assertEquals(
            busStop18331Location,
            cameraPositionState.position.target
        )

        assertTrue(uiState.showNearbyBusStopsOnMap)

        assertFalse(uiState.showBicycleParkingOnMap)

        assertFalse(uiState.showOnlyOperatingBusServices)

        assertFalse(uiState.searchingForBusStops)

    }

}