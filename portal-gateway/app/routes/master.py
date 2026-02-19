from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from .. import models, schemas
from ..database import SessionLocal

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.post("/create-group")
def create_group(group: schemas.AccessGroupCreate, db: Session = Depends(get_db)):
    # In real app, check if current user is MASTER
    new_group = models.AccessGroup(name=group.name)
    
    # Add tools to group
    for tool_id in group.tool_ids:
        tool = db.query(models.Tool).filter(models.Tool.id == tool_id).first()
        if tool:
            new_group.tools.append(tool)
            
    db.add(new_group)
    db.commit()
    return {"message": "Group created successfully", "group": new_group.name}
