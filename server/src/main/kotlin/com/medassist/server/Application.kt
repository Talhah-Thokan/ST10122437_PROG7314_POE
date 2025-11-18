package com.medassist.server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.gson.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

/**
 * Ktor REST API Server for MedAssist
 * 
 * This server provides REST endpoints for:
 * - GET /articles - Fetch health articles
 * - GET /providers - Fetch doctor/provider information
 * - POST /bookings - Create appointment bookings
 * 
 * Server runs on: http://10.0.2.2:8080 (Android Emulator)
 *                 http://localhost:8080 (Physical device on same network)
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
        
        install(CORS) {
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Options)
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
            anyHost() // For development only
        }
        
        routing {
            get("/") {
                call.respondText("MedAssist REST API Server is running!", ContentType.Text.Plain)
            }
            
            // GET /articles - Returns list of health articles
            get("/articles") {
                println("📚 GET /articles - Request received")
                val articles = getSampleArticles()
                call.respond(articles)
                println("✅ Responded with ${articles.size} articles")
            }
            
            // GET /providers - Returns list of doctors/providers
            get("/providers") {
                println("👨‍⚕️ GET /providers - Request received")
                val providers = getSampleProviders()
                call.respond(providers)
                println("✅ Responded with ${providers.size} providers")
            }
            
            // POST /bookings - Creates a new booking
            post("/bookings") {
                try {
                    println("📅 POST /bookings - Request received")
                    val bookingRequest = call.receive<BookingRequest>()
                    
                    // Validate request
                    if (bookingRequest.patientName.isBlank() || 
                        bookingRequest.patientEmail.isBlank() ||
                        bookingRequest.appointmentDate.isBlank() ||
                        bookingRequest.appointmentTime.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            BookingResponse(
                                id = "",
                                status = "error",
                                message = "Missing required fields"
                            )
                        )
                        return@post
                    }
                    
                    // Create booking response
                    val bookingId = "BK${System.currentTimeMillis()}"
                    val response = BookingResponse(
                        id = bookingId,
                        status = "confirmed",
                        message = "Appointment booked successfully via REST API"
                    )
                    
                    println("✅ Booking created: $bookingId for ${bookingRequest.patientName}")
                    call.respond(response)
                } catch (e: Exception) {
                    println("❌ Error processing booking: ${e.message}")
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        BookingResponse(
                            id = "",
                            status = "error",
                            message = "Server error: ${e.message}"
                        )
                    )
                }
            }
        }
    }.start(wait = true)
}

/**
 * Sample articles data matching Firestore structure
 */
fun getSampleArticles(): List<Article> {
    return listOf(
        Article(
            id = "1",
            title = "Managing Diabetes in South Africa: A Comprehensive Guide",
            author = "Dr. Thabo Ndlovu",
            summary = "With diabetes on the rise in SA, learn how to manage your blood sugar levels and live a healthy life.",
            content = "Diabetes is a growing concern in South Africa, affecting millions of people. This comprehensive guide covers blood sugar management, dietary recommendations tailored to South African cuisine, exercise tips, and how to access affordable medication through public healthcare. Learn about the latest diabetes management techniques available at local clinics and hospitals.",
            imageUrl = "https://via.placeholder.com/400x250/008B8B/FFFFFF?text=MedAssist+Health",
            date = "2025-10-05"
        ),
        Article(
            id = "2",
            title = "Winter Health Tips for South Africans",
            author = "Dr. Zanele Khumalo",
            summary = "Stay healthy this winter with practical tips for preventing flu, colds, and other seasonal illnesses.",
            content = "As winter approaches in South Africa, it's important to protect yourself from seasonal illnesses. This article covers vaccination schedules, immune-boosting foods available in local markets, proper layering for cold mornings, and when to visit your local clinic. Learn about free flu vaccinations available at public health facilities.",
            imageUrl = "https://via.placeholder.com/400x250/20B2AA/FFFFFF?text=MedAssist+Health",
            date = "2025-10-02"
        ),
        Article(
            id = "3",
            title = "Mental Health Resources in South Africa",
            author = "Dr. Sipho Mkhize",
            summary = "Mental health matters. Discover free and affordable mental health resources available across South Africa.",
            content = "Mental health is a priority, and South Africa offers various support systems. This guide covers SADAG (South African Depression and Anxiety Group), community health centers, online counseling services, and how to access mental health care through medical aid schemes. Learn about free helplines and support groups in your area.",
            imageUrl = "https://via.placeholder.com/400x250/006666/FFFFFF?text=MedAssist+Health",
            date = "2025-09-28"
        ),
        Article(
            id = "4",
            title = "Understanding Medical Aid Schemes in South Africa",
            author = "Dr. Lerato Mokoena",
            summary = "Navigate the complex world of medical aids and choose the right plan for you and your family.",
            content = "Choosing the right medical aid in South Africa can be overwhelming. This article breaks down the differences between schemes like Discovery, Momentum, Bonitas, and Medihelp. Learn about PMBs (Prescribed Minimum Benefits), hospital plans vs comprehensive plans, and how to maximize your benefits. Includes tips for those without medical aid on accessing quality healthcare.",
            imageUrl = "https://via.placeholder.com/400x250/008B8B/FFFFFF?text=MedAssist+Health",
            date = "2025-09-25"
        ),
        Article(
            id = "5",
            title = "Healthy Eating on a Budget in South Africa",
            author = "Dr. Nomvula Dlamini",
            summary = "Nutritious meals don't have to be expensive. Learn how to eat healthy with affordable South African ingredients.",
            content = "Eating healthy is possible even on a tight budget in South Africa. This guide covers affordable nutritious foods available at local markets, meal planning with staples like pap, beans, and seasonal vegetables, and budget-friendly protein sources. Learn how to create balanced meals for under R50 per person and discover community feeding programs and food gardens.",
            imageUrl = "https://via.placeholder.com/400x250/20B2AA/FFFFFF?text=MedAssist+Health",
            date = "2025-09-20"
        )
    )
}

/**
 * Sample providers/doctors data matching Firestore structure
 */
fun getSampleProviders(): List<Doctor> {
    return listOf(
        Doctor(
            id = "1",
            name = "Dr. Thabo Mokoena",
            specialty = "General Practitioner",
            rating = "4.9",
            distance = "1.2 km away",
            experience = "12 years experience",
            price = "R650 per consultation",
            availability = "Available today"
        ),
        Doctor(
            id = "2",
            name = "Dr. Zanele Khumalo",
            specialty = "Cardiologist",
            rating = "4.8",
            distance = "3.5 km away",
            experience = "18 years experience",
            price = "R1,250 per consultation",
            availability = "Available tomorrow"
        ),
        Doctor(
            id = "3",
            name = "Dr. Sipho Dlamini",
            specialty = "Pediatrician",
            rating = "4.9",
            distance = "2.1 km away",
            experience = "15 years experience",
            price = "R850 per consultation",
            availability = "Available today"
        ),
        Doctor(
            id = "4",
            name = "Dr. Lerato Ndlovu",
            specialty = "Dermatologist",
            rating = "4.7",
            distance = "4.8 km away",
            experience = "10 years experience",
            price = "R950 per consultation",
            availability = "Available next week"
        ),
        Doctor(
            id = "5",
            name = "Dr. Mandla Mbatha",
            specialty = "Orthopedic Surgeon",
            rating = "4.8",
            distance = "5.2 km away",
            experience = "22 years experience",
            price = "R1,450 per consultation",
            availability = "Available tomorrow"
        ),
        Doctor(
            id = "6",
            name = "Dr. Nomvula Nkosi",
            specialty = "Psychiatrist",
            rating = "4.9",
            distance = "2.7 km away",
            experience = "14 years experience",
            price = "R1,100 per session",
            availability = "Available today"
        ),
        Doctor(
            id = "7",
            name = "Dr. Bongani Zulu",
            specialty = "Dentist",
            rating = "4.8",
            distance = "1.5 km away",
            experience = "11 years experience",
            price = "R750 per consultation",
            availability = "Available tomorrow"
        ),
        Doctor(
            id = "8",
            name = "Dr. Precious Mthembu",
            specialty = "Gynecologist",
            rating = "4.9",
            distance = "3.0 km away",
            experience = "16 years experience",
            price = "R1,000 per consultation",
            availability = "Available next week"
        )
    )
}

/**
 * Data classes matching the Android app's API interface
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

