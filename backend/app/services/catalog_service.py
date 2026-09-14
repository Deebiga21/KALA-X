import asyncio
from sqlalchemy.orm import Session
from app.models.product import Product
from app.models.processing import ProductProcessing
from datetime import datetime

class MockCatalogService:
    async def generate_catalog(self, db: Session, product_id: int):
        processing = ProductProcessing(product_id=product_id, step="catalog_generation", status="processing")
        db.add(processing)
        db.commit()

        await asyncio.sleep(2)
        
        product = db.query(Product).filter(Product.id == product_id).first()
        if product:
            product.description = product.translation if product.translation else f"Beautiful handmade {product.name}."
            product.seo_title = f"Handmade Natural {product.name}"
            product.keywords = f"{product.name.lower()}, handmade, eco friendly, indian handicraft"
            
            processing.status = "completed"
            processing.completed_at = datetime.utcnow()
            db.commit()
