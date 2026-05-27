# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

```bash
# Build
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK (requires signing config)
./gradlew installDebug           # Build and install on connected device

# Quality
./gradlew lint                   # Run Android lint

# Testing
./gradlew test                   # Unit tests (JVM)
./gradlew connectedAndroidTest   # Instrumented tests (requires device/emulator)
./gradlew testDebugUnitTest      # Single build-variant unit test run

# Utility
./gradlew clean                  # Clean build outputs
```

On Windows use `gradlew.bat` instead of `./gradlew`.

## Architecture

**Pattern:** Clean Architecture + MVVM with Unidirectional Data Flow

```
Presentation  →  Domain (Use Cases)  →  Data (Repositories)  →  Remote API
   (Compose         (pure Kotlin,          (implementations,      (Retrofit)
   + ViewModels)     no Android deps)       DTO mapping)
```

**Single Activity:** `MainActivity` hosts the entire app via Jetpack Navigation Compose. All routing lives in `navigation/MovieNavHost.kt` using string-based routes.

**Data flow per feature:**
1. Compose screen observes ViewModel state (`mutableStateOf` / `StateFlow`)
2. ViewModel calls a Use Case (never a Repository directly)
3. Use Case applies validation/business logic, calls Repository interface
4. Repository implementation calls a Retrofit API, maps DTO → domain model
5. JWT token is auto-injected on every request by `core/network/` OkHttp interceptor reading from `AuthDataStore`

**Authentication persistence:** `data/local/datastore/AuthDataStore.kt` stores the JWT token and user ID via DataStore Preferences. `NetworkModule` injects an `OkHttp` interceptor that reads this store synchronously (`runBlocking`) to add the `Authorization: Bearer <token>` header.

## Package Structure

```
com.movie2night/
├── core/
│   ├── network/          # Retrofit instance, OkHttp client, JWT interceptor
│   └── notifications/    # FirebaseMessagingService (FCM push handler)
├── data/
│   ├── local/datastore/  # AuthDataStore
│   ├── remote/api/       # Retrofit interfaces (Auth, Movie, Match, Chat, User)
│   ├── remote/dto/       # DTOs with .toDomain() extension functions
│   └── repository/       # Repository implementations (5 total)
├── di/                   # Hilt AppModule (binds repositories, use cases, APIs)
├── domain/
│   ├── model/            # Entities: User, Movie, Session, Match, Interest, Message, Cinema, Rating, Report
│   ├── repository/       # Repository interfaces (no Android imports)
│   └── usecase/          # Business logic entry points (5 use cases)
├── navigation/           # Routes constants + MovieNavHost composable
├── presentation/         # Screens + ViewModels grouped by feature:
│   │                     #   auth, home, chat, match, matches, profile, session
├── ui/theme/             # Material Design 3 theme, colors, typography
├── MainActivity.kt
└── MovieApp.kt           # @HiltAndroidApp application class
```

## Key Technical Details

**Backend:** Dev backend runs at `http://10.0.2.2:8080/` (Android emulator localhost). `usesCleartextTraffic=true` is set for development.

**Dependency Injection:** Hilt. All ViewModels use `@HiltViewModel`. Repository bindings and network graph are wired in `di/AppModule.kt` and `core/network/NetworkModule.kt`.

**Image loading:** Coil (`coil-compose`).

**Navigation routes** (defined in `navigation/` as string constants):
`login`, `register`, `create_profile`, `home`, `movies/{movieId}/sessions`, `interested/{sessionId}`, `matches`, `chat/{matchId}`, `profile/{userId}`, plus check-in and rating routes.

**Versions:** minSdk 26, compileSdk/targetSdk 35, Kotlin 2.1.0, Compose BOM 2024.12.01, Hilt 2.52, Retrofit 2.11.0.

**DTO convention:** All DTOs in `data/remote/dto/` expose a `.toDomain()` extension function. Repositories always return domain models, never DTOs.

**UiState convention:** ViewModels expose a data class (e.g., `AuthUiState`) containing `isLoading: Boolean`, `errorMessage: String?`, and feature-specific fields. Screens observe this via `collectAsState()` or `by remember { mutableStateOf(...) }`.
