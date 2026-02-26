# Quick Email Setup Check

## 🔍 Diagnostic Steps

### Step 1: Test Email Configuration (MOST IMPORTANT)

**Open your browser and visit:**
```
http://localhost:8089/freelancity/report/test/send-email?email=cw.club102@gmail.com
```

**Expected Result:**
- Browser shows: "Test email sent successfully to: cw.club102@gmail.com"
- Check your email at cw.club102@gmail.com (including spam folder)

**If you see an error:**
- Copy the error message
- Check the complaints service console for detailed error logs

---

### Step 2: Check User Email in Database

The system sends emails to the user's email address stored in the database.

**Run this SQL query:**
```sql
SELECT id, username, email FROM user WHERE id = 1;
```

**Expected Result:**
```
id | username | email
1  | john     | john@example.com
```

**If email is NULL or empty:**
```sql
UPDATE user SET email = 'test@example.com' WHERE id = 1;
```

---

### Step 3: Check Application Logs

After creating a complaint, look for these messages in the console:

**✅ Success:**
```
Complaint creation notification sent to: user@example.com
```

**❌ Error:**
```
Failed to send email notification
Failed to fetch email for user ID: 1
Authentication failed
Connection timeout
```

---

### Step 4: Verify Services Are Running

**Check complaints service:**
```
http://localhost:8089/freelancity/report/test/email-config
```
Should return: "Email service is configured and ready"

**Check user service:**
```
http://localhost:8081/api/users/1
```
Should return user data with email field

---

## 🚨 Common Issues & Quick Fixes

### Issue 1: "Failed to fetch email for user ID"
**Cause:** User service not running or user has no email

**Fix:**
1. Start user service: `cd backend && ./mvnw spring-boot:run`
2. Or update user email in database (see Step 2)

### Issue 2: "Authentication failed"
**Cause:** Gmail app password incorrect

**Fix:**
1. Verify password in `application.yml`: `mejwmoqbngvmqktsi`
2. Regenerate app password at: https://myaccount.google.com/apppasswords

### Issue 3: No error but no email
**Cause:** Email might be in spam folder

**Fix:**
1. Check spam folder at cw.club102@gmail.com
2. Mark as "Not Spam" if found

### Issue 4: "Connection timeout"
**Cause:** Firewall blocking port 587

**Fix:**
1. Temporarily disable firewall/antivirus
2. Test: `telnet smtp.gmail.com 587`

---

## ✅ Quick Test Checklist

- [ ] Complaints service is running
- [ ] User service is running (port 8081)
- [ ] Test endpoint works: http://localhost:8089/freelancity/report/test/send-email?email=cw.club102@gmail.com
- [ ] User has email in database: `SELECT email FROM user WHERE id = 1;`
- [ ] Check spam folder at cw.club102@gmail.com
- [ ] No errors in application logs

---

## 📧 What Should Happen

### When User Creates Complaint:

1. User fills form at http://localhost:4200/complaints
2. Clicks "Submit"
3. Backend saves complaint
4. Backend fetches user email from database
5. Backend sends email via Gmail SMTP
6. User receives email at their registered email address

### Email Content:

```
Subject: Complaint Received - #123

Dear User,

Thank you for submitting your complaint. We have received it and our team will review it shortly.

Complaint Details:
ID: 123
Title: Payment issue
Description: Cannot process payment
Priority: High
Status: Pending
Category: BILLING

You will receive email notifications when there are updates to your complaint.

View your complaint: http://localhost:4200/complaints

Best regards,
Freelancity Support Team
```

---

## 🔧 Manual Test

### Test 1: Direct Email Test
```bash
curl "http://localhost:8089/freelancity/report/test/send-email?email=cw.club102@gmail.com"
```

### Test 2: Create Complaint via UI
1. Go to http://localhost:4200/complaints
2. Click "Add Complaint"
3. Fill in details
4. Submit
5. Check console logs
6. Check email inbox

### Test 3: Check Database
```sql
-- Check if complaint was created
SELECT * FROM complaints ORDER BY id_reclamation DESC LIMIT 1;

-- Check user email
SELECT id, username, email FROM user WHERE id = 1;
```

---

## 📞 Need Help?

**Provide this information:**

1. **Test endpoint result:**
   - Visit: http://localhost:8089/freelancity/report/test/send-email?email=cw.club102@gmail.com
   - What message do you see?

2. **User email in database:**
   ```sql
   SELECT id, username, email FROM user WHERE id = 1;
   ```
   - What is the result?

3. **Application logs:**
   - Copy any error messages from console after creating complaint

4. **Services status:**
   - Is complaints service running? (port 8089)
   - Is user service running? (port 8081)

---

## 🎯 Most Likely Issues

1. **User has no email in database** (90% of cases)
   - Fix: `UPDATE user SET email = 'test@example.com' WHERE id = 1;`

2. **User service not running** (5% of cases)
   - Fix: Start user service on port 8081

3. **Email in spam folder** (3% of cases)
   - Fix: Check spam folder

4. **Gmail app password wrong** (2% of cases)
   - Fix: Regenerate app password

---

**START HERE:** Test the email endpoint first!
```
http://localhost:8089/freelancity/report/test/send-email?email=cw.club102@gmail.com
```

This will immediately tell us if the email system is working.
