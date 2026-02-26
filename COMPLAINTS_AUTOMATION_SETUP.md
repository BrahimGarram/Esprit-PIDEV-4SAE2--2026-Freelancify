# Complaints Automation & Email Notifications Setup

## Features Implemented

### 1. **Email Notifications**
- ✅ Status change notifications
- ✅ Admin response notifications (when resolution note is added)
- ✅ Pending complaint reminders
- ✅ Auto-close notifications
- ✅ Admin assignment notifications

### 2. **Auto-Assignment System**
- ✅ Auto-categorization based on keywords in title/description
- ✅ Auto-assignment to admins based on category
- ✅ Configurable category-to-admin mapping

### 3. **Scheduled Tasks**
- ✅ Daily reminders for pending complaints (runs at 9 AM)
- ✅ Auto-close resolved complaints after 6 days of inactivity (runs at 2 AM)

### 4. **Categories**
- TECHNICAL - Technical issues, errors, bugs
- BILLING - Payment and billing issues
- SERVICE - Service quality issues
- ACCOUNT - Account-related issues
- FEATURE - Feature requests
- BUG - Bug reports
- OTHER - Other issues

## Database Changes

New fields added to `Complaints` table:
- `category` (ENUM) - Complaint category
- `assigned_to_admin_id` (BIGINT) - ID of assigned admin
- `last_reminder_sent_at` (DATETIME) - Last reminder email timestamp

## Email Configuration

### Gmail Setup (Recommended for Testing)

1. **Enable 2-Factor Authentication** on your Gmail account

2. **Generate App Password**:
   - Go to Google Account Settings
   - Security → 2-Step Verification → App passwords
   - Generate a new app password for "Mail"

3. **Update `application.yml`**:
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-16-digit-app-password
```

### Alternative Email Providers

#### SendGrid
```yaml
spring:
  mail:
    host: smtp.sendgrid.net
    port: 587
    username: apikey
    password: your-sendgrid-api-key
```

#### Mailgun
```yaml
spring:
  mail:
    host: smtp.mailgun.org
    port: 587
    username: postmaster@your-domain.mailgun.org
    password: your-mailgun-password
```

#### AWS SES
```yaml
spring:
  mail:
    host: email-smtp.us-east-1.amazonaws.com
    port: 587
    username: your-smtp-username
    password: your-smtp-password
```

## Auto-Assignment Configuration

### Keyword Mapping
Keywords are automatically detected in complaint title/description:

**Technical**: error, bug, crash, not working, broken, issue, problem
**Billing**: payment, invoice, charge, refund, billing, price
**Account**: login, password, account, profile, access
**Service**: service, quality, support, response
**Feature**: feature, request, suggestion, improvement

### Admin Assignment
Edit `AutoAssignmentService.java` to configure which admin handles which category:

```java
categoryToAdminMap.put(ClaimCategory.TECHNICAL, 1L);  // Admin ID 1
categoryToAdminMap.put(ClaimCategory.BILLING, 2L);    // Admin ID 2
// etc...
```

## Scheduled Tasks Configuration

### Reminder Schedule
Default: Every day at 9:00 AM
- Sends reminders for complaints pending > 3 days
- Won't send if reminder was sent in last 24 hours

To change schedule, edit `ComplaintScheduledTasks.java`:
```java
@Scheduled(cron = "0 0 9 * * *") // Format: second minute hour day month weekday
```

### Auto-Close Schedule
Default: Every day at 2:00 AM
- Auto-closes resolved complaints inactive for 6 days

To change schedule or inactivity period:
```java
@Scheduled(cron = "0 0 2 * * *")
LocalDateTime sixDaysAgo = LocalDateTime.now().minusDays(6); // Change 6 to desired days
```

## Testing

### Test Email Notifications
1. Create a complaint
2. Admin changes status → User receives email
3. Admin adds resolution note → User receives email

### Test Auto-Assignment
1. Create complaint with title "Payment issue"
2. Check database - should be categorized as BILLING
3. Check assigned_to_admin_id - should match BILLING admin

### Test Scheduled Tasks (Manual Trigger)
For testing, you can temporarily change cron expressions:
```java
@Scheduled(fixedDelay = 60000) // Run every 60 seconds
```

## Troubleshooting

### Emails Not Sending
1. Check email credentials in `application.yml`
2. Check firewall/antivirus blocking SMTP port 587
3. Check application logs for email errors
4. Verify "Less secure app access" or App Password is configured

### Auto-Assignment Not Working
1. Check logs for auto-detection messages
2. Verify keywords match your complaint text
3. Check admin IDs exist in user database

### Scheduled Tasks Not Running
1. Verify `@EnableScheduling` is present in main application class
2. Check application logs for scheduled task execution
3. Verify cron expression syntax

## Production Recommendations

1. **Use Professional Email Service**: SendGrid, Mailgun, or AWS SES
2. **Configure Email Templates**: Use HTML templates instead of plain text
3. **Add Email Queue**: Use message queue (RabbitMQ/Kafka) for reliability
4. **Monitor Email Delivery**: Track bounces and failures
5. **Add Unsubscribe Option**: Allow users to opt-out of notifications
6. **Rate Limiting**: Prevent email spam
7. **Database Indexes**: Add indexes on status, createdAt, updatedAt fields
8. **Logging**: Implement proper logging for all automated actions

## Future Enhancements

- [ ] HTML email templates with branding
- [ ] Email preferences per user
- [ ] SMS notifications for urgent complaints
- [ ] Slack/Teams integration
- [ ] Machine learning for better categorization
- [ ] Admin workload balancing
- [ ] Escalation rules
- [ ] SLA tracking and alerts
