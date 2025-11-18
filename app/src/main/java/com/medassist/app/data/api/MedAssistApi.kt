package com.medassist.app.data.api

import retrofit2.Response
import retrofit2.http.*

/**
 * REST API interface for MedAssist backend services
 * 
 * This interface defines the REST API endpoints that connect to the Ktor server.
 * The app will attempt to use these endpoints first, then fall back to Firebase
 * if the REST API is unavailable.
 */
interface MedAssistApi {

    /**
     * GET /articles - Fetches list of health articles
     * @return Response containing list of Article objects
     */
    @GET("articles")
    suspend fun getArticles(): Response<List<Article>>

    /**
     * GET /providers - Fetches list of doctors/providers
     * @return Response containing list of Doctor objects
     */
    @GET("providers")
    suspend fun getProviders(): Response<List<Doctor>>

    /**
     * POST /bookings - Creates a new appointment booking
     * @param booking The booking request data
     * @return Response containing booking confirmation
     */
    @POST("bookings")
    suspend fun createBooking(@Body booking: BookingRequest): Response<BookingResponse>
}

/**
 * Data classes for API requests and responses
 */
data class Article(
    val id: String,
    val title: String,
    val author: String,
    val summary: String,
    val content: String,
    val imageUrl: String,
    val date: String
)

data class Doctor(
    val id: String,
    val name: String,
    val specialty: String,
    val rating: String,
    val distance: String,
    val experience: String,
    val price: String,
    val availability: String
)

data class BookingRequest(
    val doctorId: String? = null,
    val patientName: String,
    val patientEmail: String,
    val appointmentDate: String,
    val appointmentTime: String,
    val reason: String
)

data class BookingResponse(
    val id: String,
    val status: String,
    val message: String
)