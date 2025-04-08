# 22110020 - Ho Thanh Dat
## Photo Gallery App
A modern **Android** application built with **Jetpack Compose** that showcases your photos in a smooth, gesture‑driven gallery.  It doubles as an advanced teaching sample: every feature is mapped to a specific UI‑interaction requirement, demonstrating best‑practice Compose patterns and clean architecture (MVVM).

---

## ✨ Key Features

| Interaction requirement | How the app fulfils it | What you’ll learn |
|-------------------------|------------------------|-------------------|
| **Clickable images** | A `LazyVerticalGrid` shows photo thumbnails; tapping one opens a full‑screen view. | Efficient list rendering, item keys, navigation with the Jetpack Navigation component. |
| **Buttons** | "Previous" / "Next" in the detail screen let you page through the collection. | State hoisting, ViewModel‑driven UI, back‑stack manipulation. |
| **Floating Action Button (FAB)** | A speed‑dial FAB expands to: **Pick from gallery · Take photo**. | Material 3 FABs, animations, `rememberLauncherForActivityResult`. |
| **Common gestures** | • **Swipe** left/right to navigate photos  
• **Pinch‑to‑zoom** on a photo  
• **Long‑press** a thumbnail to *favorite* or *delete*. | `pointerInput` gesture detectors, `AnimatedVisibility`, Compose physics‑based animation. |

---

## 📐 Architecture & Tech Stack

* **Kotlin + Jetpack Compose** (Material 3)
* **MVVM** pattern – `ViewModel`, `StateFlow`, `hiltViewModel()` for DI
* **Room** – persistent store for photo metadata
* **Coil** – image loading & caching
* **CameraX** – optional in‑app capture
* **Navigation‑Compose** – type‑safe navigation graph

This stack mirrors production Android apps, giving students hands‑on experience with the components they’ll meet in the wild.

---

## 🚀 Getting Started

1. **Clone** the repo and open in *Android Studio Hedgehog* or newer.
2. Connect an emulator or device running **API 24+**.
3. Click **Run ▶︎** – Gradle will fetch dependencies automatically.
4. Grant storage / camera permissions when prompted.

> **Tip for instructors** – Each feature is isolated in its own commit; you can walk the class through the git history to explain incremental development.

---

## 🏗️ Project Breakdown

### 1. UI Layer (`ui/`)
* **`GalleryScreen`** – grid, FAB, long‑press dialog
* **`PhotoDetailScreen`** – zoom, swipe, nav buttons

### 2. Domain Layer (`data/`, `repository/`)
* `PhotoEntity`, `PhotoDao`, `PhotoRepository`

### 3. Platform Layer (`camera/`, `permission/`)
* `CameraScreen` built on **CameraX**
* Runtime permission helpers for `READ_MEDIA_IMAGES` / `CAMERA`

---

## 🧩 Extending the App

* **Performance** – experiment with paging large photo sets, `SnapshotStateList`, and Coil’s memory policies.
* **Cloud sync** – add Firebase Storage to back up photos.
* **Theming** – wire up dynamic colour & dark‑mode palettes.
* **Testing** – write UI tests with `ComposeTestRule` and Hilt‑enabled unit tests.

---

## 🎓 Learning Outcomes

By completing (or dissecting) this project students will:

1. Build adaptive layouts with Compose **Lazy components**.
2. Handle **gestures** and **animations** idiomatically.
3. Architect an app using **MVVM + DI**.
4. Optimise image loading and **cache strategy** with Coil.
5. Persist data locally via **Room** and observe it reactively with **Flow**.

---

## 📄 License

```
MIT License – do whatever you want, but please attribute.
```

Happy coding & happy shooting! 📸

