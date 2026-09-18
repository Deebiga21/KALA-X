import os
import shutil
from fastapi import FastAPI, Depends, HTTPException, UploadFile, File
from sqlalchemy.orm import Session
from typing import List

from database import engine, Base, get_db
import models
import schemas
from services import (
    vision_service, speech_service, language_service, 
    catalog_service, pricing_service, readiness_service
)

# Create database tables
Base.metadata.create_all(bind=engine)

app = FastAPI(title="KALA-X Backend")

UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)

@app.post("/api/products", response_model=schemas.ProductResponse)
def create_product(product: schemas.ProductCreate, db: Session = Depends(get_db)):
    db_product = models.Product(**product.model_dump())
    db.add(db_product)
    db.commit()
    db.refresh(db_product)
    return db_product

@app.get("/api/products", response_model=List[schemas.ProductResponse])
def get_products(db: Session = Depends(get_db)):
    return db.query(models.Product).all()

@app.get("/api/products/{id}", response_model=schemas.ProductResponse)
def get_product(id: int, db: Session = Depends(get_db)):
    product = db.query(models.Product).filter(models.Product.id == id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    return product

@app.post("/api/products/{id}/image", response_model=schemas.ProductResponse)
def upload_image(id: int, file: UploadFile = File(...), db: Session = Depends(get_db)):
    product = db.query(models.Product).filter(models.Product.id == id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
        
    file_path = os.path.join(UPLOAD_DIR, f"{id}_{file.filename}")
    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)
        
    product.image_url = file_path
    
    # Process image
    desc = product.description or ""
    vision_res = vision_service.process_image(file_path, desc)
    if vision_res.get("material"):
        product.material = vision_res["material"]
        
    db.commit()
    db.refresh(product)
    return product

@app.post("/api/products/{id}/voice", response_model=schemas.ProductResponse)
def upload_voice(id: int, file: UploadFile = File(...), db: Session = Depends(get_db)):
    product = db.query(models.Product).filter(models.Product.id == id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
        
    file_path = os.path.join(UPLOAD_DIR, f"{id}_{file.filename}")
    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)
        
    product.audio_url = file_path
    
    # Process voice
    transcription = speech_service.process_audio(file_path)
    if not product.description:
        product.description = transcription
    else:
        product.description += " " + transcription
        
    # Extract language info
    lang_info = language_service.extract_information(transcription)
    if lang_info.get("material"):
        product.material = lang_info["material"]
    if lang_info.get("raw_material_cost"):
        product.raw_material_cost = lang_info["raw_material_cost"]
        
    db.commit()
    db.refresh(product)
    return product

@app.post("/api/products/{id}/generate-catalog", response_model=schemas.ProductResponse)
def generate_catalog_endpoint(id: int, db: Session = Depends(get_db)):
    product = db.query(models.Product).filter(models.Product.id == id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
        
    cat_res = catalog_service.generate_catalog(
        product.name, 
        product.description or "", 
        product.material or ""
    )
    product.catalog_title = cat_res["catalog_title"]
    product.catalog_description = cat_res["catalog_description"]
    product.catalog_seo_tags = cat_res["catalog_seo_tags"]
    
    db.commit()
    db.refresh(product)
    return product

@app.post("/api/products/{id}/pricing", response_model=schemas.ProductResponse)
def set_pricing(id: int, pricing: schemas.PricingInput, db: Session = Depends(get_db)):
    product = db.query(models.Product).filter(models.Product.id == id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
        
    product.raw_material_cost = pricing.raw_material_cost
    product.labor_cost = pricing.labor_cost
    product.packaging_cost = pricing.packaging_cost
    product.margin_percentage = pricing.margin_percentage
    
    product.final_price = pricing_service.calculate_price(
        pricing.raw_material_cost,
        pricing.labor_cost,
        pricing.packaging_cost,
        pricing.margin_percentage
    )
    
    db.commit()
    db.refresh(product)
    return product

@app.post("/api/products/{id}/readiness", response_model=schemas.ProductResponse)
def update_readiness(id: int, db: Session = Depends(get_db)):
    product = db.query(models.Product).filter(models.Product.id == id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
        
    product.readiness_score = readiness_service.calculate_readiness(product)
    db.commit()
    db.refresh(product)
    return product

@app.post("/api/products/{id}/publish", response_model=schemas.ProductResponse)
def publish_product(id: int, db: Session = Depends(get_db)):
    product = db.query(models.Product).filter(models.Product.id == id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
        
    # Maybe update readiness first
    product.readiness_score = readiness_service.calculate_readiness(product)
    if product.readiness_score < 100.0:
        raise HTTPException(status_code=400, detail="Product is not ready to publish (score < 100)")
        
    product.is_published = True
    db.commit()
    db.refresh(product)
    return product
