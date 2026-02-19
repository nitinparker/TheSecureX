from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime

class Token(BaseModel):
    access_token: str
    token_type: str

class UserBase(BaseModel):
    username: str
    email: str

class UserCreate(UserBase):
    password: str
    invite_code: str

class User(UserBase):
    id: int
    is_active: bool
    role: str
    access_group_id: Optional[int] = None

    class Config:
        orm_mode = True

class InviteCodeCreate(BaseModel):
    role_to_assign: str
    target_group_id: Optional[int] = None
    days_valid: int = 7

class AccessGroupCreate(BaseModel):
    name: str
    tool_ids: List[int] = []
