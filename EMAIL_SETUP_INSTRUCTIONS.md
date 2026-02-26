# Email Setup Instructions

## ⚠️ IMPORTANT: Complete Email Configuration

Your Gmail SMTP password has been configured: `mejw moqb ngvm qktsi`

### Required: Add Your Gmail Address

You need to update the `username` field in the configuration file with your actual Gmail address.

**File to edit:** `complaints-service/src/main/resources/application.yml`

**Find this section:**
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com  # ← CHANGE THIS to your actual Gmail address
    password: mejw moqb ngvm qktsi
```

**Replace `your-email@gmail.com` with your actual Gmail address** (the one you generated the app password for).

Example:
```yaml
    username: john.doe@gmail.com
    password: mejw moqb ngvm qktsi
```

## Email Notifications Configured

### 1. When User Creates a Complaint
**Recipient:** User who created the complaint
**Email includes:**
- Complaint ID
- Title and description
- Priority and status
- Auto-detected category
- Link to view complaint

### 2. When Admin Responds (Adds Resolution Note)
**Recipient:** User who created the complaint
**Email includes:**
- Complaint ID and title
- Current status
- Admin's response/resolution note
- Link to view complaint

### 3. When Admin Changes Status
**Recipient:** User who created the complaint
**Email includes:**
- Previous status
- New status
- Link to view complaint

### 4. When Complaint is Auto-Assigned
**Recipient:** Admin assigned to the complaint
**Email includes:**
- Complaint details
- Category and priority
- Link to admin dashboard

## Testing Email Notifications

### Test 1: Create Complaint
1. Go to `http://localhost:4200/complaints`
2. Click "Add Complaint"
3. Fill in title (e.g., "Payment issue"), description, priority
4. Submit
5. **Expected:** User receives "Complaint Received" email

### Test 2: Admin Response
1. Go to `http://localhost:4200/admin/complaints`
2. Click "View Details" on any complaint
3. Add a resolution note (e.g., "We are investigating this issue")
4. Click "Save Resolution Note"
5. **Expected:** User receives "Admin Response" email

### Test 3: Status Change
1. In admin dashboard, view complaint details
2. Click any status change button (e.g., "Start Review", "Resolve")
3. **Expected:** User receives "Status Updated" email

## Troubleshooting

### Emails Not Sending?

1. **Verify Gmail Address**
   - Make sure you replaced `your-email@gmail.com` with your actual Gmail address
   - The address must match the one you generated the app password for

2. **Check Application Logs**
   ```bash
   # Look for these messages:
   "Complaint creation notification sent to: user@example.com"
   "Resolution note notification sent to: user@example.com"
   "Status change notification sent to: user@example.com"
   
   # Or errors:
   "Failed to send email notification"
   ```

3. **Verify App Password**
   - Make sure the app password is correct: `mejw moqb ngvm qktsi`
   - No spaces in the password (already configured correctly)

4. **Check User Email in Database**
   - Emails are sent to the email address stored in the User entity
   - Verify users have valid email addresses:
   ```sql
   SELECT id, username, email FROM user;
   ```

5. **Test SMTP Connection**
   ```bash
   telnet smtp.gmail.com 587
   ```
   Should connect successfully.

6. **Firewall/Antivirus**
   - Make sure port 587 is not blocked
   - Temporarily disable antivirus to test

## Email Flow Diagram

```
User Creates Complaint
    ↓
System saves complaint
    ↓
System auto-categorizes & assigns
    ↓
Email sent to USER: "Complaint Received"
    ↓
Email sent to ADMIN: "New Complaint Assigned"

---

Admin Adds Resolution Note
    ↓
System saves note
    ↓
Email sent to USER: "Admin Response"

---

Admin Changes Status
    ↓
System updates status
    ↓
Email sent to USER: "Status Updated"
```

## Next Steps

1. ✅ Add your Gmail address to `application.yml`
2. ✅ Restart complaints service
3. ✅ Test by creating a complaint
4. ✅ Check your email inbox
5. ✅ Test admin response feature

## Production Recommendations

For production use, consider:
- Using a dedicated email service (SendGrid, Mailgun, AWS SES)
- Creating HTML email templates with branding
- Adding unsubscribe functionality
- Implementing email queue for reliability
- Setting up email delivery monitoring
