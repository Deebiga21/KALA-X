from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.models.user import User
from app.models.product import Product
from app.schemas.user import UserUpdate

router = APIRouter()

@router.get("/")
def get_profile(db: Session = Depends(get_db)):
    user = db.query(User).first()
    products_count = db.query(Product).filter(Product.user_id == user.id).count()
    published_count = db.query(Product).filter(Product.user_id == user.id, Product.status == "published").count()
    
    return {
        "success": True,
        "data": {
            "name": user.name,
            "location": user.location,
            "language": user.preferred_language,
            "craft": user.craft_category,
            "profile_image": user.profile_image,
            "products_count": products_count,
            "published_count": published_count,
            "sales": 24600,
            "commerce_score": 91,
            "completion": 100 if user.profile_image else 80
        }
    }

@router.put("/")
def update_profile(data: UserUpdate, db: Session = Depends(get_db)):
    user = db.query(User).first()
    for key, value in data.dict(exclude_unset=True).items():
        setattr(user, key, value)
    db.commit()
    return {"success": True, "data": user}
