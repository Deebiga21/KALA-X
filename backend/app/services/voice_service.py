import asyncio
from sqlalchemy.orm import Session
from app.models.product import Product
from app.models.processing import ProductProcessing
from datetime import datetime

class MockVoiceService:
    async def process_audio(self, db: Session, product_id: int, language: str):
        processing = ProductProcessing(product_id=product_id, step="speech_transcription", status="processing")
        db.add(processing)
        db.commit()

        await asyncio.sleep(1)
        
        product = db.query(Product).filter(Product.id == product_id).first()
        if product:
            product.original_language = language
            # Mock transcription
            if language.lower() == "tamil":
                product.transcription = "இந்த கூடை இயற்கையான மூங்கிலால் கையால் செய்யப்பட்டது"
            else:
                product.transcription = f"Handmade {product.name} crafted with care."
            
            processing.status = "completed"
            processing.completed_at = datetime.utcnow()
            db.commit()

            translation_proc = ProductProcessing(product_id=product_id, step="translation", status="processing")
            db.add(translation_proc)
            db.commit()
            
            await asyncio.sleep(1)
            
            product.translation = "This basket is handmade using natural bamboo."
            translation_proc.status = "completed"
            translation_proc.completed_at = datetime.utcnow()
            db.commit()
