from sqlalchemy import Column, Integer, String, Float, DateTime
from database import Base
import datetime

class Product(Base):
    __tablename__ = "products"

    id = Column(String, primary_key=True, index=True)
    name = Column(String, index=True)
    category = Column(String)
    material = Column(String)
    image = Column(String)
    enhancedImage = Column(String, nullable=True)
    description = Column(String)
    originalLanguage = Column(String, nullable=True)
    transcription = Column(String, nullable=True)
    translation = Column(String, nullable=True)
    keywords = Column(String) # Stored as comma-separated string
    rawMaterialCost = Column(Float, nullable=True)
    labourCost = Column(Float, nullable=True)
    packagingCost = Column(Float, nullable=True)
    otherCost = Column(Float, nullable=True)
    totalCost = Column(Float, nullable=True)
    marketMin = Column(Float, nullable=True)
    marketMax = Column(Float, nullable=True)
    recommendedPrice = Column(Float, nullable=True)
    pricingConfidence = Column(Integer, nullable=True)
    commerceScore = Column(Integer, nullable=True)
    dimensions = Column(String, nullable=True)
    status = Column(String, default="Draft")
    createdAt = Column(DateTime, default=datetime.datetime.utcnow)
