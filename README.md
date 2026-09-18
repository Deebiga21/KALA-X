# KALA-X: From Handmade to Market-Ready

![KALA-X UI](https://via.placeholder.com/800x400?text=KALA-X+App+Screenshot+Here)  
*(Insert a GIF or screenshot of the app working here)*

**The Problem:** Traditional artisans and craftsmen create incredible products but struggle to sell them online due to the technical friction of cataloging, writing descriptions, pricing competitively, and navigating e-commerce platforms.  
**Who it's for:** Rural and traditional artisans, small-batch creators, and craft entrepreneurs who need a dead-simple way to digitize their inventory.

KALA-X bridges this gap by providing a frictionless, single-screen experience to digitize, price, and publish artisan goods using advanced on-device edge AI. 

## 🛠 Tech Stack

**Mobile App (Native Android):** Kotlin, Jetpack Compose, Material 3, Room Database, AndroidX WorkManager, Retrofit.  
**Backend:** Python, FastAPI, Uvicorn, SQLAlchemy (SQLite).  
**AI/ML:** Llama 3B (INT4 Quantized for Edge), MediaPipe Vision, Hugging Face PEFT (Backend LoRA Training).

## 📱 Project Architecture

KALA-X is split into two components that communicate via a **REST API**:
1. **Android App (`/app`)**: The user-facing client. It runs heavily on-device to preserve privacy and battery. Core ML models (Vision for background removal, Llama for text generation) execute natively on the phone's NPU.
2. **FastAPI Backend (`/backend`)**: Currently designed to be run **locally** (it is not hosted on the cloud yet). The Android app uses Retrofit to communicate with the backend to sync product data and offload heavy AI training. 

**The Personal Learning Loop (How they talk):** When an artisan edits an AI-generated price or description on their phone, the Android `WorkManager` syncs these corrections to the FastAPI backend. The backend simulates training a personalized 3MB LoRA (Low-Rank Adaptation) adapter and serves it back to the phone. From then on, the phone's offline AI generates listings perfectly matched to the artisan's unique style!

## 🚀 How to Run Locally

Because the backend is not hosted on the cloud, you must spin it up locally to test the full syncing experience.

### 1. Run the Backend
You need Python 3.10+ installed.
```bash
cd backend
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -r requirements.txt
python -m uvicorn app.main:app --host 0.0.0.0 --port 8000
```
The backend will now be running on `http://localhost:8000`.

### 2. Run the Android App
1. Open the repository root in **Android Studio** (Ladybug or newer).
2. Sync the Gradle project.
3. The app is pre-configured to point to `http://10.0.2.2:8000/` (the Android emulator's alias for localhost). If running on a physical device, update the Retrofit base URL in `KalaApi.kt` to your computer's local IP address.
4. Click **Run** (`Shift + F10`) to deploy to your emulator or device.

## 🛣 Future Roadmap

While KALA-X already features a working Hybrid AI Loop, our roadmap for the future includes:
* **Multi-Modal SLMs**: Moving beyond text to allow the on-device AI to analyze the actual aesthetic of the raw photo to suggest pricing tiers (e.g., identifying premium clay vs. standard clay).
* **Federated Learning Network**: Allowing artisans within the same region (e.g., a specific pottery village) to share an aggregated, anonymized LoRA adapter to collectively boost their pricing intelligence without sharing proprietary designs.
* **Direct E-Commerce Integration**: One-tap publishing directly to Shopify and Etsy APIs.

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
