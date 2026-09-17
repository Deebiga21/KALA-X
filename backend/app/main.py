from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
import os

from app.core.database import engine, Base
from app.models import __init__ # to register all models
from app.routes import home, products, processing, insights, profile, notifications, sync, lora_training

# Create database tables
Base.metadata.create_all(bind=engine)

app = FastAPI(title="KALA-X API")

# Configure CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Ensure uploads directory exists
os.makedirs("uploads/products", exist_ok=True)
os.makedirs("uploads/enhanced", exist_ok=True)

# Mount static files for images
app.mount("/uploads", StaticFiles(directory="uploads"), name="uploads")

app.include_router(sync.router, prefix="/api/sync", tags=["Sync"])
app.include_router(home.router, prefix="/api/home", tags=["Home"])
app.include_router(products.router, prefix="/api/products", tags=["Products"])
app.include_router(processing.router, prefix="/api/products", tags=["Processing"])
app.include_router(insights.router, prefix="/api/insights", tags=["Insights"])
app.include_router(profile.router, prefix="/api/profile", tags=["Profile"])
app.include_router(notifications.router, prefix="/api/notifications", tags=["Notifications"])
app.include_router(lora_training.router, prefix="/api/lora-training", tags=["LoRA Training"])

@app.get("/")
def root():
    return {"message": "KALA-X API is running"}
