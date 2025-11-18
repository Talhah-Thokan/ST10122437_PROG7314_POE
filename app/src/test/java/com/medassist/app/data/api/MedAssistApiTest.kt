package com.medassist.app.data.api

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response
import org.mockito.Mockito.*

/**
 * Unit tests for MedAssistApi service
 */
@RunWith(MockitoJUnitRunner::class)
class MedAssistApiTest {

    @Mock
    private lateinit var mockApiService: MedAssistApi

    @Test
    fun `getArticles should return list of articles`() = runBlocking {
        // Given
        val mockArticles = listOf(
            Article(
                id = "1",
                title = "Test Article",
                author = "Test Author",
                summary = "Test summary",
                content = "Test content",
                imageUrl = "test.jpg",
                date = "2025-01-15"
            )
        )
        val mockResponse = Response.success(mockArticles)
        `when`(mockApiService.getArticles()).thenReturn(mockResponse)

        // When
        val response = mockApiService.getArticles()

        // Then
        assertTrue(response.isSuccessful)
        assertEquals(mockArticles, response.body())
        verify(mockApiService).getArticles()
    }

    @Test
    fun `getProviders should return list of doctors`() = runBlocking {
        // Given
        val mockDoctors = listOf(
            Doctor(
                id = "1",
                name = "Dr. Test",
                specialty = "Cardiologist",
                rating = "4.8",
                distance = "2.1 km",
                experience = "15+ years",
                price = "$150",
                availability = "Available today"
            )
        )
        val mockResponse = Response.success(mockDoctors)
        `when`(mockApiService.getProviders()).thenReturn(mockResponse)

        // When
        val response = mockApiService.getProviders()

        // Then
        assertTrue(response.isSuccessful)
        assertEquals(mockDoctors, response.body())
        verify(mockApiService).getProviders()
    }

    @Test
    fun `createBooking should return booking confirmation`() = runBlocking {
        // Given
        val bookingRequest = BookingRequest(
            doctorId = "1",
            patientName = "Test Patient",
            patientEmail = "test@example.com",
            appointmentDate = "2025-01-20",
            appointmentTime = "10:00",
            reason = "Test reason"
        )
        val mockBooking = BookingResponse(
            id = "BK123",
            status = "confirmed",
            message = "Appointment booked successfully"
        )
        val mockResponse = Response.success(mockBooking)
        `when`(mockApiService.createBooking(bookingRequest)).thenReturn(mockResponse)

        // When
        val response = mockApiService.createBooking(bookingRequest)

        // Then
        assertTrue(response.isSuccessful)
        assertEquals(mockBooking, response.body())
        verify(mockApiService).createBooking(bookingRequest)
    }
}

