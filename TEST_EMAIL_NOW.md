# Test Email Notifications - Quick Guide

## ✅ What Changed

The system now sends emails directly to the **authenticated user** (the person who is logged in and creating the complaint).

No need to fetch email from User service - it's passed directly from the frontend!

## 🚀 Test Now

### Step 1: Restart Services

**Restart Complaints Service:**
```bash
cd complaints-service
./mvnw spring-boot:run
```

**Restart Frontend (if needed):**
```bash
cd frontend
npm start
```

### Step 2: Create a Complaint

1. Go to: http://localhost:4200/complaints
2. Make sure you're **logged in**
3. Click "Add Complaint"
4. Fill in:
   - Title: "Test email notification"
   - Description: "Testing the email system"
   - Priority: High
5. Click "Submit"

### Step 3: Check Console Logs

You should see:
```
Current user loaded: 1 user@example.com
Email sent successfully to: user@example.com
```

### Step 4: Check Email

Check the email inbox of the **logged-in user**.

**Important:** Check the spam folder if you don't see it in inbox!

## 📧 What Email You'll Receive

**Subject:** Complaint Received - #[ID]

**Content:**
```
Dear User,

Thank you for submitting your complaint. We have received it and our team will review it shortly.

Complaint Details:
ID: [number]
Title: Test email notification
Description: Testing the email system
Priority: High
Status: Pending
Category: [auto-detected]

You will receive email notifications when there are updates to your complaint.

View your complaint: http://localhost:4200/complaints

Best regards,
Freelancity Support Team
```

**From:** cw.club102@gmail.com

## 🔍 Troubleshooting

### No email received?

1. **Check console logs** - Do you see "Email sent successfully"?

2. **Check spam folder** - Gmail often filters automated emails

3. **Verify user has email:**
   ```sql
   SELECT id, username, email FROM user WHERE id = [your_user_id];
   ```

4. **Test email directly:**
   ```
   http://localhost:8089/freelancity/report/test/send-email?email=YOUR_EMAIL@gmail.com
   ```

### Console shows "No email provided"?

The logged-in user doesn't have an email in the database.

**Fix:**
```sql
UPDATE user SET email = 'your@email.com' WHERE id = [user_id];
```

Then logout and login again.

### Console shows "Failed to send email"?

Check the error message. Common issues:
- Gmail credentials wrong
- Port 587 blocked by firewall
- Internet connection issue

## ✅ Success Checklist

- [ ] Complaints service restarted
- [ ] Frontend restarted (if needed)
- [ ] Logged in to the application
- [ ] Created a test complaint
- [ ] Console shows "Current user loaded: [id] [email]"
- [ ] Console shows "Email sent successfully to: [email]"
- [ ] Email received (check spam folder)

## 🎯 Expected Flow

```
1. User logs in
   ↓
2. Frontend gets user email from session
   ↓
3. User creates complaint
   ↓
4. Frontend sends: complaint data + user email
   ↓
5. Backend saves complaint
   ↓
6. Backend sends email to user
   ↓
7. User receives email notification
```

## 📞 Still Not Working?

Provide this information:

1. **Console output** after creating complaint
2. **User email check:**
   ```sql
   SELECT id, username, email FROM user WHERE id = 1;
   ```
3. **Test endpoint result:**
   ```
   http://localhost:8089/freelancity/report/test/send-email?email=cw.club102@gmail.com
   ```

---

**The system is now ready! Create a complaint and check your email!** 📧
