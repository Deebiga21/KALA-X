from app.core.database import SessionLocal, engine, Base
from app.models.user import User
from app.models.product import Product
from app.models.market import MarketInsight
from app.models.notification import Notification
from app.models import __init__

def seed_db():
    Base.metadata.create_all(bind=engine)
    db = SessionLocal()

    # User
    user = db.query(User).first()
    if not user:
        user = User(
            name="Lakshmi",
            location="Tamil Nadu, India",
            preferred_language="Tamil",
            craft_category="Handicrafts"
        )
        db.add(user)
        db.commit()
        db.refresh(user)
    
    # Products
    if db.query(Product).count() == 0:
        p1 = Product(user_id=user.id, name="Bamboo Basket", category="Home & Lifestyle", material="Natural Bamboo", status="published", commerce_score=91, recommended_price=849, dimensions="10x10x15")
        p2 = Product(user_id=user.id, name="Terracotta Lamp", category="Decor", material="Clay", status="draft", commerce_score=60)
        p3 = Product(user_id=user.id, name="Handwoven Bag", category="Fashion", material="Cotton", status="published", commerce_score=88, recommended_price=499)
        db.add_all([p1, p2, p3])
        db.commit()

    # Market Insights
    if db.query(MarketInsight).count() == 0:
        m1 = MarketInsight(category="Bamboo Crafts", product_type="Basket", demand_level="High", demand_percentage=85, market_min=700, market_max=950, trend_percentage=18, recommended_price=849, period="Q3 2026")
        db.add(m1)
        db.commit()

    # Notifications
    if db.query(Notification).count() == 0:
        n1 = Notification(user_id=user.id, type="system", title="Welcome to KALA-X", message="Your artisan account is ready.", is_read=False)
        db.add(n1)
        db.commit()

    db.close()
    print("Database seeded successfully!")

if __name__ == "__main__":
    seed_db()
