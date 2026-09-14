from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime

class ProductBase(BaseModel):
    name: str
    category: str
    material: str
    image: str
    enhancedImage: Optional[str] = None
    description: str
    originalLanguage: Optional[str] = None
    transcription: Optional[str] = None
    translation: Optional[str] = None
    keywords: List[str] = []
    rawMaterialCost: Optional[float] = None
    labourCost: Optional[float] = None
    packagingCost: Optional[float] = None
    otherCost: Optional[float] = None
    totalCost: Optional[float] = None
    marketMin: Optional[float] = None
    marketMax: Optional[float] = None
    recommendedPrice: Optional[float] = None
    pricingConfidence: Optional[int] = None
    commerceScore: Optional[int] = None
    dimensions: Optional[str] = None
    status: str = "Draft"

class ProductCreate(ProductBase):
    id: str

class ProductResponse(ProductBase):
    id: str
    createdAt: datetime

    class Config:
        from_attributes = True
