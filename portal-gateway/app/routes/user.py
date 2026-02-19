from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from datetime import datetime, timedelta
from fastapi.security import OAuth2PasswordRequestForm
from .. import models, schemas, auth
from ..database import SessionLocal

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.post("/signup", response_model=schemas.Token)
def signup(user: schemas.UserCreate, db: Session = Depends(get_db)):
    # 1. Validate Invite Code
    invite = db.query(models.InviteCode).filter(models.InviteCode.code == user.invite_code).first()
    if not invite:
        raise HTTPException(status_code=400, detail="Invalid invite code")
    if invite.is_used:
        raise HTTPException(status_code=400, detail="Invite code already used")
    if invite.expires_at < datetime.utcnow():
        raise HTTPException(status_code=400, detail="Invite code expired")

    # 2. Check if user exists
    db_user = db.query(models.User).filter(models.User.username == user.username).first()
    if db_user:
        raise HTTPException(status_code=400, detail="Username already registered")

    # 3. Create User
    hashed_password = auth.get_password_hash(user.password)
    new_user = models.User(
        username=user.username,
        email=user.email,
        password_hash=hashed_password,
        role=invite.role_to_assign,
        invited_by_code=invite.code,
        access_group_id=invite.target_group_id
    )
    
    # 4. Mark Invite as Used
    invite.is_used = True
    
    db.add(new_user)
    db.add(invite)
    db.commit()
    db.refresh(new_user)

    # 5. Generate Token
    access_token = auth.create_access_token(
        data={"sub": new_user.username, "role": new_user.role}
    )
    return {"access_token": access_token, "token_type": "bearer"}

@router.post("/login", response_model=schemas.Token)
def login(form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    user = db.query(models.User).filter(models.User.username == form_data.username).first()
    if not user or not auth.verify_password(form_data.password, user.password_hash):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    access_token = auth.create_access_token(
        data={"sub": user.username, "role": user.role}
    )
    return {"access_token": access_token, "token_type": "bearer"}
