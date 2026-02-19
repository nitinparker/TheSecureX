import requests
from fastapi import FastAPI, HTTPException, Query, UploadFile, File, Form
from fastapi.responses import HTMLResponse, JSONResponse
import os
import hashlib
import shutil

app = FastAPI(title="TheSecureAnalyzerX")

# Configuration
# Default to localhost for local dev, Docker overrides this via env var
JAVA_PORTAL_URL = os.environ.get("JAVA_PORTAL_URL", "http://localhost:8081/api/tools/verify")

def calculate_sha256(file_path):
    sha256_hash = hashlib.sha256()
    with open(file_path, "rb") as f:
        # Read in blocks to handle large forensic images
        for byte_block in iter(lambda: f.read(4096), b""):
            sha256_hash.update(byte_block)
    return sha256_hash.hexdigest()

        
@app.get("/", response_class=HTMLResponse)
async def tool_root(token: str = Query(None)):
    if not token:
        return """
        <html>
        <body style="font-family: sans-serif; padding: 50px; text-align: center; background: #f4f7f9;">
            <h1 style="color: #d93025;">🚫 Access Denied</h1>
            <p>You cannot access <b>TheSecureAnalyzerX</b> directly.</p>
            <p>Please login via the <b>SecureX Portal</b> to obtain a valid session token.</p>
            <br>
            <a href="http://localhost:8081" style="background: #1a73e8; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;">🔐 Go to SecureX Portal</a>
        </body>
        </html>
        """
    try:
        response = requests.get(f"{JAVA_PORTAL_URL}?token={token}")
        if response.status_code != 200:
            raise HTTPException(status_code=403, detail="Invalid or expired token")
        
        user_data = response.json() # Contains username/role from Java
    except Exception as e:
        print(f"Error contacting portal: {e}")
        raise HTTPException(status_code=500, detail="Portal unreachable")

    # Store user data for session
    # For simplicity in this demo, we'll embed it in the form or response.
    # In a real app, use a session cookie or JWT.
    
    return f"""
    <html>
        <body style="font-family: sans-serif; padding: 50px; background: #f4f7f9;">
            <h1 style="color: #1a73e8;">🛡️ TheSecureAnalyzerX</h1>
            <p>Welcome, <b>{user_data['username']}</b>. AI Engine is ready for analysis.</p>
            <div style="border: 1px solid #ddd; background: white; padding: 20px; border-radius: 8px;">
                <h4>AI Status: Initialized</h4>
                <p>Telemetry Stream: <span style="color: green;">ACTIVE</span></p>
                <hr>
                <h5>Upload Evidence for Analysis</h5>
                <form action="/analyze" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="username" value="{user_data['username']}">
                    <input type="file" name="file" required>
                    <button type="submit" style="background: #1a73e8; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer;">Analyze Evidence</button>
                </form>
            </div>
        </body>
    </html>
    """

@app.post("/analyze", response_class=JSONResponse)
async def analyze_file(file: UploadFile = File(...), username: str = Form(...)):
    try:
        # Save to evidence vault (shared volume)
        # Use absolute path inside container, or local relative path
        vault_root = os.environ.get("VAULT_PATH", "../evidence-vault")
        vault_path = os.path.join(vault_root, file.filename)
        # Ensure directory exists (it should be mounted, but good practice)
        os.makedirs(vault_root, exist_ok=True)
        
        with open(vault_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)
        
        file_hash = calculate_sha256(vault_path)
        
        # Log evidence to Portal
        evidence_payload = {
            "username": username,
            "fileName": file.filename,
            "fileHash": file_hash,
            "fileType": os.path.splitext(file.filename)[1]
        }
        
        # Determine portal URL for internal communication (Docker vs Local)
        # Use JAVA_PORTAL_URL base but switch endpoint
        portal_evidence_url = JAVA_PORTAL_URL.replace("/api/tools/verify", "/api/evidence/log")
        
        requests.post(portal_evidence_url, json=evidence_payload)
        
        return {
            "status": "Analysis Complete",
            "filename": file.filename,
            "integrity_hash": file_hash,
            "vault_location": vault_path,
            "message": "Evidence hash calculated and Chain of Custody logged."
        }
    except Exception as e:
        return JSONResponse(status_code=500, content={"error": str(e)})
