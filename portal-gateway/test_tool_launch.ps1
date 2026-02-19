$baseUrl = "http://localhost:8081"
$username = "user"
$password = "user123"

# 1. Login
echo "Logging in as $username..."
$loginUrl = "$baseUrl/login"
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession

try {
    $response = Invoke-WebRequest -Uri $loginUrl -SessionVariable session -Method Get -UseBasicParsing
} catch {
    echo "Error accessing login page: $_"
    exit 1
}

$body = @{ username = $username; password = $password }
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/login" -Method Post -Body $body -WebSession $session -UseBasicParsing
} catch {
    $response = $_.Exception.Response
}

if ($response.StatusCode -eq 200 -and $response.Content -match "Invalid username or password") {
    echo "Login failed! Invalid credentials."
    exit 1
}
echo "Login successful."

# 2. Launch Tool (ID 1 - TheLogX) - Using .NET HttpWebRequest to handle redirect manually
echo "Launching Tool ID 1..."
$launchUrl = "$baseUrl/tools/launch/1"
$request = [System.Net.HttpWebRequest]::Create($launchUrl)
$request.AllowAutoRedirect = $false
$request.CookieContainer = $session.Cookies

try {
    $launchResponse = $request.GetResponse()
} catch {
    # If 302, it might still throw in some contexts, but usually GetResponse() returns 302 if AllowAutoRedirect is false
    if ($_.Exception.Response) {
        $launchResponse = $_.Exception.Response
    } else {
        echo "Error launching tool: $_"
        exit 1
    }
}

# Cast to HttpWebResponse to access StatusCode and Headers
$httpResponse = [System.Net.HttpWebResponse]$launchResponse
$statusCode = [int]$httpResponse.StatusCode

if ($statusCode -eq 302) {
    $redirectUrl = $httpResponse.Headers["Location"]
    echo "Redirect URL: $redirectUrl"
    
    if ($redirectUrl -match "token=([^&]+)") {
        $token = $matches[1]
        echo "Token extracted: $token"
        
        # 3. Verify Token
        echo "Verifying token..."
        $verifyUrl = "$baseUrl/api/tools/verify?token=$token"
        try {
            $verifyResponse = Invoke-WebRequest -Uri $verifyUrl -Method Get -UseBasicParsing
            $json = $verifyResponse.Content | ConvertFrom-Json
            
            if ($json.valid -eq $true) {
                echo "✅ Token verification SUCCESS!"
                echo "User: $($json.username)"
                echo "Tool ID: $($json.toolId)"
            } else {
                echo "❌ Token verification FAILED!"
                echo $json
                exit 1
            }
        } catch {
            echo "❌ Error verifying token: $_"
            exit 1
        }
        
        # 4. Verify Token Burn
        echo "Verifying token burn (should fail)..."
        try {
            $verifyResponse2 = Invoke-WebRequest -Uri $verifyUrl -Method Get -ErrorAction Stop -UseBasicParsing
            # If successful (status 200), verify content
            $json2 = $verifyResponse2.Content | ConvertFrom-Json
            if ($json2.valid -eq $true) {
                echo "❌ Token NOT burned! It is still valid."
                exit 1
            }
        } catch {
            echo "✅ Token burned successfully (Request failed as expected)."
        }

    } else {
        echo "❌ Token not found in redirect URL!"
        exit 1
    }
} else {
    echo "❌ Expected 302 Redirect, got $statusCode"
    exit 1
}
