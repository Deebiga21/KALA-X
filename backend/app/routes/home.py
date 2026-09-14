from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from sqlalchemy import func
from app.core.database import get_db
from app.models.user import User
from app.models.product import Product
from app.models.processing import ProductProcessing

router = APIRouter()

@router.get("/")
def get_home_data(db: Session = Depends(get_db)):
    # Mock user 1 for prototype
    user = db.query(User).first()
    if not user:
        return {"success": False, "error": {"code": "NO_USER", "message": "No user found"}}

    products_created = db.query(Product).filter(Product.user_id == user.id).count()
    published = db.query(Product).filter(Product.user_id == user.id, Product.status == "published").count()
    drafts = products_created - published
    
    total_sales = 24600 # Static for now, could be derived from an orders table
    
    # Calculate avg commerce score for published products
    avg_score = db.query(func.avg(Product.commerce_score)).filter(Product.user_id == user.id, Product.status == "published").scalar() or 0

    recent_products = db.query(Product).filter(Product.user_id == user.id).order_by(Product.created_at.desc()).limit(3).all()
    
    # Derive recent activities from processing and products
    activities = []
    for p in recent_products:
        activities.append(f"{p.name} created")
        if p.status == "published":
            activities.append(f"{p.name} was published")
            
    recent_processing = db.query(ProductProcessing).order_by(ProductProcessing.started_at.desc()).limit(3).all()
    for proc in recent_processing:
        if proc.status == "completed":
            activities.append(f"AI {proc.step} completed")

    return {
        "success": True,
        "data": {
            "user": {
                "id": user.id,
                "name": user.name
            },
            "stats": {
                "products_created": products_created,
                "published": published,
                "drafts": drafts,
                "total_sales": total_sales,
                "commerce_score": int(avg_score)
            },
            "recent_products": [
                {"id": p.id, "name": p.name, "image": p.original_image or p.enhanced_image} 
                for p in recent_products
            ],
            "recent_activities": activities[:5]
        }
    }
