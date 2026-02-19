from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from .. import schemas, auth
from ..database import SessionLocal
from ..oem_logic import generate_secure_invite

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
    result = generate_secure_invite(
        db=db,
        role=invite.role_to_assign,
        group_id=invite.target_group_id,
        days_valid=invite.days_valid
    )
    return result["invite_code"]
