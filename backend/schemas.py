from pydantic import BaseModel
from typing import Optional, List

class ProductBase(BaseModel):
    name: str
    description: Optional[str] = None

class ProductCreate(ProductBase):
    pass

class ProductResponse(ProductBase):
    id: int
    image_url: Optional[str] = None
    audio_url: Optional[str] = None
    material: Optional[str] = None
    raw_material_cost: Optional[float] = None
    labor_cost: Optional[float] = None
    packaging_cost: Optional[float] = None
    margin_percentage: Optional[float] = None
    final_price: Optional[float] = None
    catalog_title: Optional[str] = None
    catalog_description: Optional[str] = None
    catalog_seo_tags: Optional[str] = None
    readiness_score: Optional[float] = None
    is_published: bool

    class Config:
        from_attributes = True
        
class PricingInput(BaseModel):
    raw_material_cost: float
    labor_cost: float
    packaging_cost: float
    margin_percentage: float
