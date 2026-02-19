from fastapi import FastAPI
from .models import database, models
from .auth import routes as auth_routes

# Create Database Tables
models.Base.metadata.create_all(bind=database.engine)

app = FastAPI(title="TheSecureX Portal Gateway")

app.include_router(auth_routes.router, prefix="/auth", tags=["Authentication"])

@app.get("/")
def read_root():
    return {"message": "Welcome to TheSecureX Portal Gateway"}
