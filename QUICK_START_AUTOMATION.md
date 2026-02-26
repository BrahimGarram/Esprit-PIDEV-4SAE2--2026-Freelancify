# Quick Start Guide - Complaints Automation

## 🚀 Quick Setup (5 minutes)

### Step 1: Configure Email (Choose One)

#### Option A: Gmail (Easiest for Testing)
1. Go to https://myaccount.google.com/security
2. Enable 2-Step Verification
3. Go to App Passwords → Generate new password for "Mail"
4. Copy the 16-digit password

#### Option B: Mailtrap (Best for Development)
1. Sign up at https://mailtrap.io (free)
2. Get SMTP credentials from inbox settings

### Step 2: Update Configuration

Edit `complaints-service/src/main/resources/application.yml`:

```yaml
spring:
  mail:
    host: smtp.gmail.com  # or smtp.mailtrap.io
    port: 587
    username: your-email@gmail.com  # or mailtrap username
    password: your-app-password  # or mailtrap password
```

### Step 3: Restart Complaints Service

```bash
cd complaints-service
./mvnw spring-boot:run
```

Or restart from your IDE.

### Step 4: Test It!

1. **Test Auto-Assignment**:
   - Create complaint with title: "Payment not working"
   - Check logs: Should see "Auto-detected category: BILLING"
   - Check database: `category` should be "BILLING"

2. **Test Email Notifications**:
   - Admin changes complaint status
   - Check user's email inbox
   - Should receive status change notification

3. **Test Resolution Note**:
   - Admin adds resolution note
   - User receives email with admin's response

## 📧 Email Notification Types

| Trigger | Recipient | When |
|---------|-----------|------|
| Status Change | User | Admin changes complaint status |
| Resolution Note | User | Admin adds/updates resolution note |
| New Assignment | Admin | Complaint auto-assigned to admin |
| Pending Reminder | User | Complaint pending > 3 days (daily at 9 AM) |
| Auto-Close | User | Resolved complaint inactive for 6 days (daily at 2 AM) |

## 🤖 Auto-Assignment Rules

| Keywords | Category | Default Admin |
|----------|----------|---------------|
| error, bug, crash, broken | TECHNICAL | Admin ID 1 |
| payment, invoice, refund | BILLING | Admin ID 2 |
| login, password, account | ACCOUNT | Admin ID 2 |
| service, quality, support | SERVICE | Admin ID 1 |
| feature, request, suggestion | FEATURE | Admin ID 1 |

## ⏰ Scheduled Tasks

### Daily Reminders (9:00 AM)
- Sends reminders for complaints pending > 3 days
- Won't spam: Only sends if no reminder in last 24 hours

### Auto-Close (2:00 AM)
- Closes resolved complaints inactive for 6 days
- Sends notification to user

## 🔧 Customization

### Change Admin Assignments
Edit `AutoAssignmentService.java`:
```java
categoryToAdminMap.put(ClaimCategory.BILLING, 3L); // Assign to Admin 3
```

### Add New Keywords
Edit `AutoAssignmentService.java`:
```java
keywordToCategoryMap.put("subscription", ClaimCategory.BILLING);
```

### Change Schedule Times
Edit `ComplaintScheduledTasks.java`:
```java
@Scheduled(cron = "0 0 10 * * *") // Change to 10 AM
```

### Change Auto-Close Period
Edit `ComplaintScheduledTasks.java`:
```java
LocalDateTime tenDaysAgo = LocalDateTime.now().minusDays(10); // Change to 10 days
```

## 🐛 Troubleshooting

### Emails Not Sending?
1. Check logs for errors: `Failed to send email`
2. Verify email credentials in `application.yml`
3. Test SMTP connection: `telnet smtp.gmail.com 587`
4. Check firewall/antivirus blocking port 587

### Auto-Assignment Not Working?
1. Check logs: Should see "Auto-detected category"
2. Verify keywords match your complaint text (case-insensitive)
3. Check admin IDs exist in database

### Scheduled Tasks Not Running?
1. Verify `@EnableScheduling` in main application class
2. Check logs for: "Starting scheduled task"
3. For testing, change cron to run every minute:
   ```java
   @Scheduled(cron = "0 * * * * *") // Every minute
   ```

## 📊 Monitoring

### Check Logs
```bash
tail -f complaints-service/logs/application.log
```

Look for:
- `Auto-detected category: BILLING`
- `Auto-assigned complaint with category BILLING to admin ID: 2`
- `Status change notification sent to: user@example.com`
- `Starting scheduled task: Send pending complaint reminders`

### Database Queries
```sql
-- Check auto-assigned complaints
SELECT id_reclamation, title, category, assigned_to_admin_id 
FROM complaints 
WHERE assigned_to_admin_id IS NOT NULL;

-- Check complaints needing reminders
SELECT id_reclamation, title, created_at, last_reminder_sent_at
FROM complaints 
WHERE claim_status IN ('Pending', 'Under_Review')
AND created_at < DATE_SUB(NOW(), INTERVAL 3 DAY);

-- Check complaints ready for auto-close
SELECT id_reclamation, title, updated_at
FROM complaints 
WHERE claim_status = 'Resolved'
AND updated_at < DATE_SUB(NOW(), INTERVAL 6 DAY);
```

## ✅ Verification Checklist

- [ ] Email credentials configured in `application.yml`
- [ ] Complaints service restarted
- [ ] Test complaint created with keyword (e.g., "payment")
- [ ] Category auto-detected in logs
- [ ] Admin ID assigned in database
- [ ] Status change email received
- [ ] Resolution note email received
- [ ] Scheduled tasks running (check logs at 9 AM and 2 AM)

## 🎯 Next Steps

1. Customize admin assignments for your team
2. Add more keywords for better categorization
3. Adjust reminder and auto-close schedules
4. Set up HTML email templates
5. Configure production email service (SendGrid/Mailgun)
6. Add monitoring and alerting
7. Implement email preferences per user

## 📚 Full Documentation

See `COMPLAINTS_AUTOMATION_SETUP.md` for detailed configuration options and production recommendations.
