from app.database import SessionLocal, engine
from app import models, auth
from datetime import datetime

def setup_oem():
    db = SessionLocal()
    
    # Ensure Tools exist
    tools = ["TheLogX", "TheRemoteX", "TheSecureDiskX", "TheNetProtectX"]
    for t_name in tools:
        if not db.query(models.Tool).filter(models.Tool.name == t_name).first():
            db.add(models.Tool(name=t_name))
    
    # Check for OEM User
    oem_user = db.query(models.User).filter(models.User.role == "OEM").first()
    if not oem_user:
        print("Creating Initial OEM Account...")
        oem_user = models.User(
            username="admin_oem",
            email="oem@thesecurex.com",
            password_hash=auth.get_password_hash("securex_root_2024"),
            role="OEM",
            is_active=True
        )
        db.add(oem_user)
        db.commit()
        print("OEM Account Created!")
        print("Username: admin_oem")
        print("Password: securex_root_2024")
    else:
        print("OEM Account already exists.")
    
    db.close()

if __name__ == "__main__":
    # Create tables first
    models.Base.metadata.create_all(bind=engine)
    setup_oem()
