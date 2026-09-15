from fastapi import APIRouter, Depends, UploadFile, File, HTTPException
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.models.product import Product
from app.schemas.product import SyncProductRequest
from typing import List
import shutil
import os

router = APIRouter()

@router.post("/products")
def sync_products(products: List[SyncProductRequest], db: Session = Depends(get_db)):
    """
    Sync offline generated products from the Edge device to the Cloud.
    Uses UPSERT logic based on offline_id.
    """
    synced_ids = []
    
    for item in products:
        # Check if product with this offline_id already exists
        db_product = db.query(Product).filter(Product.offline_id == item.offline_id).first()
        
        if db_product:
            # Update existing
            for key, value in item.dict().items():
                setattr(db_product, key, value)
        else:
            # Create new
            db_product = Product(**item.dict(), user_id=1) # Hardcoded user 1 for demo
            db.add(db_product)
            
        synced_ids.append(item.offline_id)
        
    db.commit()
    return {"success": True, "data": {"synced_offline_ids": synced_ids}}

@router.post("/images/{offline_id}")
async def sync_image(offline_id: str, image_type: str, file: UploadFile = File(...), db: Session = Depends(get_db)):
    """
    Sync an image (original or enhanced) for a product created offline.
    image_type should be 'original' or 'enhanced'.
    """
    product = db.query(Product).filter(Product.offline_id == offline_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found. Sync JSON data first.")
        
    folder = "enhanced" if image_type == "enhanced" else "products"
    file_path = f"uploads/{folder}/{offline_id}_{file.filename}"
    
    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)
        
    if image_type == "enhanced":
        product.enhanced_image = f"/{file_path}"
    else:
        product.original_image = f"/{file_path}"
        
    db.commit()
    return {"success": True, "data": {"url": f"/{file_path}"}}
