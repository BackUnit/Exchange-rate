package com.example.financecalculators.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.financecalculators.ui.screens.compoundinterest.CompoundInterestInputScreen
import com.example.financecalculators.ui.screens.compoundinterest.CompoundInterestResultScreen

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String) {
    object Input : Screen("input")
    object Result : Screen("result/{finalAmount}/{totalInvested}/{totalInterest}") {
        fun createRoute(finalAmount: Double, totalInvested: Double, totalInterest: Double): String {
            return "result/$finalAmount/$totalInvested/$totalInterest"
        }
    }
}

/**
 * App navigation setup using Navigation Compose
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    onNavigateToResult: (Double, Double, Double) -> Unit = { _, _, _ -> }
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Input.route
    ) {
        composable(Screen.Input.route) {
            CompoundInterestInputScreen(
                onNavigateToResult = onNavigateToResult
            )
        }
        
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("finalAmount") { type = NavType.DoubleType },
                navArgument("totalInvested") { type = NavType.DoubleType },
                navArgument("totalInterest") { type = NavType.DoubleType }
            )
        ) { backStackEntry ->
            val finalAmount = backStackEntry.arguments?.getDouble("finalAmount") ?: 0.0
            val totalInvested = backStackEntry.arguments?.getDouble("totalInvested") ?: 0.0
            val totalInterest = backStackEntry.arguments?.getDouble("totalInterest") ?: 0.0
            
            CompoundInterestResultScreen(
                finalAmount = finalAmount,
                totalInvested = totalInvested,
                totalInterest = totalInterest,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
