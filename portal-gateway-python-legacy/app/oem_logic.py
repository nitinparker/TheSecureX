import secrets
import string
from datetime import datetime, timedelta
from sqlalchemy.orm import Session
from .models import InviteCode

def generate_secure_invite(
    db: Session,
    role: str,
    group_id: int = None,
    days_valid: int = 7
):
    """
    Creates a unique, time-limited invite code.
    Roles: 'MASTER' (can create groups) or 'USER' (limited to a group)
    """
    # Generate a readable but secure code: SX-XXXX-XXXX
    suffix = ''.join(secrets.choice(string.ascii_uppercase + string.digits) for _ in range(8))
    formatted_code = f"SX-{suffix[:4]}-{suffix[4:]}"
    
    expiry = datetime.utcnow() + timedelta(days=days_valid)
    
    new_invite = InviteCode(
        code=formatted_code,
        role_to_assign=role.upper(),
        target_group_id=group_id,
        expires_at=expiry,
        is_used=False
    )
    
    db.add(new_invite)
    db.commit()
    db.refresh(new_invite)
    
    return {
        "invite_code": new_invite.code,
        "expires": new_invite.expires_at,
        "role_granted": new_invite.role_to_assign
    }
