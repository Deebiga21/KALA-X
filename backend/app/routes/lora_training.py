from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List
from app.services.lora_trainer_service import LoraTrainerService
from fastapi.responses import FileResponse
import os

router = APIRouter(prefix="/api/sync", tags=["Learning Loop"])

class CorrectionPayload(BaseModel):
    catalogItemId: int
    originalText: str
    correctedText: str
    originalPrice: float
    correctedPrice: float
    correctionType: str

trainer = LoraTrainerService()

@router.post("/corrections")
async def sync_corrections(corrections: List[CorrectionPayload]):
    """Receive artisan corrections and trigger LoRA adapter training."""
    trainer.ingest_corrections(
        artisan_id="default_artisan",
        corrections=[c.dict() for c in corrections]
    )
    trainer.train_adapter(artisan_id="default_artisan")
    return {"success": True, "data": {"corrections_received": len(corrections)}}

@router.get("/adapter/{artisan_id}")
async def get_adapter(artisan_id: str):
    """Download the latest LoRA adapter for an artisan."""
    adapter_path = trainer.get_adapter_path(artisan_id)
    if adapter_path and os.path.exists(adapter_path):
        return FileResponse(adapter_path, media_type="application/octet-stream", filename="adapter_latest.bin")
    raise HTTPException(status_code=404, detail="No adapter available yet")
