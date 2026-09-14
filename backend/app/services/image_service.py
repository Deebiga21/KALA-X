import time
from sqlalchemy.orm import Session
from app.models.processing import ProductProcessing
from app.models.product import Product
from datetime import datetime
import asyncio

class MockImageService:
    async def process_image(self, db: Session, product_id: int, image_url: str):
        # Create processing records
        analysis = ProductProcessing(product_id=product_id, step="image_analysis", status="processing")
        db.add(analysis)
        db.commit()

        # Simulate steps
        await asyncio.sleep(1)
        analysis.status = "completed"
        analysis.completed_at = datetime.utcnow()
        db.commit()

        enhancement = ProductProcessing(product_id=product_id, step="image_enhancement", status="processing")
        db.add(enhancement)
        db.commit()

        await asyncio.sleep(1)
        enhancement.status = "completed"
        enhancement.completed_at = datetime.utcnow()
        
        # Update product with mock enhanced image (for now we can just copy original or append mock)
        product = db.query(Product).filter(Product.id == product_id).first()
        if product:
            product.enhanced_image = image_url # Mock: just use original
        
        db.commit()
