from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.models.market import MarketInsight

router = APIRouter()

@router.get("/")
def get_insights(db: Session = Depends(get_db)):
    insights = db.query(MarketInsight).all()
    
    trending = [{"category": i.category, "trend": i.trend_percentage} for i in insights]
    
    return {
        "success": True,
        "data": {
            "trending": trending,
            "opportunities": [],
            "recommendations": ["Demand is currently high for Bamboo Crafts."]
        }
    }

@router.get("/{product_id}")
def get_product_insight(product_id: int, db: Session = Depends(get_db)):
    return {
        "success": True,
        "data": {
            "product": "Bamboo Basket",
            "market_range": {"min": 799, "max": 899},
            "recommended_price": 849,
            "demand": "High",
            "trend": 18,
            "estimated_margin": 249,
            "recommendation": "Demand is currently high. Consider pricing around ₹849."
        }
    }
