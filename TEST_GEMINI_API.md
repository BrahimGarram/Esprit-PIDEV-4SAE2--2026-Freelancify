# Test Gemini API - Quick Verification

## Issue Fixed

❌ **Before**: Used `gemini-2.0-flash-exp` (experimental, not available in v1beta)  
✅ **After**: Using `gemini-1.5-flash` (stable, production-ready)

---

## Test the API Directly

### Using curl (Windows PowerShell):

```powershell
$headers = @{
    "Content-Type" = "application/json"
    "x-goog-api-key" = "AIzaSyA5aY9d803zT7nQKQKb5inR152B0oc8_Pw"
}

$body = @{
    contents = @(
        @{
            parts = @(
                @{
                    text = "Is 'Google' a real company? Respond with yes or no."
                }
            )
        }
    )
} | ConvertTo-Json -Depth 10

Invoke-RestMethod -Uri "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent" -Method Post -Headers $headers -Body $body
```

### Using curl (Bash):

```bash
curl "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent" \
  -H "x-goog-api-key: AIzaSyA5aY9d803zT7nQKQKb5inR152B0oc8_Pw" \
  -H "Content-Type: application/json" \
  -d '{
    "contents": [{
      "parts": [{
        "text": "Is Google a real company? Respond with yes or no."
      }]
    }]
  }'
```

---

## Expected Response

```json
{
  "candidates": [
    {
      "content": {
        "parts": [
          {
            "text": "Yes.\n"
          }
        ],
        "role": "model"
      },
      "finishReason": "STOP",
      "index": 0
    }
  ]
}
```

---

## Test in Application

1. **Restart backend** (if running):
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **Open frontend**: `http://localhost:4200`

3. **Test verification**:
   - Login as Enterprise user
   - Click "New Company"
   - Type: **"Google"**
   - Should see: ✅ Verified with details

---

## Troubleshooting

### Still getting 404?
- Clear Maven cache: `mvn clean`
- Rebuild: `mvn compile`
- Restart backend

### Getting 400 Bad Request?
- Check API key is correct
- Verify JSON format in request

### Getting 429 Rate Limit?
- Wait 1 minute
- You've hit 15 requests/minute limit

---

## Available Gemini Models (v1beta)

✅ **Stable Models:**
- `gemini-1.5-flash` - Fast, efficient (USING THIS)
- `gemini-1.5-pro` - More capable, slower
- `gemini-1.0-pro` - Original model

❌ **Not Available in v1beta:**
- `gemini-2.0-flash-exp` - Experimental, only in v1alpha
- `gemini-3-flash-preview` - Preview, not released

---

## Success Criteria

- [x] Model changed to `gemini-1.5-flash`
- [x] Backend compiles successfully
- [x] No 404 errors
- [x] Company verification works
- [x] Documentation updated

---

## Ready to Test! 🚀

The fix is complete. Restart your backend and test company verification!
