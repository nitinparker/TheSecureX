from sqlalchemy import Boolean, Column, ForeignKey, Integer, String, DateTime, Table
from sqlalchemy.orm import relationship
from datetime import datetime
from .database import Base

# Association Table for Many-to-Many between AccessGroup and Tools
group_tool_map = Table(
    'group_tool_map', Base.metadata,
    Column('group_id', Integer, ForeignKey('access_groups.id')),
    Column('tool_id', String, primary_key=True) # e.g., 'TheLogX', 'TheRemoteX'
)

# Association Table for Many-to-Many between User and AccessGroup
user_group_map = Table(
    'user_group_map', Base.metadata,
    Column('user_id', Integer, ForeignKey('users.id')),
    Column('group_id', Integer, ForeignKey('access_groups.id'))
)

class Organization(Base):
    __tablename__ = "organizations"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, unique=True, index=True)
    created_at = Column(DateTime, default=datetime.utcnow)

    access_groups = relationship("AccessGroup", back_populates="organization")

class AccessGroup(Base):
    __tablename__ = "access_groups"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, index=True) # e.g., "Level 1 Analysts"
    organization_id = Column(Integer, ForeignKey("organizations.id"))

    organization = relationship("Organization", back_populates="access_groups")
    users = relationship("User", secondary=user_group_map, back_populates="access_groups")
    invite_codes = relationship("InviteCode", back_populates="access_group")

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    username = Column(String, unique=True, index=True)
    email = Column(String, unique=True, index=True)
    hashed_password = Column(String)
    is_active = Column(Boolean, default=True)
    is_superuser = Column(Boolean, default=False) # OEM Admin

    access_groups = relationship("AccessGroup", secondary=user_group_map, back_populates="users")

class InviteCode(Base):
    __tablename__ = "invite_codes"

    id = Column(Integer, primary_key=True, index=True)
    code = Column(String, unique=True, index=True)
    is_used = Column(Boolean, default=False)
    expires_at = Column(DateTime)
    access_group_id = Column(Integer, ForeignKey("access_groups.id"))

    access_group = relationship("AccessGroup", back_populates="invite_codes")
