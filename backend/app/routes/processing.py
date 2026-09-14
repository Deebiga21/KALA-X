from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.models.processing import ProductProcessing

router = APIRouter()

@router.get("/{product_id}/processing")
def get_processing_status(product_id: int, db: Session = Depends(get_db)):
    records = db.query(ProductProcessing).filter(ProductProcessing.product_id == product_id).all()
    steps = [{"step": r.step, "status": r.status, "message": r.message} for r in records]
    
    return {
        "success": True,
        "data": {
            "product_id": product_id,
            "steps": steps
        }
    }
