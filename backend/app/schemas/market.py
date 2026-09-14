from pydantic import BaseModel
from typing import Optional, List
from datetime import datetime

class MarketInsightBase(BaseModel):
    category: str
    product_type: str
    demand_level: str
    demand_percentage: int
    market_min: float
    market_max: float
    trend_percentage: int
    recommended_price: float
    period: str

class MarketInsight(MarketInsightBase):
    id: int
    created_at: datetime

    class Config:
        from_attributes = True

class ProductInsightResponse(BaseModel):
    product: str
    market_range: dict # {"min": x, "max": y}
    recommended_price: float
    demand: str
    trend: int
    estimated_margin: float
    recommendation: str

class GeneralInsightsResponse(BaseModel):
    trending: List[dict] # [{"category": "x", "trend": y}]
    opportunities: List[dict]
    recommendations: List[str]
