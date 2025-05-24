package com.aiepoissac.busapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aiepoissac.busapp.BusApplication

enum class Pages(val route: String, val title: String) {
    HomePage(route = "HomePage", title = "Home"),
    BusArrival(route = "BusArrival/{text}", title = "Bus Arrival Timings"),
    BusServiceInformation(route = "BusServiceInfo/{text}", title = "Bus Service Information"),
    BusRouteInformation(route = "BusRouteInfo/{text1}/{text2}", title = "Bus Route Information"),
    BusStopInformation(route = "BusStopInfo/{text}", title = "Bus Stop Information"),
    NearbyInformation(route = "NearbyInfo/{text1}/{text2}", title = "Nearby Bus Stops");

    fun withText(text: String): String {
        return route.replace("{text}", text)
    }

    fun with2Text(text1: String, text2: String): String {
        return route.replace("{text1}", text1)
            .replace("{text2}", text2)
    }

    companion object {
        fun getRoute(route: String): Pages {
            return when {
                route.startsWith(BusArrival.route) -> BusArrival
                route.startsWith(BusServiceInformation.route) -> BusServiceInformation
                route.startsWith(BusRouteInformation.route) -> BusRouteInformation
                route.startsWith(NearbyInformation.route) -> NearbyInformation
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
                    navigateUp = { navController.navigateUp() }
                )
            }
        },
        bottomBar = {

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
                    HomePageUI(navController = navController)
                }
                composable(route = Pages.BusArrival.route) {
                    backStackEntry ->
                        val text = backStackEntry.arguments?.getString("text") ?: ""
                        BusArrivalUI(
                            navController = navController,
                            busStopCodeInput = text
                        )
                }
                composable(route = Pages.BusServiceInformation.route) {
                    backStackEntry ->
                        val text = backStackEntry.arguments?.getString("text") ?: ""
                        BusServiceUI(
                            navController = navController,
                            busServiceInput = text
                        )
                }
                composable(route = Pages.BusRouteInformation.route) { backStackEntry ->
                    val text1 = backStackEntry.arguments?.getString("text1") ?: ""
                    val text2 = backStackEntry.arguments?.getString("text2") ?: ""
                    BusRouteUI(
                        navController = navController,
                        serviceNo = text1,
                        direction = text2.toInt()
                    )
                }
                composable(route = Pages.NearbyInformation.route) { backStackEntry ->
                    val text1 = backStackEntry.arguments?.getString("text1") ?: ""
                    val text2 = backStackEntry.arguments?.getString("text2") ?: ""
                    NearbyUI(
                        navController = navController,
                        latitude = text1.toDouble(),
                        longitude = text2.toDouble()
                    )
                }
            }
    }

}

@SuppressLint("MissingPermission")
@Preview
@Composable
private fun HomePageUI(
    homePageViewModel: HomePageViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {

    RequestLocationPermission {
        homePageViewModel.fetchLocation()
    }

    Column {

        Button(
            onClick = {
                val location = homePageViewModel.location.value
                if (location != null) {
                    navigateToNearby(
                        navController = navController,
                        latitude = location.latitude.toString(),
                        longitude = location.longitude.toString()
                    )
                } else {
                    Toast.makeText(
                        BusApplication.instance,
                        "No location permission",
                        Toast.LENGTH_LONG
                    ).show()
                    navigateToNearby(
                        navController = navController
                    )
                }
                      },
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Text(
                text = "Nearby bus stops",
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        TextBox(
            title = Pages.BusArrival.title,
            label = "Bus Stop Code",
            text = homePageViewModel.busArrivalBusStopCodeInput,
            onValueChange = { homePageViewModel.updateBusArrivalBusStopCodeInput(it) },
            keyboardType = KeyboardType.Number,
            onDone = {
                if (homePageViewModel.downloaded) {
                    navigateToBusArrival(
                        navController = navController,
                        busStopInput = homePageViewModel.busArrivalBusStopCodeInput
                    )
                    homePageViewModel.updateBusArrivalBusStopCodeInput(input = "")
                } else {
                    displayNotDownloadToast(context = BusApplication.instance)
                }
            }

        )

        TextBox(
            title = Pages.BusServiceInformation.title,
            label = "Service No.",
            text = homePageViewModel.busServiceNoInput,
            onValueChange = { homePageViewModel.updateBusServiceNoInput(it) },
            onDone = {
                if (homePageViewModel.downloaded) {
                    navigateToBusServiceInformation(
                        navController = navController,
                        busServiceInput = homePageViewModel.busServiceNoInput
                    )
                    homePageViewModel.updateBusServiceNoInput(input = "")
                } else {
                    displayNotDownloadToast(context = BusApplication.instance)
                }
            }
        )

    }
}

private fun displayNotDownloadToast(context: Context) {
    Toast.makeText(context, "Data still downloading", Toast.LENGTH_LONG).show()
}

@Composable
private fun TextBox(
    title: String,
    label: String,
    text: String = "",
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Text(
            text = title,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
            value = text,
            singleLine = true,
            onValueChange = onValueChange,
            label = { Text(text = label) },
            isError = false,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = keyboardType),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BusAppBar(
    currentPage: Pages,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
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
    latitude: String = "1.290270",
    longitude: String = "103.851959"
) {
    navController.navigate(Pages.NearbyInformation.with2Text(latitude, longitude))
}

fun navigateToBusServiceInformation(
    navController: NavHostController,
    busServiceInput: String = ""
) {
    navController.navigate(Pages.BusServiceInformation.withText(busServiceInput))
}

@Composable
fun RequestLocationPermission(onGranted: () -> Unit) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onGranted()
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            onGranted()
        }
    }
}


