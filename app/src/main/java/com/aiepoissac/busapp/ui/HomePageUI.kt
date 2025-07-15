package com.aiepoissac.busapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DepartureBoard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Subway
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


enum class Pages(val route: String, val title: String) {
    HomePage(route = "HomePage", title = "Home"),
    BusArrival(route = "BusArrival/{text}", title = "Bus Stop Information"),
    BusRouteInformation(route = "BusRouteInfo/{text1}/{text2}/{text3}/{text4}/{text5}", title = "Bus Route Information"),
    NearbyInformation(route = "NearbyInfo/{text1}/{text2}/{text3}", title = "Nearby Bus Stops"),
    BusesToMRTStation(route = "BusesToMRTStation/{text1}/{text2}/{text3}", title = "Bus Service to MRT station"),
    SavedJourneys(route = "SavedJourneys", title = "Saved Journeys"),
    SavedJourney(route = "SavedJourney/{text}", title = "Saved Journey");

    fun withText(text: String): String {
        return route.replace("{text}", text)
    }

    fun with2Text(text1: String, text2: String): String {
        return route.replace("{text1}", text1)
            .replace("{text2}", text2)
    }

    fun with3Text(text1: String, text2: String, text3: String): String {
        return route.replace("{text1}", text1)
            .replace("{text2}", text2)
            .replace("{text3}", text3)
    }

    fun with5Text(text1: String, text2: String, text3: String, text4: String, text5: String): String {
        return route.replace("{text1}", text1)
            .replace("{text2}", text2)
            .replace("{text3}", text3)
            .replace("{text4}", text4)
            .replace("{text5}", text5)
    }

    companion object {
        fun getRoute(route: String): Pages {
            return when {
                route.startsWith(BusArrival.route) -> BusArrival
                route.startsWith(BusRouteInformation.route) -> BusRouteInformation
                route.startsWith(NearbyInformation.route) -> NearbyInformation
                route.startsWith(BusesToMRTStation.route) -> BusesToMRTStation
                route.startsWith(SavedJourneys.route) -> SavedJourneys
                route.startsWith(SavedJourney.route) -> SavedJourney
                else -> HomePage
            }
        }
    }

}

@Composable
fun BusApp(
    navController: NavHostController = rememberNavController()
) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentPage = Pages.getRoute(
        backStackEntry?.destination?.route ?: Pages.HomePage.route
    )

    val configuration = LocalConfiguration.current

    Scaffold(
        topBar = {
            if (configuration.orientation == 1) {
                BusAppBar(
                    currentPage = currentPage,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    navigateHome = { navigateToHomePage(navController) }
                )
            }
        }
    ) {
        innerPadding ->

            NavHost(
                navController = navController,
                startDestination = Pages.HomePage.name,
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                composable(route = Pages.HomePage.route) {
                    HomePageUI(
                        navController = navController,
                        innerPadding = innerPadding
                    )
                }

                composable(route = Pages.BusArrival.route) {
                    backStackEntry ->
                        val text = backStackEntry.arguments?.getString("text") ?: ""
                        BusArrivalUI(
                            navController = navController,
                            busStopCodeInput = text,
                        )
                }

                composable(route = Pages.BusRouteInformation.route) { backStackEntry ->
                    val text1 = backStackEntry.arguments?.getString("text1") ?: ""
                    val text2 = backStackEntry.arguments?.getString("text2") ?: ""
                    val text3 = backStackEntry.arguments?.getString("text3") ?: ""
                    val text4 = backStackEntry.arguments?.getString("text4") ?: ""
                    val text5 = backStackEntry.arguments?.getString("text5") ?: ""
                    BusRouteUI(
                        navController = navController,
                        serviceNo = text1,
                        direction = text2.toInt(),
                        stopSequence = text3.toInt(),
                        showMap = text4.toBoolean(),
                        showLiveBuses = text5.toBoolean()
                    )
                }

                composable(route = Pages.NearbyInformation.route) { backStackEntry ->
                    val text1 = backStackEntry.arguments?.getString("text1") ?: ""
                    val text2 = backStackEntry.arguments?.getString("text2") ?: ""
                    val text3 = backStackEntry.arguments?.getString("text3") ?: ""
                    NearbyUI(
                        navController = navController,
                        latitude = text1.toDouble(),
                        longitude = text2.toDouble(),
                        isLiveLocation = text3.toBoolean()
                    )
                }

                composable(route = Pages.BusesToMRTStation.route) { backStackEntry ->
                    val text1 = backStackEntry.arguments?.getString("text1") ?: ""
                    val text2 = backStackEntry.arguments?.getString("text2") ?: ""
                    val text3 = backStackEntry.arguments?.getString("text3") ?: ""
                    BusToMRTStationsUI(
                        navController = navController,
                        latitude = text1.toDouble(),
                        longitude = text2.toDouble(),
                        stationCode = text3
                    )
                }

                composable(route = Pages.SavedJourneys.route) {
                    SavedJourneysUI(
                        navController = navController
                    )
                }

                composable(route = Pages.SavedJourney.route) { backStackEntry ->
                    val text = backStackEntry.arguments?.getString("text") ?: ""
                    SavedJourneyUI(
                        navController = navController,
                        journeyID = text
                    )
                }
            }
    }

}

@Composable
private fun HomePageUI(
    navController: NavHostController = rememberNavController(),
    innerPadding: PaddingValues
) {

    val homePageViewModel: HomePageViewModel =
        viewModel(
            factory = HomePageViewModelFactory()
        )
    val homePageUIState by homePageViewModel.uiState.collectAsState()

    if (homePageViewModel.downloaded) {

        Column(
            modifier = Modifier.fillMaxSize()
                .consumeWindowInsets(innerPadding)
        ) {
            if (!homePageUIState.busStopSearchBarExpanded &&
                    !homePageUIState.busServiceSearchBarExpanded &&
                    !homePageUIState.busStopRoadSearchBarExpanded &&
                    !homePageUIState.mrtStationSearchBarExpanded
                ) {
                Button(
                        onClick = {
                            navigateToNearby(
                                navController = navController,
                                isLiveLocation = true
                            )
                        },
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                ) {
                    Row {
                        Icon(
                            Icons.Filled.NearMe,
                            contentDescription = "Nearby bus/MRT"
                        )

                        Text(
                            text = "Nearby bus/MRT",
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        navigateToSavedJourneys(navController = navController)
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                ) {
                    Row {
                        Icon(
                            Icons.Filled.Save,
                            contentDescription = "Saved journeys"
                        )

                        Text(
                            text = "Saved journeys",
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            if (!homePageUIState.busServiceSearchBarExpanded &&
                !homePageUIState.mrtStationSearchBarExpanded) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Filled.DepartureBoard,
                            contentDescription = Pages.BusRouteInformation.title,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "Bus Arrivals and Bus Stops",
                            modifier = Modifier.weight(4f)
                        )
                    }

                    SearchBarWithSuggestions(
                        onQueryChange = homePageViewModel::updateBusStopCodeInput,
                        onSearch = {
                            if (homePageUIState.busStopSearchResult.isNotEmpty()) {
                                navigateToBusArrival(
                                    navController = navController,
                                    busStopInput = homePageUIState.busStopSearchResult.first().busStopCode
                                )
                            }
                        },
                        query = homePageUIState.busStopCodeInput,
                        placeholder = "Bus stop code or name",
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = "Clear input",
                                modifier = Modifier.clickable {
                                    homePageViewModel.updateBusStopCodeInput(busStopCodeInput = "")
                                }
                            )
                                       },
                        expanded = homePageUIState.busStopSearchBarExpanded,
                        onExpandedChange = homePageViewModel::setBusStopSearchBarExpanded,
                        searchResults = homePageUIState.busStopSearchResult,
                        onItemClick = { busStopInfo ->
                            navigateToBusArrival(
                                navController = navController,
                                busStopInput = busStopInfo.busStopCode
                            )
                        },
                        itemContent = { busStopInfo ->
                            Text(
                                text = busStopInfo.toString(),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    )

                    SearchBarWithSuggestions(
                        onQueryChange = homePageViewModel::updateBusStopRoadInput,
                        onSearch = {
                            if (homePageUIState.busStopRoadSearchResult.isNotEmpty()) {
                                navigateToBusArrival(
                                    navController = navController,
                                    busStopInput = homePageUIState.busStopRoadSearchResult.first().busStopCode
                                )
                            }
                        },
                        query = homePageUIState.busStopRoadInput,
                        placeholder = "Bus road name",
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = "Clear input",
                                modifier = Modifier.clickable {
                                    homePageViewModel.updateBusStopRoadInput(busStopRoadInput = "")
                                }
                            )
                        },
                        expanded = homePageUIState.busStopRoadSearchBarExpanded,
                        onExpandedChange = homePageViewModel::setBusStopRoadSearchBarExpanded,
                        searchResults = homePageUIState.busStopRoadSearchResult,
                        onItemClick = { busStopInfo ->
                            navigateToBusArrival(
                                navController = navController,
                                busStopInput = busStopInfo.busStopCode
                            )
                        },
                        itemContent = { busStopInfo ->
                            Text(
                                text = "${busStopInfo.description} (${busStopInfo.roadName})",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    )
                }
            }

            if (!homePageUIState.busStopSearchBarExpanded &&
                !homePageUIState.busStopRoadSearchBarExpanded &&
                !homePageUIState.mrtStationSearchBarExpanded) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Filled.DirectionsBus,
                        contentDescription = Pages.BusRouteInformation.title,
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    SearchBarWithSuggestions(
                        onQueryChange = homePageViewModel::updateBusServiceInput,
                        onSearch = {
                            if (homePageUIState.busServiceSearchResult.isNotEmpty()) {
                                val busServiceInfo = homePageUIState.busServiceSearchResult.first().busServiceInfo
                                navigateToBusRouteInformation(
                                    navController = navController,
                                    serviceNo = busServiceInfo.serviceNo,
                                    direction = 1
                                )
                            }
                        },
                        query = homePageUIState.busServiceInput,
                        placeholder = "Bus service number",
                        expanded = homePageUIState.busServiceSearchBarExpanded,
                        onExpandedChange = homePageViewModel::setBusServiceSearchBarExpanded,
                        searchResults = homePageUIState.busServiceSearchResult,
                        onItemClick = { busService ->
                            navigateToBusRouteInformation(
                                navController = navController,
                                serviceNo = busService.busServiceInfo.serviceNo,
                                direction = busService.busServiceInfo.direction
                            )
                        },
                        itemContent = { busService ->
                            val busServiceInfo = busService.busServiceInfo
                            Card(
                                modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                onClick = {
                                    navigateToBusRouteInformation(
                                        navController = navController,
                                        serviceNo = busServiceInfo.serviceNo,
                                        direction = busServiceInfo.direction
                                    )
                                }
                            ) {
                                Text(
                                    text = "${busServiceInfo.operator} ${busServiceInfo.category} ${busServiceInfo.serviceNo}"
                                )

                                if (!busServiceInfo.isLoop()) {
                                    Text(
                                        text = "${busService.originBusStopInfo.description} to ${busService.destinationBusStopInfo.description}"
                                    )
                                } else {
                                    Text(
                                        text = "Loop at: ${busServiceInfo.loopDesc}"
                                    )
                                }
                            }
                        }
                    )
                }
            }

            if (!homePageUIState.busStopSearchBarExpanded &&
                !homePageUIState.busStopRoadSearchBarExpanded &&
                !homePageUIState.busServiceSearchBarExpanded) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Subway,
                        contentDescription = Pages.NearbyInformation.title,
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    SearchBarWithSuggestions(
                        onQueryChange = homePageViewModel::updateMRTStationInput,
                        onSearch = {

                        },
                        query = homePageUIState.mrtStationInput,
                        placeholder = "MRT Station name",
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = "Clear input",
                                modifier = Modifier.clickable {
                                    homePageViewModel.updateMRTStationInput(mrtStationInput = "")
                                }
                            )
                        },
                        expanded = homePageUIState.mrtStationSearchBarExpanded,
                        onExpandedChange = homePageViewModel::setMRTStationSearchBarExpanded,
                        searchResults = homePageUIState.mrtStationSearchResult,
                        onItemClick = { mrtStation ->
                            navigateToNearby(
                                navController = navController,
                                latitude = mrtStation.latitude,
                                longitude = mrtStation.longitude
                            )
                        },
                        itemContent = { mrtStation ->
                            Text(
                                text = "${mrtStation.stationCode} ${mrtStation.stationName}",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    )
                }
            }

        }
    } else if (!homePageViewModel.failedDownload) {
        Column {
            Text(
                text = "Downloading data",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    } else {
        Column {
            Text(
                text = "Download Failed",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
            Button(
                onClick = {
                    homePageViewModel.reInitialiseData()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Retry",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchBarWithSuggestions(
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    query: String,
    placeholder: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    searchResults: List<T>,
    leadingIcon: @Composable (() -> Unit)? = { Icon(Icons.Filled.Search, contentDescription = "Search") },
    trailingIcon: @Composable (() -> Unit)? = null,
    itemContent: @Composable (T) -> Unit,
    onItemClick: (T) -> Unit
) {
    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                expanded = false,
                onExpandedChange = onExpandedChange,
                placeholder = { Text(placeholder) },
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon
            )
        },
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        LazyColumn {
            items(searchResults) { item ->
                Box(
                    modifier = Modifier
                        .clickable { onItemClick(item) }
                        .fillMaxWidth()
                ) {
                    itemContent(item)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BusAppBar(
    currentPage: Pages,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    navigateHome: () -> Unit
) {

    TopAppBar(
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                text = currentPage.title
            )
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back Button"
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = navigateHome) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Back Button"
                )
            }
        }
    )
}

fun navigateToBusArrival(
    navController: NavHostController,
    busStopInput: String = ""
) {
    navController.navigate(Pages.BusArrival.withText(busStopInput))
}

fun navigateToNearby(
    navController: NavHostController,
    latitude: Double = 1.290270,
    longitude: Double = 103.851959,
    isLiveLocation: Boolean = false
) {
    navController.navigate(Pages.NearbyInformation.with3Text(
        text1 = latitude.toString(),
        text2 = longitude.toString(),
        text3 = isLiveLocation.toString())
    )
}

fun navigateToSavedJourneys(
    navController: NavHostController
) {
    navController.navigate(Pages.SavedJourneys.route)
}

fun navigateToHomePage(navController: NavHostController) {
    navController.popBackStack(
        route = Pages.HomePage.name,
        inclusive = false
    )
}

fun navigateToBusRouteInformation(
    navController: NavHostController,
    serviceNo: String = "",
    direction: Int,
    stopSequence: Int = -1,
    showMap: Boolean = false,
    showLiveBuses: Boolean = false
) {
    navController.navigate(
        Pages.BusRouteInformation.with5Text(
            text1 = serviceNo,
            text2 = direction.toString(),
            text3 = stopSequence.toString(),
            text4 = showMap.toString(),
            text5 = showLiveBuses.toString()
        )
    )
}