from sqlalchemy import Column, Integer, String, Float, Text, Boolean
from database import Base

class Product(Base):
    __tablename__ = "products"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, index=True)
    description = Column(Text, nullable=True)
    image_url = Column(String, nullable=True)
    audio_url = Column(String, nullable=True)
    
    material = Column(String, nullable=True)
    
    raw_material_cost = Column(Float, nullable=True)
    labor_cost = Column(Float, nullable=True)
    packaging_cost = Column(Float, nullable=True)
    margin_percentage = Column(Float, nullable=True)
    final_price = Column(Float, nullable=True)
    
    catalog_title = Column(String, nullable=True)
    catalog_description = Column(Text, nullable=True)
    catalog_seo_tags = Column(String, nullable=True)
    
    readiness_score = Column(Float, default=0.0)
    is_published = Column(Boolean, default=False)
