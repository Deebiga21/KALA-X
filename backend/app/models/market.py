from sqlalchemy import Column, Integer, String, Float, DateTime
from datetime import datetime
from app.core.database import Base

class MarketInsight(Base):
    __tablename__ = "market_insights"

    id = Column(Integer, primary_key=True, index=True)
    category = Column(String, index=True)
    product_type = Column(String, index=True)

    demand_level = Column(String) # High, Medium, Low
    demand_percentage = Column(Integer)

    market_min = Column(Float)
    market_max = Column(Float)

    trend_percentage = Column(Integer)
    recommended_price = Column(Float)

    period = Column(String) # e.g. "Q3 2026"

    created_at = Column(DateTime, default=datetime.utcnow)
