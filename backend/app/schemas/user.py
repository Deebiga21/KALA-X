from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class UserBase(BaseModel):
    name: str
    email: Optional[str] = None
    phone: Optional[str] = None
    location: Optional[str] = None
    preferred_language: Optional[str] = None
    craft_category: Optional[str] = None
    profile_image: Optional[str] = None

class UserCreate(UserBase):
    pass

class UserUpdate(BaseModel):
    name: Optional[str] = None
    location: Optional[str] = None
    preferred_language: Optional[str] = None
    craft_category: Optional[str] = None

class User(UserBase):
    id: int
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True
