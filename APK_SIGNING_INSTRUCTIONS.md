# APK Signing Instructions

## Generating a Release Keystore

To create a signed release APK for Play Store distribution:

### Step 1: Generate Keystore

```bash
keytool -genkey -v -keystore medassist-release.keystore -alias medassist -keyalg RSA -keysize 2048 -validity 10000
```

You will be prompted for:
- Keystore password
- Key password
- Your name and organization details

**Important**: Save these passwords securely! You'll need them to sign future updates.

### Step 2: Create keystore.properties

Create a file named `keystore.properties` in the project root:

```properties
storePassword=your_store_password_here
keyPassword=your_key_password_here
keyAlias=medassist
storeFile=../medassist-release.keystore
```

**Important**: Add `keystore.properties` to `.gitignore` to avoid committing passwords!

### Step 3: Place Keystore File

Place `medassist-release.keystore` in the project root directory (same level as `build.gradle.kts`).

### Step 4: Build Release APK

```bash
./gradlew assembleRelease
```

The signed APK will be generated at:
`app/build/outputs/apk/release/app-release.apk`

## For Development/Testing

The current configuration uses debug signing if `keystore.properties` doesn't exist. This is fine for development but **NOT** for Play Store submission.

## Security Notes

- Never commit `keystore.properties` or `.keystore` files to Git
- Keep backups of your keystore file in a secure location
- If you lose the keystore, you cannot update your app on Play Store
- Use different keystores for different apps

## Verifying APK Signature

```bash
jarsigner -verify -verbose -certs app-release.apk
```

Or use:
```bash
apksigner verify --verbose app-release.apk
```

