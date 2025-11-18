package com.medassist.app.ui.navigation

/**
 * Navigation routes for MedAssist app
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Articles : Screen("articles")
    object ArticleDetail : Screen("article_detail/{articleId}") {
        fun createRoute(articleId: String) = "article_detail/$articleId"
    }
    object Doctors : Screen("doctors")
    object DoctorDetail : Screen("doctor_detail/{doctorId}") {
        fun createRoute(doctorId: String) = "doctor_detail/$doctorId"
    }
    object Booking : Screen("booking/{doctorId}") {
        fun createRoute(doctorId: String) = "booking/$doctorId"
    }
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}

