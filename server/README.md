# MedAssist Ktor Server

REST API server for MedAssist Android application.

## 🚀 Running the Server

### Option 1: Using Gradle Wrapper (Recommended)

```bash
cd server
./gradlew run
```

The server will start on `http://localhost:8080`

### Option 2: Using Android Studio Terminal

1. Open Android Studio
2. Open Terminal (View → Tool Windows → Terminal)
3. Navigate to server folder:
   ```bash
   cd server
   ```
4. Run the server:
   ```bash
   ./gradlew run
   ```

### Option 3: If Gradle Wrapper Doesn't Work

If you get "no such file or directory" error, try:

```bash
cd server
# Use the root project's gradlew
../gradlew -p server run
```

Or from the root directory:

```bash
./gradlew -p server run
```

## 📡 API Endpoints

- **GET /articles** - Returns list of health articles
- **GET /providers** - Returns list of doctors/providers  
- **POST /bookings** - Creates appointment bookings

## 🔧 Troubleshooting

### "zsh: no such file or directory: ./gradlew"

**Solution:** The gradlew script might not be executable or missing. Try:

```bash
chmod +x gradlew
./gradlew run
```

If that doesn't work, use the root project's gradlew:

```bash
cd ..  # Go back to root
./gradlew -p server run
```

### "Unable to locate a Java Runtime"

**Solution:** You need Java installed. Android Studio includes Java, so you can:

1. Use Android Studio's Terminal (it has Java configured)
2. Or install Java: `brew install openjdk@17`

### Server Won't Start

1. Check if port 8080 is already in use:
   ```bash
   lsof -i :8080
   ```
2. Kill the process if needed:
   ```bash
   kill -9 <PID>
   ```
3. Try again: `./gradlew run`

## ✅ Verification

Once the server is running, you should see:

```
Application started in X seconds
```

You can test the endpoints:

- Open browser: `http://localhost:8080/`
- Should see: "MedAssist Ktor Server is running!"

For Android emulator, use: `http://10.0.2.2:8080/`

---

*Server runs on port 8080 by default*
