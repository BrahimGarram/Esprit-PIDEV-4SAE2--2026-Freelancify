# Quick Start Guide - Complaints Feature

## 🚀 What's Been Fixed

✅ **Edit Functionality** - Now works correctly  
✅ **Delete Functionality** - Now works correctly  
✅ **Download Button** - Now works correctly  
✅ **ImgBB Integration** - Images uploaded to CDN  

## 📋 Setup Steps

### 1. Get ImgBB API Key (Optional but Recommended)

```bash
# Visit: https://api.imgbb.com/
# Sign up and get your free API key
```

### 2. Configure API Key

Edit `frontend/src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  imgbbApiKey: 'PASTE_YOUR_API_KEY_HERE', // ← Replace this
  // ... rest of config
};
```

### 3. Install Dependencies (if needed)

```bash
cd frontend
npm install
```

### 4. Start the Application

```bash
# Terminal 1 - Backend (Complaints Service)
cd complaints-service
mvn spring-boot:run

# Terminal 2 - Frontend
cd frontend
npm start
```

### 5. Test the Features

1. **Navigate to Complaints Page**
   - Login to the application
   - Go to `/complaints` route

2. **Create a Claim**
   - Fill in the form
   - Optionally attach an image (will upload to ImgBB)
   - Click "Submit Claim"

3. **View Claim Details**
   - Click on any claim in the list
   - Modal opens with full details

4. **Edit a Claim**
   - Open claim details
   - Click "Edit Claim"
   - Modify fields
   - Click "Save Changes"

5. **Delete a Claim**
   - Open claim details
   - Click "Delete Claim"
   - Confirm deletion

6. **Download Attachment**
   - Open claim details
   - Click "Download Attachment" button
   - File opens/downloads

## 🔍 Troubleshooting

### Edit/Delete Not Working?

**Check:**
- Backend is running on port 8089
- User is logged in
- User owns the claim (can only edit/delete own claims)
- Check browser console for errors

**Solution:**
```bash
# Restart backend
cd complaints-service
mvn clean spring-boot:run
```

### Download Button Not Working?

**Check:**
- File URL in database is correct
- Backend serves files from `/uploads/` directory
- CORS is configured correctly

**Solution:**
- Files should be accessible at: `http://localhost:8089/uploads/claims/filename.ext`
- Check `complaints-service/uploads/claims/` directory exists

### ImgBB Upload Failing?

**Check:**
- API key is correct in `environment.ts`
- Internet connection is working
- Image is under 32 MB

**Solution:**
- App automatically falls back to local storage if ImgBB fails
- Check browser console for error messages
- Verify API key at https://api.imgbb.com/

## 📱 Features Overview

### Modal Display
- ✅ Claim ID, Title, Priority, Status
- ✅ Full description
- ✅ Timeline (Created, Updated, Resolved)
- ✅ Resolution note
- ✅ Attachment details (name, type, size, date)
- ✅ Download button

### Actions
- ✅ Edit (title, description, priority, status)
- ✅ Delete (with confirmation)
- ✅ Download attachment
- ✅ Close modal

### UI/UX
- ✅ Smooth animations
- ✅ Loading states
- ✅ Toast notifications
- ✅ Responsive design
- ✅ Color-coded badges

## 🎯 API Endpoints

```
Base URL: http://localhost:8089/freelancity/report

GET    /retrieve-all-complaints?userId={userId}
POST   /create-claim (multipart/form-data)
PUT    /update-claim?userId={userId}
DELETE /drop-claim/{claim-id}?userId={userId}
```

## 💡 Tips

1. **Image Files**: Automatically uploaded to ImgBB CDN
2. **Other Files**: Stored locally on backend server
3. **File Size**: Max 32 MB for images
4. **Permissions**: Users can only edit/delete their own claims
5. **Confirmation**: Delete requires confirmation to prevent accidents

## 🐛 Common Issues

### "Failed to update claim"
- Ensure you own the claim
- Check backend logs for errors
- Verify request format matches backend expectations

### "Failed to delete claim"
- Ensure you own the claim
- Check if claim exists
- Verify backend endpoint is `/drop-claim/` not `/delete-claim/`

### "Download not working"
- Check file exists in `uploads/claims/` directory
- Verify file URL in database
- Check backend serves static files from `/uploads/`

## 📞 Need Help?

1. Check browser console for errors
2. Check backend logs
3. Verify all services are running
4. Check database for data integrity
5. Review `COMPLAINTS_FEATURE_SUMMARY.md` for detailed info

## ✨ What's Next?

Consider adding:
- [ ] Image preview in modal
- [ ] Multiple file attachments
- [ ] Drag-and-drop upload
- [ ] Real-time updates
- [ ] Claim history/audit log
- [ ] Admin response system

---

**Happy Coding! 🎉**
