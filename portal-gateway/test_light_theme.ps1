
$ErrorActionPreference = "Stop"
$baseUrl = "http://localhost:8081"
$adminUser = "admin"
$adminPass = "admin123"

# 1. Login as Admin
Write-Host "Logging in as Admin..."
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$loginUrl = "$baseUrl/login"
$loginBody = @{
    username = $adminUser
    password = $adminPass
}
try {
    Invoke-WebRequest -Uri $loginUrl -Method Post -Body $loginBody -WebSession $session -UseBasicParsing | Out-Null
    Write-Host "✅ Admin Login Successful"
} catch {
    Write-Host "❌ Admin Login Failed: $_"
    exit
}

# 2. Generate Invite Code
Write-Host "Generating Invite Code..."
$inviteUrl = "$baseUrl/api/admin/invites/generate"
try {
    $response = Invoke-RestMethod -Uri $inviteUrl -Method Post -WebSession $session -UseBasicParsing
    $inviteCode = $response.code
    Write-Host "✅ Invite Code Generated: $inviteCode"
} catch {
    Write-Host "❌ Invite Generation Failed: $_"
    exit
}

# 3. Register Master
Write-Host "Registering Master User..."
$registerUrl = "$baseUrl/auth/signup"
$masterUser = "master_light"
$masterPass = "master123"
$registerBody = @{
    username = $masterUser
    password = $masterPass
    inviteCode = $inviteCode
} | ConvertTo-Json
try {
    Invoke-WebRequest -Uri $registerUrl -Method Post -Body $registerBody -ContentType "application/json" -UseBasicParsing | Out-Null
    Write-Host "✅ Master Registration Successful"
} catch {
    Write-Host "❌ Master Registration Failed: $_"
    exit
}

# 4. Login as Master
Write-Host "Logging in as Master..."
$masterSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
try {
    Invoke-WebRequest -Uri $loginUrl -Method Post -Body @{ username = $masterUser; password = $masterPass } -WebSession $masterSession -UseBasicParsing | Out-Null
    Write-Host "✅ Master Login Successful"
} catch {
    Write-Host "❌ Master Login Failed: $_"
    exit
}

# 5. Access Master Dashboard
Write-Host "Accessing Master Dashboard..."
$dashboardUrl = "$baseUrl/master/dashboard"
try {
    $dashboard = Invoke-WebRequest -Uri $dashboardUrl -Method Get -WebSession $masterSession -UseBasicParsing
    if ($dashboard.Content -match "Team Access Management") {
        Write-Host "✅ Dashboard Access Successful (Found 'Team Access Management')"
    } else {
        Write-Host "❌ Dashboard Access Failed: Content mismatch"
        Write-Host $dashboard.Content
        exit
    }
} catch {
    Write-Host "❌ Dashboard Access Failed: $_"
    exit
}

# 6. Create Access Group
Write-Host "Creating Access Group..."
$groupUrl = "$baseUrl/master/dashboard/create-group"
# Assuming tool ID 1 exists (TheLogX)
$groupBody = @{
    groupName = "Light Theme Squad"
    toolIds = 1
}
try {
    Invoke-WebRequest -Uri $groupUrl -Method Post -Body $groupBody -WebSession $masterSession -UseBasicParsing | Out-Null
    Write-Host "✅ Group Creation Request Sent"
} catch {
    Write-Host "❌ Group Creation Failed: $_"
    exit
}

# 7. Verify Group in Dashboard
Write-Host "Verifying Group in Dashboard..."
$dashboard = Invoke-WebRequest -Uri $dashboardUrl -Method Get -WebSession $masterSession -UseBasicParsing
if ($dashboard.Content -match "Light Theme Squad") {
    Write-Host "✅ Group Found in Dashboard!"
} else {
    Write-Host "❌ Group NOT Found in Dashboard"
    exit
}
