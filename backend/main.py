from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from routers import products
import models
from database import engine

# Create database tables
models.Base.metadata.create_all(bind=engine)

app = FastAPI(title="KALA-X API")

# Configure CORS for frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], # In production, replace with frontend URL
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(products.router, prefix="/api")

@app.get("/")
def read_root():
    return {"message": "Welcome to KALA-X API"}
