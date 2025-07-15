package com.aiepoissac.busapp

import com.aiepoissac.busapp.data.businfo.BusRepository
import com.aiepoissac.busapp.data.businfo.BusRouteInfo
import com.aiepoissac.busapp.data.businfo.BusRouteInfoWithBusStopInfo
import com.aiepoissac.busapp.data.businfo.BusServiceInfo
import com.aiepoissac.busapp.data.businfo.BusServiceInfoWithBusStopInfo
import com.aiepoissac.busapp.data.businfo.BusStopInfo
import com.aiepoissac.busapp.data.businfo.BusStopInfoWithBusRoutesInfo
import com.aiepoissac.busapp.data.businfo.MRTStation
import com.aiepoissac.busapp.data.businfo.PlannedBusRouteInfo
import com.aiepoissac.busapp.data.businfo.PlannedBusRouteInfoWithBusStopInfo

class TestBusRepository(
    private val busServiceList: List<BusServiceInfo> =
        listOf(
            BusServiceInfo(
                serviceNo = "1004A",
                direction = 1,
                originCode = "18331",
                destinationCode = "1000109"
            ),
            BusServiceInfo(
                serviceNo = "1004B",
                direction = 1,
                originCode = "1000109",
                destinationCode = "18339",
            ),
            BusServiceInfo(
                serviceNo = "1004",
                direction = 1,
                originCode = "1000169",
                destinationCode = "1000169",
                loopDesc = "UTown"
            ),
            BusServiceInfo(
                serviceNo = "1011",
                direction = 1,
                originCode = "1000151",
                destinationCode = "1000159"
            ),

        ),
    private val busRouteList: List<BusRouteInfo> =
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
            ),
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
            ),
            BusRouteInfo(
                serviceNo = "1004B",
                direction = 1,
                stopSequence = 0,
                busStopCode = "1000109",
                distance = 0.0
            ),
            BusRouteInfo(
                serviceNo = "1004B",
                direction = 1,
                stopSequence = 1,
                busStopCode = "18329",
                distance = 0.9
            ),
            BusRouteInfo(
                serviceNo = "1004B",
                direction = 1,
                stopSequence = 2,
                busStopCode = "18319",
                distance = 1.3
            ),
            BusRouteInfo(
                serviceNo = "1004B",
                direction = 1,
                stopSequence = 3,
                busStopCode = "18309",
                distance = 1.6
            ),
            BusRouteInfo(
                serviceNo = "1004B",
                direction = 1,
                stopSequence = 4,
                busStopCode = "18339",
                distance = 2.1
            ),
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
            ),

        ),
    private val busStopList: List<BusStopInfo> =
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
            ),
            BusStopInfo(
                busStopCode = "18311",
                roadName = "Lower Kent Ridge Rd",
                description = "Blk S12",
                latitude = 1.2971385871545937,
                longitude = 103.7787816322459
            ),
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
                busStopCode = "16169",
                roadName = "Kent Ridge Cres",
                description = "NUS Raffles Hall",
                latitude = 1.300990117538751,
                longitude = 103.77271004122734
            ),
            BusStopInfo(
                busStopCode = "16161",
                roadName = "Kent Ridge Cres",
                description = "NUS Museums",
                latitude = 1.3010758029196525,
                longitude = 103.77368891080535
            ),
            BusStopInfo(
                busStopCode = "16151",
                roadName = "Clementi Rd",
                description = "The Japanese Pr Sch",
                latitude = 1.3007452197593086,
                longitude = 103.769926979676
            ),
            BusStopInfo(
                busStopCode = "18329",
                roadName = "Clementi Rd",
                description = "Coll of Design & Engrg",
                latitude = 1.3006030993121336,
                longitude = 103.77017106068999
            ),
            BusStopInfo(
                busStopCode = "16141",
                roadName = "Clementi Rd",
                description = "Tentera Diraja Mque",
                latitude = 1.2978580354183633,
                longitude = 103.76959245432683
            ),
            BusStopInfo(
                busStopCode = "16149",
                roadName = "Clementi Rd",
                description = "SDE3",
                latitude = 1.2978219573176526,
                longitude = 103.76993979514482
            ),
            BusStopInfo(
                busStopCode = "16009",
                roadName = "Clementi Rd",
                description = "Kent Ridge Ter",
                latitude = 1.2942547560928301,
                longitude = 103.76987914606597
            ),
            BusStopInfo(
                busStopCode = "16189",
                roadName = "Kent Ridge Cres",
                description = "Information Technology",
                latitude = 1.2972176268958733,
                longitude = 103.7726877061283
            ),
            BusStopInfo(
                busStopCode = "16181",
                roadName = "Kent Ridge Cres",
                description = "Ctrl Lib",
                latitude = 1.2965671166736066,
                longitude = 103.77254247576647
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
            ),
            BusStopInfo(
                busStopCode = "1000109",
                roadName = "College Link Rd",
                description = "University Town",
                latitude = 1.3035640377827036,
                longitude = 103.77441333021167
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
                busStopCode = "1000129",
                roadName = "Kent Ridge Dr",
                description = "Opp NUSS",
                latitude = 1.2932864089899943,
                longitude = 103.77243577306346
            ),
            BusStopInfo(
                busStopCode = "1000121",
                roadName = "Kent Ridge Dr",
                description = "AS 5",
                latitude = 1.2934849444163823,
                longitude = 103.77192263374366
            ),
            BusStopInfo(
                busStopCode = "1000139",
                roadName = "Business Link",
                description = "Opp HSSML",
                latitude = 1.2929864002314433,
                longitude = 103.77510132358289
            ),
            BusStopInfo(
                busStopCode = "1000131",
                roadName = "Business Link",
                description = "Biz 2",
                latitude = 1.293431694682836,
                longitude = 103.77511296942437
            ),
            BusStopInfo(
                busStopCode = "1000159",
                roadName = "Prince George's Pk",
                description = "PGP Foyer",
                latitude = 1.2909442886549687,
                longitude = 103.78110146793922
            ),
            BusStopInfo(
                busStopCode = "1000151",
                roadName = "Prince George's Pk",
                description = "PGP",
                latitude = 1.2918202990643677,
                longitude = 103.78042627545256
            ),
            BusStopInfo(
                busStopCode = "1000169",
                roadName = "Research Link",
                description = "COM 3",
                latitude = 1.2949181152540041,
                longitude = 103.77493256351714
            )

        )
): BusRepository {

    override suspend fun insertBusService(busServiceInfo: BusServiceInfo) {
        throw UnsupportedOperationException("Read only test repository")
    }

    override suspend fun deleteBusService(busServiceInfo: BusServiceInfo) {
        throw UnsupportedOperationException("Read only test repository")
    }

    override suspend fun updateBusService(busServiceInfo: BusServiceInfo) {
        throw UnsupportedOperationException("Read only test repository")
    }

    override suspend fun deleteAllBusServices() {
        throw UnsupportedOperationException("Read only test repository")
    }

    override suspend fun getBusServicesCount(): Int {
        return busServiceList.size
    }

    override suspend fun getBusService(serviceNo: String): List<BusServiceInfoWithBusStopInfo> {
        return busServiceList
            .filter { it.serviceNo.startsWith(serviceNo.filter { it.isDigit() }) }
            .map {
                BusServiceInfoWithBusStopInfo(
                    busServiceInfo = it,
                    originBusStopInfo = busStopList.find { busStopInfo ->
                        busStopInfo.busStopCode == it.originCode
                    }!!,
                    destinationBusStopInfo = busStopList.find { busStopInfo ->
                        busStopInfo.busStopCode == it.destinationCode
                    }!!,
                )
            }
    }

    override suspend fun getBusService(serviceNo: String, direction: Int): BusServiceInfo? {
        return busServiceList.find {
            it.serviceNo == serviceNo && it.direction == direction
        }
    }

    override suspend fun insertBusRoute(busRouteInfo: BusRouteInfo) {
        throw UnsupportedOperationException("Read only test repository")
    }

    override suspend fun deleteBusRoute(busRouteInfo: BusRouteInfo) {
        throw UnsupportedOperationException("Read only test repository")
    }

    override suspend fun updateBusRoute(busRouteInfo: BusRouteInfo) {
        throw UnsupportedOperationException("Read only test repository")
    }

    override suspend fun deleteAllBusRoutes() {
        throw UnsupportedOperationException("Read only test repository")
    }

    override suspend fun getBusRoutesCount(): Int {
        return busRouteList.size
    }

    override suspend fun getBusServiceRoute(
        serviceNo: String,
        direction: Int
    ): List<BusRouteInfoWithBusStopInfo> {
        return busRouteList.filter {
            it.serviceNo == serviceNo && it.direction == direction
        }
            .map {
                BusRouteInfoWithBusStopInfo(
                    busRouteInfo = it,
                    busStopInfo = busStopList.find { busStopInfo ->
                        busStopInfo.busStopCode == it.busStopCode
                    }!!
                )
            }
    }

    override suspend fun getBusServiceRouteAfterSpecifiedStop(
        serviceNo: String,
        direction: Int,
        stopSequence: Int
    ): List<BusRouteInfoWithBusStopInfo> {
        return getBusServiceRoute(serviceNo, direction)
            .filter { it.busRouteInfo.stopSequence >= stopSequence }
    }

    override suspend fun getBusRouteInfoWithBusStopInfo(
        serviceNo: String,
        direction: Int,
        stopSequence: Int
    ): BusRouteInfoWithBusStopInfo {
        val found = busRouteList.find {
            it.serviceNo == serviceNo && it.direction == direction && it.stopSequence == stopSequence
        }!!
        return BusRouteInfoWithBusStopInfo(
            busRouteInfo = found,
            busStopInfo = busStopList.find { busStopInfo ->
                busStopInfo.busStopCode == found.busStopCode
            }!!
        )
    }

    override suspend fun getBusRoutesAtBusStop(busStopCode: String): List<BusRouteInfo> {
        return busRouteList.filter { it.busStopCode == busStopCode }
    }

    override suspend fun insertBusStop(busStopInfo: BusStopInfo) {
        throw UnsupportedOperationException("Read only test repository")
    }

    override suspend fun updateBusStop(busStopInfo: BusStopInfo) {
        throw UnsupportedOperationException("Read only test repository")
    }

    override suspend fun deleteBusStop(busStopInfo: BusStopInfo) {
        throw UnsupportedOperationException("Read only test repository")
    }

    override suspend fun deleteAllBusStops() {
        throw UnsupportedOperationException("Read only test repository")
    }

    override suspend fun getBusStopsCount(): Int {
        return busStopList.size
    }

    override suspend fun getBusStop(busStopCode: String): BusStopInfo? {
        return busStopList.find { it.busStopCode == busStopCode }
    }

    override suspend fun getAllBusStops(): List<BusStopInfo> {
        return busStopList
    }

    override suspend fun getBusStopsContaining(partialDescription: String): List<BusStopInfo> {
        return busStopList.filter { it.description.contains(
            partialDescription,
            ignoreCase = true
        ) }
    }

    override suspend fun getBusStopsWithPrefixCode(busStopCode: String): List<BusStopInfo> {
        return busStopList.filter { it.busStopCode.startsWith(busStopCode) }
    }

    override suspend fun getBusStopsWithPartialRoadName(roadName: String): List<BusStopInfo> {
        return busStopList.filter { it.roadName.contains(
            roadName,
            ignoreCase = true
        ) }
    }

    override suspend fun getBusStopWithBusRoutes(busStopCode: String): BusStopInfoWithBusRoutesInfo {
        TODO("Not yet implemented")
    }

    override suspend fun insertMRTStation(mrtStation: MRTStation) {
        throw UnsupportedOperationException("Read only test repository")
    }

    override suspend fun updateMRTStation(mrtStation: MRTStation) {
        throw UnsupportedOperationException("Read only test repository")
    }

    override suspend fun deleteMRTStation(mrtStation: MRTStation) {
        throw UnsupportedOperationException("Read only test repository")
    }

    override suspend fun deleteAllMRTStations() {
        throw UnsupportedOperationException("Read only test repository")
    }

    override suspend fun getMRTStationCount(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getMRTStation(stationCode: String): MRTStation {
        TODO("Not yet implemented")
    }

    override suspend fun getMRTStationsContaining(partialName: String): List<MRTStation> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllMRTStations(): List<MRTStation> {
        TODO("Not yet implemented")
    }

    override suspend fun insertPlannedBusRoute(plannedBusRouteInfo: PlannedBusRouteInfo) {
        TODO("Not yet implemented")
    }

    override suspend fun updatePlannedBusRoute(plannedBusRouteInfo: PlannedBusRouteInfo) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlannedBusRoute(plannedBusRouteInfo: PlannedBusRouteInfo) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllPlannedBusRoutes() {
        TODO("Not yet implemented")
    }

    override suspend fun getAllPlannedBusRoutes(): List<PlannedBusRouteInfoWithBusStopInfo> {
        TODO("Not yet implemented")
    }
}
