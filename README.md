<div align="center">
  <br />
  <img src="https://img.shields.io/badge/version-1.0.0-6750A4?style=flat-square" />
  <img src="https://img.shields.io/badge/platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/minSdk-26-brightgreen?style=flat-square" />
  <img src="https://img.shields.io/badge/license-MIT-blue?style=flat-square" />
  <br /><br />

  <h1>Movie2Night</h1>
  <p><strong>Find someone to go to the movies with.</strong></p>
  <p>
    Movie2Night connects people who want to watch the same film,<br/>
    at the same session, at the same cinema — safely and effortlessly.
  </p>

  <br />

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Hilt](https://img.shields.io/badge/Hilt-FF6F00?style=for-the-badge&logo=google&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Retrofit](https://img.shields.io/badge/Retrofit-48B983?style=for-the-badge&logo=square&logoColor=white)

<br /><br />

> ⚠️ This project is currently in active development. Backend integration is in progress.

</div>

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Security](#security)
- [Roadmap](#roadmap)

---

## Overview

Movie2Night is an Android application designed to solve a common problem: wanting to go to the movies but not having anyone to go with.

The flow is straightforward:

1. **Browse** films currently showing and pick a session
2. **Express interest** in that session
3. **See who else** wants to attend the same screening
4. **Send a match request** — if accepted, a private chat is unlocked
5. **Meet at the cinema**, check in, and rate the experience afterward

Privacy and safety are first-class concerns. Location data is never shared precisely, chat is only available after a mutual match, and every screen exposes report and block actions.

---

## Features

| Area | Feature | Status |
|---|---|:---:|
| **Auth** | Registration with age validation (18+) | ✅ |
| **Auth** | JWT-based login with DataStore persistence | ✅ |
| **Profile** | Profile creation with photo, bio and intention | ✅ |
| **Profile** | Profile editing | ✅ |
| **Profile** | Public user profile view | ✅ |
| **Discovery** | Browse films currently showing | ✅ |
| **Discovery** | Sessions filtered by city | ✅ |
| **Matching** | Express interest in a session | ✅ |
| **Matching** | View other interested users | ✅ |
| **Matching** | Send and respond to match requests | ✅ |
| **Chat** | Real-time messaging after a match | ✅ |
| **Safety** | Report and block any user | ✅ |
| **Safety** | Cinema check-in with location validation | ✅ |
| **Safety** | Post-session rating system | ✅ |
| **Notifications** | Push notifications via Firebase (match events) | ✅ |

---

## Architecture

The project follows **Clean Architecture** with the **MVVM** presentation pattern. Dependencies flow strictly inward — outer layers know about inner layers, never the reverse.

```
┌──────────────────────────────────────────────────────┐
│                    Presentation                      │
│                                                      │
│   @Composable Screens  ──►  ViewModels               │
│                              │                       │
│                         UiState (sealed)             │
└──────────────────────────────┬───────────────────────┘
                               │ invokes
┌──────────────────────────────▼───────────────────────┐
│                      Domain                          │
│                                                      │
│   UseCases  ──►  Repository interfaces               │
│       │                                              │
│   Entities  (pure Kotlin — zero Android imports)     │
└──────────────────────────────┬───────────────────────┘
                               │ implemented by
┌──────────────────────────────▼───────────────────────┐
│                       Data                           │
│                                                      │
│   RepositoryImpl  ──►  Remote (Retrofit API)         │
│                   ──►  Local  (Room + DataStore)     │
│                                                      │
│   DTOs with .toDomain() mapping functions            │
└──────────────────────────────┬───────────────────────┘
                               │ wired by
┌──────────────────────────────▼───────────────────────┐
│                    Core / DI                         │
│                                                      │
│   Hilt Modules  ──►  NetworkModule (OkHttp + JWT)   │
│                  ──►  AppModule (repositories +      │
│                        use cases)                    │
│   Navigation    ──►  NavHost + sealed Routes         │
└──────────────────────────────────────────────────────┘
```

### Key decisions

- **Single Activity** — the entire app lives in `MainActivity`. Navigation is handled by Jetpack Navigation Compose.
- **Unidirectional data flow** — ViewModels expose `UiState` via `StateFlow` or Compose `State`. Screens never mutate state directly.
- **Use cases as the single entry point** — ViewModels call use cases, never repositories directly. Business rules live exclusively in the domain layer.
- **JWT interceptor** — `OkHttp` intercepts every outbound request and injects the stored `Authorization: Bearer` header automatically.

---

## Tech Stack

### UI
| Library | Purpose |
|---|---|
| Jetpack Compose | Declarative UI toolkit |
| Material Design 3 | Design system and theming |
| Coil | Asynchronous image loading |
| Navigation Compose | In-app navigation |

### Architecture & State
| Library | Purpose |
|---|---|
| Hilt | Compile-time dependency injection |
| ViewModel + Lifecycle | Lifecycle-aware state holders |
| Kotlin Coroutines | Structured concurrency |
| Kotlin Flow | Reactive data streams |

### Networking & Persistence
| Library | Purpose |
|---|---|
| Retrofit 2 | Type-safe HTTP client |
| OkHttp 4 | HTTP engine with logging and auth interceptor |
| Room | Local SQLite database with Kotlin DSL |
| DataStore Preferences | Secure key-value storage (replaces SharedPreferences) |

### Platform Services
| Service | Purpose |
|---|---|
| Firebase Cloud Messaging | Push notifications for match events |
| Firebase Analytics | Usage metrics |
| Google Play Services Location | Approximate location for cinema proximity |

---

## Project Structure

```
app/src/main/java/com/movie2night/
│
├── core/
│   ├── network/
│   │   └── NetworkModule.kt          # Retrofit + OkHttp + JWT interceptor
│   └── notifications/
│       └── MovieFirebaseMessagingService.kt
│
├── data/
│   ├── local/
│   │   └── datastore/
│   │       └── AuthDataStore.kt      # Token persistence
│   ├── remote/
│   │   ├── api/
│   │   │   ├── AuthApi.kt
│   │   │   ├── ChatApi.kt
│   │   │   ├── MatchApi.kt
│   │   │   ├── MovieApi.kt
│   │   │   └── UserApi.kt
│   │   └── dto/
│   │       ├── AuthDto.kt
│   │       ├── MatchDto.kt
│   │       ├── MovieDto.kt
│   │       └── SessionDto.kt
│   └── repository/
│       ├── AuthRepositoryImpl.kt
│       ├── ChatRepositoryImpl.kt
│       ├── MatchRepositoryImpl.kt
│       ├── MovieRepositoryImpl.kt
│       └── UserRepositoryImpl.kt
│
├── di/
│   └── AppModule.kt                  # Hilt bindings
│
├── domain/
│   ├── model/
│   │   ├── Cinema.kt
│   │   ├── Interest.kt
│   │   ├── Match.kt
│   │   ├── Message.kt
│   │   ├── Movie.kt
│   │   ├── Rating.kt
│   │   ├── Report.kt
│   │   ├── Session.kt
│   │   └── User.kt
│   ├── repository/
│   │   ├── AuthRepository.kt
│   │   ├── ChatRepository.kt
│   │   ├── MatchRepository.kt
│   │   ├── MovieRepository.kt
│   │   └── UserRepository.kt
│   └── usecase/
│       ├── GetMoviesUseCase.kt
│       ├── LoginUseCase.kt
│       ├── RateUserUseCase.kt
│       ├── RegisterUseCase.kt
│       └── SendMatchRequestUseCase.kt
│
├── navigation/
│   ├── MovieNavHost.kt
│   └── Routes.kt
│
├── presentation/
│   ├── auth/
│   │   ├── AuthViewModel.kt
│   │   ├── LoginScreen.kt
│   │   └── RegisterScreen.kt
│   ├── chat/
│   │   ├── ChatScreen.kt
│   │   └── ChatViewModel.kt
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   └── HomeViewModel.kt
│   ├── match/
│   │   ├── InterestedUsersScreen.kt
│   │   └── MatchViewModel.kt
│   └── profile/
│       ├── CreateProfileScreen.kt
│       ├── EditProfileScreen.kt
│       ├── ProfileViewModel.kt
│       └── UserProfileScreen.kt
│
├── ui/
│   └── theme/
│       ├── Theme.kt
│       └── Type.kt
│
├── MainActivity.kt
└── MovieApp.kt
```

---

## Getting Started

### Prerequisites

- Android Studio Meerkat or later
- JDK 17
- Android emulator or physical device running API 26+
- A running instance of the Movie2Night backend

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/EmmanuelVictorio06/movie2night-android.git
cd movie2night-android
```

**2. Open in Android Studio**

```
File → Open → select the project folder
```

Wait for Gradle to sync and download all dependencies automatically.

**3. Configure Firebase**

- Go to the [Firebase Console](https://console.firebase.google.com/) and create a project
- Add an Android app with package name `com.movie2night`
- Download `google-services.json` and place it inside the `app/` directory

> `google-services.json` is excluded from version control. Every developer must configure their own.

**4. Set the backend URL**

Open `core/network/NetworkModule.kt` and update:

```kotlin
private const val BASE_URL = "https://your-backend-url.com/"
```

**5. Run the app**

Select an emulator or connected device and press **Run** (`Shift + F10`).

---

## Security

| Concern | Approach |
|---|---|
| Authentication | JWT stored in DataStore, injected via OkHttp interceptor |
| Location privacy | Approximate location only (`ACCESS_COARSE_LOCATION`) — never shared with other users |
| Chat access | Unlocked exclusively after a mutual match |
| User safety | Report and block actions available on every interaction screen |
| Secrets | `google-services.json` and keystore files excluded via `.gitignore` |

---

## Roadmap

- [ ] Full backend integration (Spring Boot + PostgreSQL)
- [ ] Session detail screen with cinema map
- [ ] Geolocation-based cinema check-in
- [ ] Post-session rating flow
- [ ] Complete dark theme support
- [ ] Google Play closed beta — Curitiba pilot

---

## Contributing

This project is currently in solo development. Contributions, issues and feature requests are welcome once the beta is live.

---

## License

Distributed under the MIT License. See `LICENSE` for more information.

---

<div align="center">
  <sub>Built in Curitiba, Brazil &nbsp;·&nbsp; by <a href="https://github.com/EmmanuelVictorio06">Emmanuel Victorio</a></sub>
</div>
