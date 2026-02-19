import argparse
import secrets
import sys
from datetime import datetime, timedelta
from sqlalchemy.orm import Session
from models import database, models

def generate_invite(group_name: str, days_valid: int):
    db = database.SessionLocal()
    try:
        # Find or Create Access Group (Simplified for CLI)
        group = db.query(models.AccessGroup).filter(models.AccessGroup.name == group_name).first()
        if not group:
            print(f"Group '{group_name}' not found. Creating temporary group...")
            # Ensure Organization exists
            org = db.query(models.Organization).filter(models.Organization.name == "DefaultOrg").first()
            if not org:
                org = models.Organization(name="DefaultOrg")
                db.add(org)
                db.commit()
            
            group = models.AccessGroup(name=group_name, organization_id=org.id)
            db.add(group)
            db.commit()

        # Generate Code
        code_str = f"SX-{secrets.token_hex(4).upper()}"
        expires = datetime.utcnow() + timedelta(days=days_valid)
        
        invite = models.InviteCode(
            code=code_str,
            expires_at=expires,
            access_group_id=group.id
        )
        db.add(invite)
        db.commit()
        
        print(f"Invite created! Send this code: {code_str}")
        print(f"Valid for group: {group_name}")
        print(f"Expires: {expires}")
        
    finally:
        db.close()

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="TheSecureX Management CLI")
    subparsers = parser.add_subparsers(dest="command")

    invite_parser = subparsers.add_parser("generate-invite", help="Generate a signup invite code")
    invite_parser.add_argument("--group", required=True, help="Access Group name (e.g., 'Level 1 Analysts')")
    invite_parser.add_argument("--days", type=int, default=7, help="Validity in days")

    args = parser.parse_args()

    if args.command == "generate-invite":
        generate_invite(args.group, args.days)
    else:
        parser.print_help()
