package com.medassist.app.data.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Firestore Data Seeder
 * Populates the Firestore database with sample data for development and testing
 */
class FirestoreDataSeeder {
    
    private val firestore = FirebaseFirestore.getInstance()
    
    companion object {
        private const val TAG = "FirestoreDataSeeder"
        
        // Collection names
        private const val COLLECTION_ARTICLES = "articles"
        private const val COLLECTION_DOCTORS = "doctors"
    }
    
    /**
     * Seeds the database with sample articles (South African healthcare context)
     */
    suspend fun seedArticles() {
        try {
            val articles = listOf(
                mapOf(
                    "title" to "Managing Diabetes in South Africa: A Comprehensive Guide",
                    "author" to "Dr. Thabo Ndlovu",
                    "summary" to "With diabetes on the rise in SA, learn how to manage your blood sugar levels and live a healthy life.",
                    "content" to "Diabetes is a growing concern in South Africa, affecting millions of people. This comprehensive guide covers blood sugar management, dietary recommendations tailored to South African cuisine, exercise tips, and how to access affordable medication through public healthcare. Learn about the latest diabetes management techniques available at local clinics and hospitals.",
                    "imageUrl" to "https://via.placeholder.com/400x250/008B8B/FFFFFF?text=MedAssist+Health",
                    "date" to "2025-10-05"
                ),
                mapOf(
                    "title" to "Winter Health Tips for South Africans",
                    "author" to "Dr. Zanele Khumalo",
                    "summary" to "Stay healthy this winter with practical tips for preventing flu, colds, and other seasonal illnesses.",
                    "content" to "As winter approaches in South Africa, it's important to protect yourself from seasonal illnesses. This article covers vaccination schedules, immune-boosting foods available in local markets, proper layering for cold mornings, and when to visit your local clinic. Learn about free flu vaccinations available at public health facilities.",
                    "imageUrl" to "https://via.placeholder.com/400x250/20B2AA/FFFFFF?text=MedAssist+Health",
                    "date" to "2025-10-02"
                ),
                mapOf(
                    "title" to "Mental Health Resources in South Africa",
                    "author" to "Dr. Sipho Mkhize",
                    "summary" to "Mental health matters. Discover free and affordable mental health resources available across South Africa.",
                    "content" to "Mental health is a priority, and South Africa offers various support systems. This guide covers SADAG (South African Depression and Anxiety Group), community health centers, online counseling services, and how to access mental health care through medical aid schemes. Learn about free helplines and support groups in your area.",
                    "imageUrl" to "https://via.placeholder.com/400x250/006666/FFFFFF?text=MedAssist+Health",
                    "date" to "2025-09-28"
                ),
                mapOf(
                    "title" to "Understanding Medical Aid Schemes in South Africa",
                    "author" to "Dr. Lerato Mokoena",
                    "summary" to "Navigate the complex world of medical aids and choose the right plan for you and your family.",
                    "content" to "Choosing the right medical aid in South Africa can be overwhelming. This article breaks down the differences between schemes like Discovery, Momentum, Bonitas, and Medihelp. Learn about PMBs (Prescribed Minimum Benefits), hospital plans vs comprehensive plans, and how to maximize your benefits. Includes tips for those without medical aid on accessing quality healthcare.",
                    "imageUrl" to "https://via.placeholder.com/400x250/008B8B/FFFFFF?text=MedAssist+Health",
                    "date" to "2025-09-25"
                ),
                mapOf(
                    "title" to "Healthy Eating on a Budget in South Africa",
                    "author" to "Dr. Nomvula Dlamini",
                    "summary" to "Nutritious meals don't have to be expensive. Learn how to eat healthy with affordable South African ingredients.",
                    "content" to "Eating healthy is possible even on a tight budget in South Africa. This guide covers affordable nutritious foods available at local markets, meal planning with staples like pap, beans, and seasonal vegetables, and budget-friendly protein sources. Learn how to create balanced meals for under R50 per person and discover community feeding programs and food gardens.",
                    "imageUrl" to "https://via.placeholder.com/400x250/20B2AA/FFFFFF?text=MedAssist+Health",
                    "date" to "2025-09-20"
                )
            )
            
            for (article in articles) {
                firestore.collection(COLLECTION_ARTICLES)
                    .add(article)
                    .await()
            }
            
            Log.d(TAG, "Successfully seeded ${articles.size} articles")
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding articles", e)
        }
    }
    
    /**
     * Seeds the database with sample doctors (South African doctors with ZAR pricing)
     */
    suspend fun seedDoctors() {
        try {
            val doctors = listOf(
                mapOf(
                    "name" to "Dr. Thabo Mokoena",
                    "specialty" to "General Practitioner",
                    "rating" to "4.9",
                    "distance" to "1.2 km away",
                    "experience" to "12 years experience",
                    "price" to "R650 per consultation",
                    "availability" to "Available today",
                    "location" to "Sandton Medical Centre, Johannesburg",
                    "searchTerms" to listOf("general", "family", "gp", "practitioner", "thabo", "mokoena", "sandton", "johannesburg")
                ),
                mapOf(
                    "name" to "Dr. Zanele Khumalo",
                    "specialty" to "Cardiologist",
                    "rating" to "4.8",
                    "distance" to "3.5 km away",
                    "experience" to "18 years experience",
                    "price" to "R1,250 per consultation",
                    "availability" to "Available tomorrow",
                    "location" to "Life Healthcare, Rosebank",
                    "searchTerms" to listOf("cardiology", "heart", "cardiovascular", "zanele", "khumalo", "rosebank")
                ),
                mapOf(
                    "name" to "Dr. Sipho Dlamini",
                    "specialty" to "Pediatrician",
                    "rating" to "4.9",
                    "distance" to "2.1 km away",
                    "experience" to "15 years experience",
                    "price" to "R850 per consultation",
                    "availability" to "Available today",
                    "location" to "Netcare Milpark Hospital, Johannesburg",
                    "searchTerms" to listOf("pediatrics", "children", "kids", "baby", "sipho", "dlamini", "milpark")
                ),
                mapOf(
                    "name" to "Dr. Lerato Ndlovu",
                    "specialty" to "Dermatologist",
                    "rating" to "4.7",
                    "distance" to "4.8 km away",
                    "experience" to "10 years experience",
                    "price" to "R950 per consultation",
                    "availability" to "Available next week",
                    "location" to "Morningside Mediclinic, Sandton",
                    "searchTerms" to listOf("dermatology", "skin", "dermatologist", "lerato", "ndlovu", "morningside")
                ),
                mapOf(
                    "name" to "Dr. Mandla Mbatha",
                    "specialty" to "Orthopedic Surgeon",
                    "rating" to "4.8",
                    "distance" to "5.2 km away",
                    "experience" to "22 years experience",
                    "price" to "R1,450 per consultation",
                    "availability" to "Available tomorrow",
                    "location" to "Sunninghill Hospital, Sandton",
                    "searchTerms" to listOf("orthopedics", "orthopedic", "bone", "joint", "surgeon", "mandla", "mbatha", "sunninghill")
                ),
                mapOf(
                    "name" to "Dr. Nomvula Nkosi",
                    "specialty" to "Psychiatrist",
                    "rating" to "4.9",
                    "distance" to "2.7 km away",
                    "experience" to "14 years experience",
                    "price" to "R1,100 per session",
                    "availability" to "Available today",
                    "location" to "Parktown Medical Centre, Johannesburg",
                    "searchTerms" to listOf("psychiatry", "mental", "health", "therapy", "nomvula", "nkosi", "parktown")
                ),
                mapOf(
                    "name" to "Dr. Bongani Zulu",
                    "specialty" to "Dentist",
                    "rating" to "4.8",
                    "distance" to "1.5 km away",
                    "experience" to "11 years experience",
                    "price" to "R750 per consultation",
                    "availability" to "Available tomorrow",
                    "location" to "Rosebank Dental Clinic, Johannesburg",
                    "searchTerms" to listOf("dentist", "dental", "teeth", "oral", "bongani", "zulu", "rosebank")
                ),
                mapOf(
                    "name" to "Dr. Precious Mthembu",
                    "specialty" to "Gynecologist",
                    "rating" to "4.9",
                    "distance" to "3.0 km away",
                    "experience" to "16 years experience",
                    "price" to "R1,000 per consultation",
                    "availability" to "Available next week",
                    "location" to "Charlotte Maxeke Hospital, Johannesburg",
                    "searchTerms" to listOf("gynecology", "women", "gynecologist", "obgyn", "precious", "mthembu", "charlotte")
                )
            )
            
            for (doctor in doctors) {
                firestore.collection(COLLECTION_DOCTORS)
                    .add(doctor)
                    .await()
            }
            
            Log.d(TAG, "Successfully seeded ${doctors.size} doctors")
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding doctors", e)
        }
    }
    
    /**
     * Seeds all sample data
     */
    suspend fun seedAllData() {
        Log.d(TAG, "Starting database seeding...")
        
        try {
            seedArticles()
            seedDoctors()
            
            Log.d(TAG, "Database seeding completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error during database seeding", e)
        }
    }
}
