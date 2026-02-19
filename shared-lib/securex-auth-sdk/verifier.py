from jose import jwt, JWTError
from typing import Optional, List

# In a real scenario, this would be imported from a config or env var
# It MUST match the key used in portal-gateway
SECRET_KEY = "09d25e094faa6ca2556c818166b7a9563b93f7099f6f0f4caa6cf63b88e8d3e7"
ALGORITHM = "HS256"

class AuthError(Exception):
    pass

def verify_token(token: str, required_scope: Optional[str] = None) -> dict:
    """
    Verifies the JWT and checks for required scope.
    Returns the decoded token payload if valid.
    Raises AuthError if invalid or unauthorized.
    """
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        user_scopes: List[str] = payload.get("scopes", [])
        
        if required_scope and required_scope not in user_scopes:
            raise AuthError(f"Access Denied: Missing scope '{required_scope}'")
            
        return payload
    except JWTError:
        raise AuthError("Invalid Token")
