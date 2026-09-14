from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List
import models, schemas
from database import get_db

router = APIRouter(
    prefix="/products",
    tags=["products"],
)

@router.get("/", response_model=List[schemas.ProductResponse])
def get_products(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
    products = db.query(models.Product).offset(skip).limit(limit).all()
    # Convert keywords string back to list for response
    for p in products:
        if p.keywords:
            p.keywords = p.keywords.split(",")
        else:
            p.keywords = []
    return products

@router.post("/", response_model=schemas.ProductResponse)
def create_product(product: schemas.ProductCreate, db: Session = Depends(get_db)):
    db_product = models.Product(**product.model_dump(exclude={"keywords"}))
    db_product.keywords = ",".join(product.keywords)
    db.add(db_product)
    db.commit()
    db.refresh(db_product)
    
    # Format for response
    if db_product.keywords:
        db_product.keywords = db_product.keywords.split(",")
    else:
        db_product.keywords = []
        
    return db_product

# Added mock AI endpoints for completeness if frontend wants to switch to backend logic later

@router.post("/enhance-image")
def enhance_image(image_url: str):
    return {"enhancedImage": "https://images.unsplash.com/photo-1606760227091-3dd870d97f1d?q=80&w=800&auto=format&fit=crop"}

@router.post("/calculate-price")
def calculate_price(raw: float, labour: float, packaging: float, other: float):
    total = raw + labour + packaging + other
    rec = round(total * 1.35)
    return {
        "totalCost": total,
        "recommendedPrice": rec,
        "marketMin": rec - 50,
        "marketMax": rec + 150,
        "confidence": 87
    }
