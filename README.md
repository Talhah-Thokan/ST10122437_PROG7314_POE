# 🏥 MedAssist - South African Healthcare App

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-orange.svg)](https://firebase.google.com)
[![CI/CD](https://github.com/Talhah-Thokan/ST10122437_PROG7314_POE_Part2_TalhahThokan/workflows/Android%20CI%2FCD/badge.svg)](https://github.com/Talhah-Thokan/ST10122437_PROG7314_POE_Part2_TalhahThokan/actions)
[![License](https://img.shields.io/badge/License-Academic-yellow.svg)](LICENSE)

**MedAssist** is a comprehensive mobile healthcare application designed for South African users, providing seamless access to health articles, doctor bookings, and medical information. Built as a final POE project for BCAD PROG7314.

---

## 📋 Table of Contents

- [Features](#-key-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Setup Instructions](#-setup-instructions)
- [Running the Ktor Server](#-running-the-ktor-server)
- [Testing Notifications](#-testing-notifications)
- [CI/CD](#-cicd)
- [Unit Tests](#-unit-tests)
- [Screenshots](#-screenshots)
- [Project Structure](#-project-structure)
- [System Notification Log](#-system-notification-log)

---

## ✨ Key Features

### 🔐 **Authentication**
- **Google Sign-In (SSO)** via Firebase Authentication
- Guest Mode for quick access
- Persistent login state with SharedPreferences

### 📚 **Health Articles**
- 5 South African healthcare articles
- Topics: Diabetes, mental health (SADAG), medical aids, nutrition, winter health
- Full article view with author attribution
- **Offline Support** - Articles cached in Room database
- **REST API Integration** - Fetches from Ktor server first, falls back to Firebase

### 👨‍⚕️ **Doctor Search & Booking**
- **8 SA Doctors** across specialties (GP, Cardiology, Pediatrics, etc.)
- **Working Search** - Filter by name, specialty, or location
- **ZAR Pricing** - R650 to R1,450 per consultation
- **Real Hospitals** - Sandton, Rosebank, Milpark, Morningside, Sunninghill
- Appointment booking via REST API with Firebase fallback
- Booking confirmation screen

### ⚙️ **Settings & Preferences**
- **Light/Dark Theme Toggle** - Instant theme switching
- **Notification Preferences** - Enable/disable push notifications
- **Multi-Language Support** - English, Afrikaans, isiZulu
- **Profile Image Upload** - Firebase Storage integration
- Database seeding tool
- User profile management

### 🔔 **Firebase Cloud Messaging (FCM)**
- Push notification support
- Token registration and Firestore storage
- Notification handling with UI display
- Appointment reminders ready

### 📱 **Offline Mode**
- Room database for local article storage
- Offline-first architecture
- Automatic sync when online
- "Offline Mode Active" indicator

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|------------|
| **Language** | Kotlin |
| **UI Framework** | Traditional Android Views (XML + Material Components) |
| **Architecture** | MVVM-inspired with Repository Pattern |
| **Backend** | Firebase (Auth, Firestore, FCM, Storage) |
| **REST API** | Ktor Server (localhost) |
| **Local Database** | Room Database |
| **Networking** | Retrofit 2.9.0 |
| **Image Loading** | Glide 4.13.2 |
| **Authentication** | Firebase Auth (Google Sign-In) |
| **Build Tool** | Gradle 7.2 |
| **CI/CD** | GitHub Actions |
| **Min SDK** | API 21 (Android 5.0) |
| **Target SDK** | API 31 (Android 12) |

---

## 🏗️ Architecture

### Data Flow Diagram

\'\'\'
┌─────────────────┐
│   UI Layer      │
│ (Activities/    │
│  Fragments)     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ DataRepository   │ ◄─── Multi-tier Strategy
└────────┬────────┘
         │
    ┌────┴────┬──────────┬──────────┐
    │         │          │          │
    ▼         ▼          ▼          ▼
┌──────┐ ┌────────┐ ┌─────────┐ ┌──────┐
│ Room │ │ REST   │ │Firebase │ │Firebase│
│  DB  │ │  API   │ │Firestore│ │Storage│
└──────┘ └────────┘ └─────────┘ └──────┘
\'\'\'

### Strategy Pattern

1. **Offline-First**: Load from Room database (immediate response)
2. **REST API**: Primary remote source (Ktor server)
3. **Firebase Fallback**: Secondary remote source (Firestore)
4. **Sync**: Update Room database with fresh data when online

---

## 🚀 Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or newer
- JDK 17
- Android SDK API 21+
- Firebase account (for full functionality)
- Kotlin 1.9.0+ (for Ktor server)

### 1. Clone Repository

\'\'\'bash
git clone https://github.com/Talhah-Thokan/ST10122437_PROG7314_POE_Part2_TalhahThokan.git
cd ST10122437_PROG7314_POE_Part2_TalhahThokan
\'\'\'

### 2. Firebase Setup

#### Option A: Use Existing Firebase Project
The project is already configured with Firebase project `medassistpoe`.

#### Option B: Create Your Own
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a new project
3. Add Android app with package: `com.medassist.app`
4. Download `google-services.json` and place in `app/` folder
5. Enable Google Sign-In in Authentication section
6. Enable Firestore Database
7. Enable Firebase Storage
8. Enable Firebase Cloud Messaging
9. Add your SHA-1 fingerprint:
   \'\'\'bash
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   \'\'\'

### 3. Build and Run

\'\'\'bash
# Sync Gradle dependencies
./gradlew build

# Install debug APK
./gradlew installDebug
\'\'\'

Or use Android Studio's **Run** button (▶️).

### 4. Seed Firestore Database
1. Run the app
2. Go to **Settings** tab
3. Tap **"Seed Firestore Database"**
4. Wait for success message
5. Restart app to see data

---

## 🖥️ Running the Ktor Server

The app includes a local REST API server built with Ktor.

### Start the Server

\'\'\'bash
cd server
./gradlew run
\'\'\'

The server will start on `http://localhost:8080`

### Server URLs

- **Android Emulator**: `http://10.0.2.2:8080`
- **Physical Device (same network)**: `http://YOUR_COMPUTER_IP:8080`
- **Localhost**: `http://localhost:8080`

### Update API URL (if needed)

Edit `app/src/main/java/com/medassist/app/data/api/ApiClient.kt`:

\'\'\'kotlin
// For physical device, update with your computer's IP
private const val BASE_URL_PHYSICAL = "http://192.168.1.100:8080/"
\'\'\'

### Test Server Endpoints

\'\'\'bash
# Test articles endpoint
curl http://localhost:8080/articles

# Test providers endpoint
curl http://localhost:8080/providers

# Test bookings endpoint
curl -X POST http://localhost:8080/bookings \
  -H "Content-Type: application/json" \
  -d \'{
    "patientName": "John Doe",
    "patientEmail": "john@example.com",
    "appointmentDate": "2025-10-15",
    "appointmentTime": "10:00 AM",
    "reason": "General checkup"
  }\'
\'\'\'

---

## 🔔 Testing Notifications

### Method 1: Firebase Console

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Select your project
3. Navigate to **Cloud Messaging**
4. Click **"Send test message"**
5. Enter your FCM token (check Logcat for `MedAssistFCMService`)
6. Enter title and message
7. Click **"Test"**

### Method 2: Check Logcat

The app logs FCM tokens automatically:

\'\'\'bash
adb logcat | grep MedAssistFCMService
\'\'\'

Look for: `🔄 FCM Token refreshed: <your-token>`

### Method 3: View Token in Firestore

1. Open Firestore Console
2. Navigate to `users/{userId}`
3. Check `fcmToken` field

---

## 🔄 CI/CD

### GitHub Actions Workflow

The project includes automated CI/CD via GitHub Actions.

**Workflow File**: `.github/workflows/android-ci.yml`

**What it does**:
- Runs on push and pull requests
- Sets up JDK 17
- Builds the project
- Runs unit tests
- Generates debug and release APKs
- Uploads APKs as artifacts

**View Workflow**: [Actions Tab](https://github.com/Talhah-Thokan/ST10122437_PROG7314_POE_Part2_TalhahThokan/actions)

**Download APKs**: After workflow completes, download from Actions artifacts

---

## 🧪 Unit Tests

### Run Tests

\'\'\'bash
./gradlew test
\'\'\'

### Test Coverage

- **PreferenceManagerTest**: Tests user preferences, login state, theme settings
- **MedAssistApiTest**: Tests API service mocking and response handling

### Test Results

Tests are located in:
- `app/src/test/java/com/medassist/app/utils/PreferenceManagerTest.kt`
- `app/src/test/java/com/medassist/app/data/api/MedAssistApiTest.kt`

---

## 📸 Screenshots

*Screenshots will be added here*

### Screens to Capture:
- Login screen with Google Sign-In
- Home screen with bottom navigation
- Articles list
- Article detail view
- Doctor search
- Booking form
- Settings with language selection
- Profile image upload
- Dark mode

---

## 📂 Project Structure

\'\'\'
app/
├── src/main/
│   ├── java/com/medassist/app/
│   │   ├── data/
│   │   │   ├── api/              # REST API interfaces (Retrofit)
│   │   │   ├── firebase/         # Firebase repositories
│   │   │   ├── local/            # Room database (entities, DAOs)
│   │   │   └── repository/       # Unified DataRepository
│   │   ├── services/             # FCM service
│   │   ├── ui/
│   │   │   ├── fragments/        # Home, Articles, Bookings
│   │   │   └── screens/          # Activities (Login, Settings, etc.)
│   │   └── utils/               # Helpers (PreferenceManager, LocaleHelper, NetworkUtils)
│   ├── res/
│   │   ├── values/               # English strings
│   │   ├── values-af/            # Afrikaans strings
│   │   ├── values-zu/            # isiZulu strings
│   │   └── layout/              # XML layouts
│   └── AndroidManifest.xml
├── google-services.json         # Firebase config
└── build.gradle.kts             # Dependencies

server/                          # Ktor REST API server
├── src/main/kotlin/
│   └── com/medassist/server/
│       └── Application.kt       # Server entry point
└── build.gradle.kts

.github/workflows/
└── android-ci.yml               # CI/CD workflow
\'\'\'

---

## 🔥 Firebase Collections

### `articles`
\'\'\'json
{
  "title": "Managing Diabetes in South Africa",
  "author": "Dr. Thabo Ndlovu",
  "summary": "...",
  "content": "...",
  "imageUrl": "...",
  "date": "2025-10-05"
}
\'\'\'

### `doctors`
\'\'\'json
{
  "name": "Dr. Thabo Mokoena",
  "specialty": "General Practitioner",
  "rating": "4.9",
  "price": "R650 per consultation",
  "location": "Sandton Medical Centre",
  "searchTerms": ["general", "gp", "thabo"]
}
\'\'\'

### `bookings`
\'\'\'json
{
  "doctorId": "abc123",
  "patientName": "John Doe",
  "patientEmail": "john@example.com",
  "appointmentDate": "2025-10-15",
  "appointmentTime": "10:00 AM",
  "status": "confirmed"
}
\'\'\'

### `users`
\'\'\'json
{
  "uid": "user123",
  "name": "John Doe",
  "email": "john@example.com",
  "photoUrl": "https://...",
  "fcmToken": "token123",
  "lastLogin": 1234567890
}
\'\'\'

---

## 🌐 Multi-Language Support

The app supports three languages:

- **English (en)** - Default
- **Afrikaans (af)** - South African language
- **isiZulu (zu)** - South African language

### Changing Language

1. Go to **Settings**
2. Select language from dropdown
3. App restarts with new language

### String Resources

- `res/values/strings.xml` - English
- `res/values-af/strings.xml` - Afrikaans
- `res/values-zu/strings.xml` - isiZulu

---

## 📱 Offline Mode

### How It Works

1. **First Launch**: App fetches articles from REST API/Firebase
2. **Caching**: Articles saved to Room database
3. **Offline Access**: When internet is unavailable, articles load from Room
4. **Sync**: When back online, app syncs with remote sources

### Testing Offline Mode

1. Load articles while online
2. Turn off WiFi/Mobile data
3. Restart app
4. Articles should load from cache
5. Toast message: "Offline Mode Active - Showing cached articles"

---

## 🔐 APK Signing

### Debug Build

Debug builds are automatically signed with debug keystore.

### Release Build

To create a signed release APK:

1. Generate keystore:
\'\'\'bash
keytool -genkey -v -keystore medassist-release.keystore -alias medassist -keyalg RSA -keysize 2048 -validity 10000
\'\'\'

2. Create `keystore.properties` in project root:
\'\'\'properties
storePassword=your_store_password
keyPassword=your_key_password
keyAlias=medassist
storeFile=../medassist-release.keystore
\'\'\'

3. Update `app/build.gradle.kts` with signing config (see code)

4. Build release APK:
\'\'\'bash
./gradlew assembleRelease
\'\'\'

---

## 📝 Logging

The app uses extensive logging for debugging:

- **API Calls**: `🔄`, `✅`, `❌` emojis for status
- **Firestore Operations**: Logged with collection/document paths
- **Room Sync**: Logged with article counts
- **Authentication**: Logged with user IDs
- **Language Switching**: Logged with language codes

### View Logs

\'\'\'bash
adb logcat | grep MedAssist
\'\'\'

---

## 📊 System Notification Log

Here is a sample logcat output when a test notification is triggered from the app's settings screen. This demonstrates that the `MedAssistFirebaseMessagingService` is correctly creating and displaying a system notification.

\'\'\'logcat
2025-11-18 18:14:44.587 11587-11587 SettingsActivity        com.medassist.app                    D  🔔 Test notification button clicked
2025-11-18 18:14:44.587 11587-11587 SettingsActivity        com.medassist.app                    D  🔔 Testing notification...
// MedAssistFirebaseMessagingService logs the creation of the notification
2025-11-18 18:14:44.600 11587-11630 MedAssistFCMService     com.medassist.app                    D  📨 FCM Message received from: /topics/test
2025-11-18 18:14:44.601 11587-11630 MedAssistFCMService     com.medassist.app                    D  📦 Message data payload: {title=Test Notification, body=This is a test from MedAssist}
2025-11-18 18:14:44.601 11587-11630 MedAssistFCMService     com.medassist.app                    D  ✅ Displaying notification: Test Notification - This is a test from MedAssist (type: general)
2025-11-18 18:14:44.602 11587-11630 MedAssistFCMService     com.medassist.app                    D  🔔 Creating notification: Test Notification
2025-11-18 18:14:44.650 11587-11630 MedAssistFCMService     com.medassist.app                    D  ✅ Notification displayed: Test Notification
2025-11-18 18:14:44.656 11587-11587 SettingsActivity        com.medassist.app                    D  ✅ Test notification displayed
\'\'\'

The following log from the `NotificationService` shows the previous issue where too many toasts were being queued, which is now resolved.

\'\'\'logcat
2025-11-18 16:32:01.317   689-1143  NotificationService     system_server                        E  Package has already queued 5 toasts. Not showing more. Package=com.medassist.app
\'\'\'

---

## 🐛 Troubleshooting

### Google Sign-In Not Working
- Ensure SHA-1 fingerprint is added to Firebase Console
- Check `google-services.json` has `oauth_client` entries
- Verify Google Sign-In is enabled in Firebase Authentication
- Look for `ApiException: statusCode=10` in logcat, which indicates a developer error related to configuration:
  \'\'\'logcat
  2025-11-18 16:35:52.679  8841-8841  FirebaseAuthManager     pid-8841                             E  ❌ Google Sign-In ApiException: statusCode=10, message=10:
  2025-11-18 16:35:52.679  8841-8841  MedAssistLoginActivity  pid-8841                             E  ❌ Google Sign-In failed: Developer error - check Firebase configuration and SHA-1 fingerprint
  \'\'\'

### REST API Not Connecting
- Ensure Ktor server is running (`cd server && ./gradlew run`)
- Check server URL in `ApiClient.kt` matches your setup
- For physical device, use computer's IP address (not localhost)

### No Articles/Doctors Loading
- Run database seeder: Settings → "Seed Firestore Database"
- Check Firestore rules allow read access
- Check internet connection
- Check Logcat for error messages

### Notifications Not Working
- Check FCM token in Logcat
- Verify token is saved in Firestore
- Ensure notifications are enabled in Settings
- Check Firebase Console Cloud Messaging setup

### Offline Mode Not Working
- Ensure articles were loaded at least once while online
- Check Room database is initialized
- Check Logcat for Room errors

---

## 📄 License

This project is created for educational purposes as part of a BCAD module. All rights reserved.

---

## 👨‍💻 Developer

**Student:** Talhah Thokan  
**Student ID:** ST10122437  
**Module:** PROG7314 - Advanced Programming  
**Institution:** IIE Varsity College Sandton  
**Year:** 2025  
**Semester:** 2  

---

## 🔗 Links

- **GitHub Repository:** [ST10122437_PROG7314_POE_Part2_TalhahThokan](https://github.com/Talhah-Thokan/ST10122437_PROG7314_POE_Part2_TalhahThokan)
- **Firebase Console:** [medassistpoe](https://console.firebase.google.com)
- **CI/CD Workflow:** [GitHub Actions](https://github.com/Talhah-Thokan/ST10122437_PROG7314_POE_Part2_TalhahThokan/actions)
- **Demo Video:** *(To be added)*

---

## 🤖 AI Assistance Disclaimer

This project was developed with assistance from AI technologies (Claude/ChatGPT) for:
- Code generation and boilerplate
- Architecture guidance and best practices
- Firebase integration setup
- UI/UX implementation with Material Design
- Documentation generation
- Multi-language translations

**Human oversight:** All AI-generated code was reviewed, tested, modified, and validated to ensure quality, functionality, and academic integrity. The developer maintains full understanding of the codebase and architectural decisions.

---

*Last Updated: November 2025*
