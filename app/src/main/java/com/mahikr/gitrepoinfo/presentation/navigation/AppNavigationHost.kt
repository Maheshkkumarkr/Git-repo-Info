package com.mahikr.gitrepoinfo.presentation.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mahikr.gitrepoinfo.presentation.screen.DetailScreen
import com.mahikr.GitRepoInfo.presentation.screen.HomeScreen



@Composable
fun AppNavigationHost() {

    // Create a NavController instance to manage navigation
    val navHostController = rememberNavController()

    // Define the NavHost, which is the root of the navigation graph
    NavHost(
        navController = navHostController, // Pass the NavController instance
        startDestination = ScreenRoute.Main.route, // Set the initial screen
        route = Route.ROOT_NAV_ROUTE.name // Set the route for the NavHost
    ) {
        // Define the Main screen route
        composable(route = ScreenRoute.Main.route) {
            // Display the HomeScreen and pass a lambda to navigate to the Detail screen
            HomeScreen { id ->
                // Navigate to the Detail screen with the provided ID
                navHostController.navigate(/*ScreenRoute.Detail.route + "/${id}"*/ScreenRoute.Detail.navigate(
                    id
                )
                )
            }
        }

        // Define the Detail screen route with an argument for the ID
        composable(
            route = /*ScreenRoute.Detail.route + "/{id}"*/ScreenRoute.Detail.getPath(),
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            // Extract the ID from the navigation arguments
            val id = backStackEntry.arguments?.getInt("id")

            // Log the ID for debugging purposes
            Log.d("TAG", "AppNavHost: => GitRepoFullScreen $id")

            // Display the DetailScreen
            DetailScreen()
        }
    }
}