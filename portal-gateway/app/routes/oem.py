from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from datetime import datetime, timedelta
from .. import models, schemas, auth
from ..database import SessionLocal

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# OEM: Generate Invite Code
@router.post("/generate-invite", response_model=str)
def generate_invite(invite: schemas.InviteCodeCreate, db: Session = Depends(get_db)):
    # In real app, check if current user is OEM
    expires = datetime.utcnow() + timedelta(days=invite.days_valid)
    new_invite = models.InviteCode(
        role_to_assign=invite.role_to_assign,
        target_group_id=invite.target_group_id,
        expires_at=expires
    )
    db.add(new_invite)
    db.commit()
    db.refresh(new_invite)
    return new_invite.code
