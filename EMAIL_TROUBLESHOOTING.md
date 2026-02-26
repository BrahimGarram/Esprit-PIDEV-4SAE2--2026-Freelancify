# Email Troubleshooting Guide

## Quick Test Steps

### Step 1: Test Email Configuration

1. **Restart the complaints service** (if not already done)
2. **Open your browser** and go to:
   ```
   http://localhost:8089/freelancity/report/test/send-email?email=cw.club102@gmail.com
   ```
3. You should see: "Test email sent successfully to: cw.club102@gmail.com"
4. **Check your inbox** (and spam folder) at cw.club102@gmail.com

If you receive the test email, the configuration is correct. If not, continue below.

### Step 2: Check Application Logs

Look for these messages in your console/logs:

**Success messages:**
```
Complaint creation notification sent to: user@example.com
```

**Error messages:**
```
Failed to send email notification
Failed to send complaint creation notification to: user@example.com
```

### Step 3: Common Issues

#### Issue 1: User Email Not Found
**Symptom:** No email sent, logs show "Failed to fetch email for user ID: X"

**Solution:** Check if users have email addresses in database:
```sql
SELECT id, username, email FROM user;
```

If email is NULL or empty, update it:
```sql
UPDATE user SET email = 'user@example.com' WHERE id = 1;
```

#### Issue 2: Gmail App Password Invalid
**Symptom:** Error: "Authentication failed"

**Solution:** 
1. Verify app password is correct: `mejwmoqbngvmqktsi`
2. Make sure 2-Factor Authentication is enabled on your Gmail
3. Generate a new app password if needed

#### Issue 3: Gmail Blocking Less Secure Apps
**Symptom:** Error: "Username and Password not accepted"

**Solution:**
1. Go to https://myaccount.google.com/security
2. Enable 2-Step Verification
3. Generate new App Password
4. Use the new app password in application.yml

#### Issue 4: Port 587 Blocked
**Symptom:** Error: "Connection timeout" or "Connection refused"

**Solution:**
1. Check firewall settings
2. Temporarily disable antivirus
3. Test SMTP connection:
   ```bash
   telnet smtp.gmail.com 587
   ```

#### Issue 5: User Service Not Running
**Symptom:** Error: "Failed to fetch email for user ID"

**Solution:**
1. Make sure user service is running on port 8081
2. Test: http://localhost:8081/api/users/1
3. Should return user data with email field

## Detailed Debugging

### Check 1: Verify Email Configuration

Open `complaints-service/src/main/resources/application.yml` and verify:
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: cw.club102@gmail.com  # ✓ Your email
    password: mejwmoqbngvmqktsi      # ✓ Your app password (no spaces)
```

### Check 2: Verify User Has Email

When you create a complaint, the system fetches the user's email from the User entity.

**Test the user service:**
```bash
curl http://localhost:8081/api/users/1
```

Should return something like:
```json
{
  "id": 1,
  "username": "john",
  "email": "john@example.com",  // ← This must exist!
  ...
}
```

### Check 3: Enable Debug Logging

Add this to `application.yml`:
```yaml
logging:
  level:
    org.springframework.mail: DEBUG
    com.example.freelanceplatformspringapp.Complaints.Services.EmailService: DEBUG
```

Restart and check logs for detailed email sending information.

### Check 4: Test with Mailtrap (Alternative)

If Gmail is not working, try Mailtrap for testing:

1. Sign up at https://mailtrap.io (free)
2. Get SMTP credentials
3. Update `application.yml`:
```yaml
spring:
  mail:
    host: smtp.mailtrap.io
    port: 2525
    username: your-mailtrap-username
    password: your-mailtrap-password
```

## Manual Email Test

### Option 1: Use Test Endpoint

```bash
# Send test email to yourself
curl "http://localhost:8089/freelancity/report/test/send-email?email=cw.club102@gmail.com"
```

### Option 2: Create Complaint via API

```bash
curl -X POST "http://localhost:8089/freelancity/report/create-claim?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Email Notification",
    "description": "Testing email system",
    "claimPriority": "High"
  }'
```

Check logs for email sending confirmation.

## Verify Email Sending Flow

1. **User creates complaint** → `ComplaintsService.addClaim()`
2. **Complaint saved** → Database
3. **Fetch user email** → `getUserEmail(userId)` calls user service
4. **Send email** → `EmailService.sendComplaintCreatedNotification()`
5. **Email sent** → Gmail SMTP server
6. **User receives email** → Check inbox/spam

## Check Each Step

### Step 1: Complaint Created?
```sql
SELECT * FROM complaints ORDER BY id_reclamation DESC LIMIT 1;
```

### Step 2: User Email Exists?
```sql
SELECT id, username, email FROM user WHERE id = 1;
```

### Step 3: Check Logs
Look for:
```
Complaint creation notification sent to: user@example.com
```
or
```
Failed to send email notification: [error message]
```

## Quick Fixes

### Fix 1: Update User Email
```sql
UPDATE user SET email = 'cw.club102@gmail.com' WHERE id = 1;
```

### Fix 2: Restart Services
```bash
# Restart complaints service
cd complaints-service
./mvnw spring-boot:run

# Restart user service (if needed)
cd backend
./mvnw spring-boot:run
```

### Fix 3: Test Email Directly
Use the test endpoint:
```
http://localhost:8089/freelancity/report/test/send-email?email=cw.club102@gmail.com
```

## Still Not Working?

### Collect This Information:

1. **Application logs** - Copy the last 50 lines after creating a complaint
2. **User email check** - Result of: `SELECT email FROM user WHERE id = 1;`
3. **Test endpoint result** - What happens when you visit the test URL?
4. **Error messages** - Any red error messages in console?

### Common Error Messages:

| Error | Cause | Solution |
|-------|-------|----------|
| "Authentication failed" | Wrong password | Regenerate app password |
| "Connection timeout" | Port blocked | Check firewall |
| "Failed to fetch email" | User service down | Start user service |
| "Email is null" | No email in database | Update user email |

## Success Indicators

✅ Log shows: "Complaint creation notification sent to: user@example.com"
✅ No errors in console
✅ Email appears in inbox (or spam folder)
✅ Test endpoint returns success message

---

**Next Step:** Try the test endpoint first:
```
http://localhost:8089/freelancity/report/test/send-email?email=cw.club102@gmail.com
```

This will tell us if the email configuration is working at all.
