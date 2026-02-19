# API Gateway Entry Point
from fastapi import FastAPI

app = FastAPI()

@app.get("/")
def read_root():
    return {"Hello": "TheSecureX API Gateway"}
