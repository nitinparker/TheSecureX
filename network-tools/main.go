package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"time"
	"math/rand"
    "path/filepath"
)

var sessions = make(map[string]string)

func verifyToken(token string) (bool, string) {
	portalURL := os.Getenv("JAVA_PORTAL_URL")
	if portalURL == "" {
		portalURL = "http://localhost:8081/api/tools/verify"
	}
	
	// Logic to call portal-gateway
	resp, err := http.Get(portalURL + "?token=" + token)
	if err != nil {
		fmt.Printf("Error contacting portal: %v\n", err)
		return false, ""
	}
	defer resp.Body.Close()

	if resp.StatusCode == http.StatusOK {
		var userData map[string]interface{}
		if err := json.NewDecoder(resp.Body).Decode(&userData); err == nil {
			if username, ok := userData["username"].(string); ok {
				return true, username
			}
		}
		return true, "User"
	}
	return false, ""
}

func generateSessionID() string {
	b := make([]byte, 16)
	rand.Read(b)
	return fmt.Sprintf("%x", b)
}

func main() {
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		// Check for session cookie
		cookie, err := r.Cookie("session_id")
		var username string
		if err == nil {
			if user, ok := sessions[cookie.Value]; ok {
				username = user
			}
		}

		// If no valid session, check token
		if username == "" {
			token := r.URL.Query().Get("token")
			valid, user := verifyToken(token)
			if token != "" && valid {
				username = user
				// Create session
				sessionID := generateSessionID()
				sessions[sessionID] = username
				http.SetCookie(w, &http.Cookie{
					Name: "session_id",
					Value: sessionID,
					Path: "/",
					HttpOnly: true,
				})
			} else {
				http.Error(w, "Unauthorized: Invalid Forensic Token", http.StatusUnauthorized)
				return
			}
		}

		fmt.Fprintf(w, `
		<html>
		<body style="font-family: sans-serif; padding: 50px; background: #f4f7f9;">
			<h1 style="color: #d93025;">🛡️ TheNetProtectX</h1>
			<p>Welcome, <b>%s</b>.</p>
			<p>Network Sniffer active on interface eth0...</p>
			<div style="border: 1px solid #ddd; background: white; padding: 20px; border-radius: 8px;">
                 <h4>Status: Monitoring</h4>
                 <p>Packets Captured: <span style="color: green;">0</span></p>
                 <form action="/capture" method="post">
                    <button type="submit" style="background: #d93025; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer;">🛑 Simulate Attack Capture</button>
                 </form>
            </div>
		</body>
		</html>`, username)
	})

	http.HandleFunc("/capture", func(w http.ResponseWriter, r *http.Request) {
		if r.Method != "POST" {
			http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
			return
		}

		// Write dummy pcap file to vault
        vaultRoot := os.Getenv("VAULT_PATH")
        if vaultRoot == "" {
            vaultRoot = "../evidence-vault"
        }
		vaultPath := filepath.Join(vaultRoot, "attack.pcap")
		// Ensure directory exists
		os.MkdirAll(vaultRoot, 0755)
		
		content := []byte("Simulated PCAP content - Network Attack Pattern Detected")
		err := os.WriteFile(vaultPath, content, 0644)
		if err != nil {
			http.Error(w, "Failed to save capture: "+err.Error(), http.StatusInternalServerError)
			return
		}

		fmt.Fprintf(w, `
		<html>
		<body style="font-family: sans-serif; padding: 50px; background: #f4f7f9;">
			<h1 style="color: #d93025;">🛡️ Capture Saved</h1>
			<p>Attack signature saved to <b>%s</b></p>
			<p>Chain of Custody initiated.</p>
			<a href="/">Back to Monitor</a>
		</body>
		</html>`, vaultPath)
	})

	fmt.Println("TheNetProtectX listening on port 8002...")
	http.ListenAndServe(":8002", nil)
}
