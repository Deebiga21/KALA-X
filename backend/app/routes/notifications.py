from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.models.notification import Notification

router = APIRouter()

@router.get("/")
def get_notifications(unread: bool = False, db: Session = Depends(get_db)):
    q = db.query(Notification)
    if unread:
        q = q.filter(Notification.is_read == False)
    notifications = q.order_by(Notification.created_at.desc()).all()
    return {"success": True, "data": notifications}

@router.get("/unread-count")
def get_unread_count(db: Session = Depends(get_db)):
    count = db.query(Notification).filter(Notification.is_read == False).count()
    return {"success": True, "data": {"count": count}}

@router.put("/{notif_id}/read")
def read_notification(notif_id: int, db: Session = Depends(get_db)):
    notif = db.query(Notification).filter(Notification.id == notif_id).first()
    notif.is_read = True
    db.commit()
    return {"success": True}

@router.post("/read-all")
def read_all(db: Session = Depends(get_db)):
    db.query(Notification).filter(Notification.is_read == False).update({Notification.is_read: True})
    db.commit()
    return {"success": True}
