# 🚀 Quick Start - Gemini Company Verification

## Start Application

```bash
# Terminal 1 - Backend
cd backend
mvn spring-boot:run

# Terminal 2 - Frontend
cd frontend
ng serve
```

## Test It

1. Open: `http://localhost:4200`
2. Login as Enterprise user
3. Click "New Company"
4. Type: **"Google"** or **"Microsoft"**
5. See AI verification! ✨

## Configuration

**File**: `backend/src/main/resources/application.yml`

```yaml
gemini:
  api:
    key: AIzaSyA5aY9d803zT7nQKQKb5inR152B0oc8_Pw
```

## What You'll See

### ✅ Real Company
```
✓ Company Verified!
Google LLC
🌍 United States
🆔 C2802481
📍 1600 Amphitheatre Parkway, Mountain View, CA
🔗 https://www.google.com
Verified by Gemini AI
```

### ❌ Fake Company
```
⚠ Not Verified
Company could not be verified
```

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Backend won't start | Check port 8081 is free |
| "Not configured" error | Check API key in application.yml |
| "429 Too Many Requests" | Wait 1 minute (rate limit) |
| No verification | Check backend logs |

## API Limits

- 15 requests/minute
- 1,500 requests/day
- Free tier

## Done! 🎉

Your company verification is powered by Google Gemini AI!

**Full Guide**: See `GEMINI_COMPANY_VERIFICATION_GUIDE.md`
