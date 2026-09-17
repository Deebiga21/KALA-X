from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
import os
from dotenv import load_dotenv

load_dotenv()

# Use SQLite for the prototype
DATABASE_URL = os.getenv("DATABASE_URL", "sqlite:///./database/kala_x.db")

# Ensure the database directory exists
db_dir = os.path.join(os.path.dirname(__file__), "..", "..", "database")
os.makedirs(db_dir, exist_ok=True)

# connect_args={"check_same_thread": False} is needed for SQLite in FastAPI
engine = create_engine(
    DATABASE_URL, connect_args={"check_same_thread": False}
)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
