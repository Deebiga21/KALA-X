from sqlalchemy import Column, Integer, String, Float, DateTime, ForeignKey, Text
from sqlalchemy.orm import relationship
from datetime import datetime
from app.core.database import Base

class Product(Base):
    __tablename__ = "products"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"))

    name = Column(String, index=True)
    category = Column(String, index=True)
    material = Column(String)

    original_image = Column(String, nullable=True)
    enhanced_image = Column(String, nullable=True)

    original_language = Column(String, nullable=True)
    transcription = Column(Text, nullable=True)
    translation = Column(Text, nullable=True)

    description = Column(Text, nullable=True)
    seo_title = Column(String, nullable=True)
    keywords = Column(Text, nullable=True) # stored as comma separated string

    raw_material_cost = Column(Float, default=0.0)
    labour_cost = Column(Float, default=0.0)
    packaging_cost = Column(Float, default=0.0)
    other_cost = Column(Float, default=0.0)
    total_cost = Column(Float, default=0.0)

    market_min = Column(Float, nullable=True)
    market_max = Column(Float, nullable=True)
    recommended_price = Column(Float, nullable=True)
    pricing_confidence = Column(Integer, nullable=True)

    commerce_score = Column(Integer, default=0)

    dimensions = Column(String, nullable=True)
    weight = Column(String, nullable=True)

    status = Column(String, default="draft") # draft, published

    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    published_at = Column(DateTime, nullable=True)

    owner = relationship("User", back_populates="products")
    processing_records = relationship("ProductProcessing", back_populates="product", cascade="all, delete-orphan")
