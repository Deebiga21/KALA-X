# KALA-X: From Handmade to Market-Ready

KALA-X is an AI-powered platform designed to empower traditional artisans. It bridges the gap between handmade craftsmanship and modern e-commerce by providing a frictionless, single-screen experience to digitize, price, and publish artisan goods using edge AI.

## 📱 Project Architecture

KALA-X consists of two main components:
1. **Android App (`/app`)**: A native Kotlin application built with Jetpack Compose. It leverages on-device AI (Llama 3B via INT4 quantization, MediaPipe Vision) for instant, offline processing to preserve battery life and privacy.
2. **FastAPI Backend (`/backend`)**: A lightweight Python server that handles cloud synchronization, LoRA (Low-Rank Adaptation) model fine-tuning for the Personal Learning Loop, and final e-commerce publishing.

## ✨ Core Features

* **Continuous Magical UI**: A fluid, single-screen scroll experience built with Jetpack Compose `LazyColumn` and `AnimatedVisibility`. No disjointed pages.
* **On-Device Vision & Speech**: Uses MediaPipe to instantly remove backgrounds from artisan photos and transcribe spoken descriptions completely offline.
* **Personal Learning Loop (Hybrid AI Architecture)**:
    * **Short-Term Memory (Edge RAG)**: Instantly injects the artisan's recent pricing/text corrections into the on-device prompt.
    * **Long-Term Memory (Cloud LoRA)**: A nightly `WorkManager` syncs corrections to the backend, which trains a personalized 3MB LoRA adapter and pushes it back to the phone.
* **Smart Pricing Engine**: Calculates material, labor, and packaging costs against the artisan's base hourly rate to suggest competitive, profitable pricing.
* **Commerce Readiness Score**: Evaluates the generated listing for e-commerce viability before publishing.

## 🛠 Tech Stack

**Mobile (Android):**
* Kotlin, Jetpack Compose, Material 3
* Room Database (Offline-first architecture)
* Kotlin Coroutines & Flow
* AndroidX WorkManager
* Retrofit & OkHttp

**Backend:**
* Python, FastAPI
* Uvicorn
* SQLAlchemy (SQLite for prototype)

## 🚀 Getting Started

### Prerequisites
* Android Studio (Ladybug or newer)
* Python 3.10+
* An Android device or emulator (Target SDK 35)

### Running the Backend
```bash
cd backend
python -m venv venv
source venv/bin/activate  # Or `venv\Scripts\activate` on Windows
pip install -r requirements.txt
python -m uvicorn app.main:app --host 0.0.0.0 --port 8000
```

### Running the Android App
1. Open the project root in Android Studio.
2. Sync Gradle files.
3. If running the backend locally on an emulator, ensure the Retrofit base URL in `KalaApi.kt` points to `http://10.0.2.2:8000/`.
4. Click **Run** (`Shift + F10`).

## 🤝 Contributing
Contributions, issues, and feature requests are welcome!

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
