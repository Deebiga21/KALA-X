from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, Boolean
from sqlalchemy.orm import relationship
from datetime import datetime
from app.core.database import Base

class Notification(Base):
    __tablename__ = "notifications"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"))

    type = Column(String, index=True) # product, pricing, market, system
    title = Column(String)
    message = Column(String)

    is_read = Column(Boolean, default=False)

    created_at = Column(DateTime, default=datetime.utcnow)

    user = relationship("User", back_populates="notifications")
