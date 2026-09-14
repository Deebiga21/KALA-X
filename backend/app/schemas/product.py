from pydantic import BaseModel
from typing import Optional, List
from datetime import datetime

class ProductBase(BaseModel):
    name: str
    category: str
    material: str

class ProductCreate(ProductBase):
    location: Optional[str] = None
    language: Optional[str] = None

class ProductUpdate(BaseModel):
    name: Optional[str] = None
    category: Optional[str] = None
    material: Optional[str] = None
    description: Optional[str] = None
    keywords: Optional[str] = None # Expecting comma separated for now
    dimensions: Optional[str] = None
    weight: Optional[str] = None

class ProductPriceUpdate(BaseModel):
    price: float

class Product(ProductBase):
    id: int
    user_id: int
    original_image: Optional[str] = None
    enhanced_image: Optional[str] = None
    original_language: Optional[str] = None
    transcription: Optional[str] = None
    translation: Optional[str] = None
    description: Optional[str] = None
    seo_title: Optional[str] = None
    keywords: Optional[str] = None
    raw_material_cost: float
    labour_cost: float
    packaging_cost: float
    other_cost: float
    total_cost: float
    market_min: Optional[float] = None
    market_max: Optional[float] = None
    recommended_price: Optional[float] = None
    pricing_confidence: Optional[int] = None
    commerce_score: int
    dimensions: Optional[str] = None
    weight: Optional[str] = None
    status: str
    created_at: datetime
    updated_at: datetime
    published_at: Optional[datetime] = None

    class Config:
        from_attributes = True

class PriceCalculationRequest(BaseModel):
    raw_material_cost: float
    labour_cost: float
    packaging_cost: float
    other_cost: float

class CommerceScoreResponse(BaseModel):
    score: int
    status: str
    breakdown: dict
    missing_fields: List[str]

class PublishResponse(BaseModel):
    can_publish: bool
    missing_fields: List[str]
