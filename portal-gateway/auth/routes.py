from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from datetime import datetime, timedelta
from ..models import database, models, schemas
from . import security
from fastapi.security import OAuth2PasswordRequestForm

router = APIRouter()

def get_db():
    db = database.SessionLocal()
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
    hashed_password = security.get_password_hash(user.password)
    new_user = models.User(
        username=user.username,
        email=user.email,
        hashed_password=hashed_password,
        is_active=True
    )
    
    # 4. Assign Access Group from Invite
    new_user.access_groups.append(invite.access_group)
    
    # 5. Mark Invite as Used
    invite.is_used = True
    
    db.add(new_user)
    db.add(invite)
    db.commit()
    db.refresh(new_user)

    # 6. Generate Token
    access_token_expires = timedelta(minutes=security.ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = security.create_access_token(
        data={"sub": new_user.username, "scopes": security.create_access_mask(new_user.access_groups)},
        expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}

@router.post("/token", response_model=schemas.Token)
def login_for_access_token(form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    user = db.query(models.User).filter(models.User.username == form_data.username).first()
    if not user or not security.verify_password(form_data.password, user.hashed_password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    access_token_expires = timedelta(minutes=security.ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = security.create_access_token(
        data={"sub": user.username, "scopes": security.create_access_mask(user.access_groups)},
        expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}
