import uvicorn
from fastapi import FastAPI
from app.models import Base
from app.database import engine
from app.routes import oem, master, user

# Create Tables
Base.metadata.create_all(bind=engine)

app = FastAPI(title="TheSecureX Portal Gateway (RBAC)")

# Include Routes
app.include_router(oem.router, prefix="/oem", tags=["OEM"])
app.include_router(master.router, prefix="/master", tags=["Master"])
app.include_router(user.router, prefix="/auth", tags=["Auth"])

if __name__ == "__main__":
    uvicorn.run("run:app", host="0.0.0.0", port=8000, reload=True)
