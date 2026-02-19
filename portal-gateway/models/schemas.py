from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime

class Token(BaseModel):
    access_token: str
    token_type: str

class TokenData(BaseModel):
    username: Optional[str] = None
    permissions: List[str] = []

class UserBase(BaseModel):
    username: str
    email: str

class UserCreate(UserBase):
    password: str
    invite_code: str

class User(UserBase):
    id: int
    is_active: bool
    access_groups: List[str] = []

    class Config:
        orm_mode = True

class InviteCreate(BaseModel):
    group_name: str
    org_name: str
    days_valid: int = 7
