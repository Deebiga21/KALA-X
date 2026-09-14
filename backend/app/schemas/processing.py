from pydantic import BaseModel
from typing import Optional, List
from datetime import datetime

class ProductProcessingBase(BaseModel):
    step: str
    status: str
    message: Optional[str] = None

class ProductProcessing(ProductProcessingBase):
    id: int
    product_id: int
    started_at: datetime
    completed_at: Optional[datetime] = None

    class Config:
        from_attributes = True

class ProcessingStatusResponse(BaseModel):
    product_id: int
    steps: List[ProductProcessingBase]
