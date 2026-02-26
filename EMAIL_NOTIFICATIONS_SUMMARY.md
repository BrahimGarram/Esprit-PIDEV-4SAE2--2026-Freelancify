# Email Notifications - Setup Complete ✅

## Configuration Status

✅ Gmail SMTP configured with your app password: `mejwmoqbngvmqktsi`
✅ Email notifications implemented for:
   - Complaint creation (sent to user)
   - Admin response/resolution note (sent to user)
   - Status changes (sent to user)
   - Auto-assignment (sent to admin)

## ⚠️ ACTION REQUIRED

**You must add your Gmail address to the configuration file:**

1. Open: `complaints-service/src/main/resources/application.yml`
2. Find line: `username: your-email@gmail.com`
3. Replace with your actual Gmail address (the one you generated the app password for)
4. Save the file
5. Restart the complaints service

Example:
```yaml
spring:
  mail:
    username: john.doe@gmail.com  # Your actual Gmail
    password: mejwmoqbngvmqktsi
```

## Email Notifications Implemented

### 1. 📧 Complaint Created
**Trigger:** User submits a new complaint
**Recipient:** User (complaint creator)
**Content:**
- Complaint ID, title, description
- Priority and status
- Auto-detected category
- Link to view complaint

**Code location:** `ComplaintsService.addClaim()` → `EmailService.sendComplaintCreatedNotification()`

### 2. 💬 Admin Response
**Trigger:** Admin adds or updates resolution note
**Recipient:** User (complaint creator)
**Content:**
- Complaint ID and title
- Current status
- Admin's resolution note/response
- Link to view complaint

**Code location:** `ComplaintsService.updateComplaint()` → `EmailService.sendResolutionNoteNotification()`

### 3. 🔄 Status Change
**Trigger:** Admin changes complaint status (Pending → Under Review → Resolved → Closed)
**Recipient:** User (complaint creator)
**Content:**
- Previous status
- New status
- Link to view complaint

**Code location:** `ComplaintsService.updateComplaint()` → `EmailService.sendStatusChangeNotification()`

## How It Works

```
┌─────────────────────────────────────────────────────────────┐
│ User Creates Complaint                                       │
│ (http://localhost:4200/complaints)                          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ Backend: ComplaintsService.addClaim()                       │
│ - Saves complaint to database                               │
│ - Auto-categorizes (Technical, Billing, etc.)              │
│ - Auto-assigns to admin                                     │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ EmailService.sendComplaintCreatedNotification()            │
│ - Fetches user email from User entity                      │
│ - Sends "Complaint Received" email to user                 │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ Admin Adds Resolution Note                                  │
│ (http://localhost:4200/admin/complaints)                   │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ Backend: ComplaintsService.updateComplaint()               │
│ - Detects resolution note was added/changed                │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ EmailService.sendResolutionNoteNotification()              │
│ - Fetches user email from User entity                      │
│ - Sends "Admin Response" email to user                     │
└─────────────────────────────────────────────────────────────┘
```

## Testing Steps

### Test 1: Create Complaint Email
1. Make sure you've added your Gmail address to `application.yml`
2. Restart complaints service
3. Go to `http://localhost:4200/complaints`
4. Click "Add Complaint"
5. Fill in:
   - Title: "Test payment issue"
   - Description: "Cannot process payment"
   - Priority: High
6. Submit
7. **Check email inbox** - should receive "Complaint Received" email

### Test 2: Admin Response Email
1. Go to `http://localhost:4200/admin/complaints`
2. Click "View Details" on the test complaint
3. In the resolution note field, type: "We are investigating your payment issue"
4. Click "Save Resolution Note"
5. **Check email inbox** - should receive "Admin Response" email

### Test 3: Status Change Email
1. In admin dashboard, view complaint details
2. Click "Start Review" button
3. **Check email inbox** - should receive "Status Updated" email

## User Email Source

Emails are sent to the email address stored in the User entity:
- User service endpoint: `http://localhost:8081/api/users/{userId}`
- Email field: `user.email`

Make sure users in your database have valid email addresses!

## Troubleshooting

### No emails received?

1. **Check you added your Gmail address**
   ```yaml
   username: your-actual-email@gmail.com  # Not "your-email@gmail.com"
   ```

2. **Check application logs**
   Look for:
   ```
   Complaint creation notification sent to: user@example.com
   Resolution note notification sent to: user@example.com
   ```
   
   Or errors:
   ```
   Failed to send email notification
   ```

3. **Check user has email in database**
   ```sql
   SELECT id, username, email FROM user WHERE id = 1;
   ```

4. **Check spam folder**
   Gmail might filter automated emails to spam initially

5. **Verify SMTP connection**
   ```bash
   telnet smtp.gmail.com 587
   ```

## Files Modified

1. `complaints-service/src/main/resources/application.yml` - Email configuration
2. `complaints-service/src/main/java/.../Services/EmailService.java` - Added `sendComplaintCreatedNotification()`
3. `complaints-service/src/main/java/.../Services/ComplaintsService.java` - Integrated email sending
4. `complaints-service/pom.xml` - Added `spring-boot-starter-mail` dependency

## Next Steps

1. ✅ Add your Gmail address to `application.yml`
2. ✅ Restart complaints service: `cd complaints-service && ./mvnw spring-boot:run`
3. ✅ Test complaint creation
4. ✅ Test admin response
5. ✅ Verify emails are received

## Production Considerations

For production deployment:
- Use dedicated email service (SendGrid, Mailgun, AWS SES)
- Create HTML email templates with company branding
- Add email preferences (allow users to opt-out)
- Implement email queue for reliability
- Monitor email delivery rates
- Add rate limiting to prevent spam

---

**Need help?** Check `EMAIL_SETUP_INSTRUCTIONS.md` for detailed troubleshooting steps.
