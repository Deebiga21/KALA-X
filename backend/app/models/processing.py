from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, Text
from sqlalchemy.orm import relationship
from datetime import datetime
from app.core.database import Base

class ProductProcessing(Base):
    __tablename__ = "product_processing"

    id = Column(Integer, primary_key=True, index=True)
    product_id = Column(Integer, ForeignKey("products.id"))

    step = Column(String, index=True)
    status = Column(String, default="pending") # pending, processing, completed, failed
    
    message = Column(Text, nullable=True)

    started_at = Column(DateTime, default=datetime.utcnow)
    completed_at = Column(DateTime, nullable=True)

    product = relationship("Product", back_populates="processing_records")
