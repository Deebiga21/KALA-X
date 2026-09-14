from fastapi import APIRouter, Depends, UploadFile, File, BackgroundTasks, HTTPException
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.models.product import Product
from app.schemas.product import ProductCreate, ProductUpdate, ProductPriceUpdate, Product as ProductSchema, PublishResponse
from app.services.image_service import MockImageService
from app.services.voice_service import MockVoiceService
from app.services.catalog_service import MockCatalogService
from app.services.pricing_service import PricingService
from app.services.commerce_score_service import CommerceScoreService
from app.models.notification import Notification
import shutil
import os
from datetime import datetime

router = APIRouter()

image_service = MockImageService()
voice_service = MockVoiceService()
catalog_service = MockCatalogService()
pricing_service = PricingService()
score_service = CommerceScoreService()

@router.get("/")
def get_products(db: Session = Depends(get_db)):
    products = db.query(Product).all()
    return {"success": True, "data": products}

@router.post("/")
def create_product(product: ProductCreate, db: Session = Depends(get_db)):
    db_product = Product(**product.dict(), user_id=1) # Hardcoded user 1
    db.add(db_product)
    db.commit()
    db.refresh(db_product)
    return {"success": True, "data": {"id": db_product.id, "status": db_product.status}}

@router.get("/{product_id}")
def get_product(product_id: int, db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    return {"success": True, "data": product}

@router.put("/{product_id}")
def update_product(product_id: int, update_data: ProductUpdate, db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    
    for key, value in update_data.dict(exclude_unset=True).items():
        setattr(product, key, value)
        
    score = score_service.calculate_score(product)
    product.commerce_score = score["score"]
    
    db.commit()
    db.refresh(product)
    return {"success": True, "data": product}

@router.post("/{product_id}/image")
async def upload_image(product_id: int, file: UploadFile = File(...), db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404)
        
    file_path = f"uploads/products/{product_id}_{file.filename}"
    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)
        
    product.original_image = f"/{file_path}"
    
    score = score_service.calculate_score(product)
    product.commerce_score = score["score"]
    
    db.commit()
    return {"success": True, "data": {"url": product.original_image}}

@router.post("/{product_id}/enhance-image")
async def enhance_image(product_id: int, background_tasks: BackgroundTasks, db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404)
        
    background_tasks.add_task(image_service.process_image, db, product_id, product.original_image)
    return {"success": True, "data": {"message": "Enhancement started"}}

@router.post("/{product_id}/voice")
async def process_voice(product_id: int, language: str, file: UploadFile = File(None), background_tasks: BackgroundTasks = BackgroundTasks(), db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404)
    background_tasks.add_task(voice_service.process_audio, db, product_id, language)
    return {"success": True, "data": {"message": "Voice processing started"}}

@router.post("/{product_id}/generate-catalog")
async def generate_catalog(product_id: int, background_tasks: BackgroundTasks, db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404)
    background_tasks.add_task(catalog_service.generate_catalog, db, product_id)
    return {"success": True, "data": {"message": "Catalog generation started"}}

@router.post("/{product_id}/calculate-price")
def calculate_price(product_id: int, db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404)
        
    res = pricing_service.calculate_price(product)
    product.total_cost = res["total_cost"]
    product.market_min = res["market_min"]
    product.market_max = res["market_max"]
    product.recommended_price = res["recommended_price"]
    product.pricing_confidence = res["pricing_confidence"]
    
    score = score_service.calculate_score(product)
    product.commerce_score = score["score"]
    
    db.commit()
    return {"success": True, "data": res}

@router.put("/{product_id}/price")
def update_price(product_id: int, price_data: ProductPriceUpdate, db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id).first()
    product.recommended_price = price_data.price
    score = score_service.calculate_score(product)
    product.commerce_score = score["score"]
    db.commit()
    return {"success": True, "data": product}

@router.post("/{product_id}/commerce-score")
def get_commerce_score(product_id: int, db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id).first()
    res = score_service.calculate_score(product)
    return {"success": True, "data": res}

@router.post("/{product_id}/publish")
def publish_product(product_id: int, db: Session = Depends(get_db)):
    product = db.query(Product).filter(Product.id == product_id).first()
    res = score_service.calculate_score(product)
    
    if len(res["missing_fields"]) > 0 or res["score"] < 50:
        return {"success": True, "data": {"can_publish": False, "missing_fields": res["missing_fields"]}}
        
    product.status = "published"
    product.published_at = datetime.utcnow()
    
    notif = Notification(user_id=product.user_id, type="product", title="Product Published", message=f"Your {product.name} has been published.")
    db.add(notif)
    db.commit()
    return {"success": True, "data": {"can_publish": True, "status": "published"}}
