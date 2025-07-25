package com.aiepoissac.busapp.data.businfo

import com.aiepoissac.busapp.data.mrtstation.MRTStation
import com.aiepoissac.busapp.data.mrtstation.MRTStationDAO

class OfflineBusRepository(
    private val busServiceInfoDAO: BusServiceInfoDAO,
    private val busRouteInfoDAO: BusRouteInfoDAO,
    private val busStopInfoDAO: BusStopInfoDAO,
    private val mrtStationDAO: MRTStationDAO,
    private val plannedBusRouteInfoDAO: PlannedBusRouteInfoDAO
): BusRepository {

    override suspend fun insertBusService(busServiceInfo: BusServiceInfo) {
        busServiceInfoDAO.insert(busServiceInfo = busServiceInfo)
    }

    override suspend fun deleteBusService(busServiceInfo: BusServiceInfo) {
        busServiceInfoDAO.delete(busServiceInfo = busServiceInfo)
    }

    override suspend fun updateBusService(busServiceInfo: BusServiceInfo) {
        busServiceInfoDAO.update(busServiceInfo = busServiceInfo)
    }

    override suspend fun deleteAllBusServices() {
        busServiceInfoDAO.deleteAll()
    }

    override suspend fun getBusServicesCount(): Int {
        return busServiceInfoDAO.getBusServicesCount()
    }

    override suspend fun getBusService(serviceNo: String): List<BusServiceInfoWithBusStopInfo> {
        return busServiceInfoDAO.getBusService(serviceNo)
    }

    override suspend fun getBusService(serviceNo: String, direction: Int): BusServiceInfo? {
        return busServiceInfoDAO.getBusService(serviceNo, direction)
    }

    override suspend fun insertBusRoute(busRouteInfo: BusRouteInfo) {
        busRouteInfoDAO.insert(busRouteInfo)
    }

    override suspend fun deleteBusRoute(busRouteInfo: BusRouteInfo) {
        busRouteInfoDAO.delete(busRouteInfo)
    }

    override suspend fun updateBusRoute(busRouteInfo: BusRouteInfo) {
        busRouteInfoDAO.update(busRouteInfo)
    }

    override suspend fun deleteAllBusRoutes() {
        busRouteInfoDAO.deleteAll()
    }

    override suspend fun getBusRoutesCount(): Int {
        return busRouteInfoDAO.getBusRoutesCount()
    }

    override suspend fun getBusServiceRouteLength(serviceNo: String, direction: Int): Int {
        return busRouteInfoDAO.getBusServiceRouteLength(serviceNo, direction)
    }

    override suspend fun getBusServiceRoute(
        serviceNo: String, direction: Int
    ): List<BusRouteInfoWithBusStopInfo> {
        return busRouteInfoDAO.getBusServiceRoute(serviceNo, direction)
    }

    override suspend fun getBusServiceRouteAfterSpecifiedStop(
        serviceNo: String, direction: Int, stopSequence: Int
    ): List<BusRouteInfoWithBusStopInfo> {
        return busRouteInfoDAO
            .getBusServiceRouteAfterSpecifiedStop(serviceNo, direction, stopSequence)
    }

    override suspend fun getBusRouteInfoWithBusStopInfo(
        serviceNo: String, direction: Int, stopSequence: Int
    ): BusRouteInfoWithBusStopInfo {
        return busRouteInfoDAO.getBusRouteInfoWithBusStopInfo(serviceNo, direction, stopSequence)
    }

    override suspend fun getBusRoutesAtBusStop(busStopCode: String): List<BusRouteInfo> {
        return busRouteInfoDAO.getBusRoutesAtBusStop(busStopCode)
    }

    override suspend fun insertBusStop(busStopInfo: BusStopInfo) {
        busStopInfoDAO.insert(busStopInfo)
    }

    override suspend fun updateBusStop(busStopInfo: BusStopInfo) {
        busStopInfoDAO.update(busStopInfo)
    }

    override suspend fun deleteBusStop(busStopInfo: BusStopInfo) {
        busStopInfoDAO.delete(busStopInfo)
    }

    override suspend fun deleteAllBusStops() {
        busStopInfoDAO.deleteAll()
    }

    override suspend fun getBusStopsCount(): Int {
        return busStopInfoDAO.getBusStopsCount()
    }

    override suspend fun getBusStop(busStopCode: String): BusStopInfo? {
        return busStopInfoDAO.getBusStop(busStopCode)
    }

    override suspend fun getAllBusStops(): List<BusStopInfo> {
        return busStopInfoDAO.getAllBusStops()
    }

    override suspend fun getBusStopsContaining(partialDescription: String): List<BusStopInfo> {
        return busStopInfoDAO.getBusStopsContaining(partialDescription)
    }

    override suspend fun getBusStopsWithPrefixCode(busStopCode: String): List<BusStopInfo> {
        return busStopInfoDAO.getBusStopsWithPrefixCode(busStopCode)
    }

    override suspend fun getBusStopsWithPartialRoadName(roadName:String): List<BusStopInfo> {
        return busStopInfoDAO.getBusStopsWithPartialRoadName(roadName)
    }

    override suspend fun getBusStopWithBusRoutes(busStopCode: String):
            BusStopInfoWithBusRoutesInfo {
        return busStopInfoDAO.getBusStopWithBusRoutes(busStopCode)
    }

    override suspend fun insertMRTStation(mrtStation: MRTStation) {
        mrtStationDAO.insert(mrtStation)
    }

    override suspend fun updateMRTStation(mrtStation: MRTStation) {
        mrtStationDAO.update(mrtStation)
    }

    override suspend fun deleteMRTStation(mrtStation: MRTStation) {
        mrtStationDAO.delete(mrtStation)
    }

    override suspend fun deleteAllMRTStations() {
        mrtStationDAO.deleteAll()
    }

    override suspend fun getMRTStationCount(): Int {
        return mrtStationDAO.getMRTStationCount()
    }

    override suspend fun getMRTStation(stationCode: String): MRTStation {
        return mrtStationDAO.getMRTStation(stationCode)
    }

    override suspend fun getAllMRTStations(): List<MRTStation> {
        return mrtStationDAO.getAllMRTStations()
    }

    override suspend fun getMRTStationsContaining(partialName: String): List<MRTStation> {
        return mrtStationDAO.getMRTStationsContaining(partialName)
    }

    override suspend fun insertPlannedBusRoute(plannedBusRouteInfo: PlannedBusRouteInfo) {
        plannedBusRouteInfoDAO.insert(plannedBusRouteInfo)
    }

    override suspend fun updatePlannedBusRoute(plannedBusRouteInfo: PlannedBusRouteInfo) {
        plannedBusRouteInfoDAO.update(plannedBusRouteInfo)
    }

    override suspend fun deletePlannedBusRoute(plannedBusRouteInfo: PlannedBusRouteInfo) {
        plannedBusRouteInfoDAO.delete(plannedBusRouteInfo)
    }

    override suspend fun deleteAllPlannedBusRoutes() {
        plannedBusRouteInfoDAO.deleteAll()
    }

    override suspend fun getPlannedBusRoutesCount(): Int {
        return plannedBusRouteInfoDAO.getPlannedBusRoutesCount()
    }

    override suspend fun getAllPlannedBusRoutes(): List<PlannedBusRouteInfoWithBusStopInfo> {
        return plannedBusRouteInfoDAO.getAll()
    }

}