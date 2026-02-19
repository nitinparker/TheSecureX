from passlib.context import CryptContext
from datetime import datetime, timedelta
from typing import Optional, List
from jose import jwt

# SECRET_KEY should be loaded from environment variables in production
SECRET_KEY = "09d25e094faa6ca2556c818166b7a9563b93f7099f6f0f4caa6cf63b88e8d3e7"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 30

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

def verify_password(plain_password, hashed_password):
    return pwd_context.verify(plain_password, hashed_password)

def get_password_hash(password):
    return pwd_context.hash(password)

def create_access_token(data: dict, expires_delta: Optional[timedelta] = None):
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=15)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt

def create_access_mask(user_access_groups) -> List[str]:
    """
    Flattens the user's access groups into a list of allowed tool IDs.
    In a real implementation, this would query the group_tool_map.
    """
    # Placeholder logic - in reality, we would query the DB for tools mapped to these groups
    allowed_tools = []
    for group in user_access_groups:
        if "Analyst" in group.name:
            allowed_tools.extend(["TheLogX", "TheSecureDiskX"])
        elif "Admin" in group.name:
            allowed_tools.extend(["TheLogX", "TheSecureDiskX", "TheRemoteX", "TheNetProtectX"])
    return list(set(allowed_tools))
