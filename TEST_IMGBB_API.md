# Testing ImgBB API

## Your API Key
```
9c862141b3c163e8a8e11109ce021ff3
```

## Test with cURL

### Test 1: Upload Base64 Image
```bash
curl --location --request POST "https://api.imgbb.com/1/upload?key=9c862141b3c163e8a8e11109ce021ff3" \
--form "image=R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"
```

### Test 2: Upload Image File
```bash
# Replace /path/to/image.jpg with actual image path
curl --location --request POST "https://api.imgbb.com/1/upload?key=9c862141b3c163e8a8e11109ce021ff3" \
--form "image=@/path/to/image.jpg"
```

### Test 3: Upload with Expiration (10 minutes)
```bash
curl --location --request POST "https://api.imgbb.com/1/upload?expiration=600&key=9c862141b3c163e8a8e11109ce021ff3" \
--form "image=R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"
```

## Expected Response

### Success Response
```json
{
  "data": {
    "id": "2ndCYJK",
    "title": "c1f64245afb2",
    "url_viewer": "https://ibb.co/2ndCYJK",
    "url": "https://i.ibb.co/w04Prt6/c1f64245afb2.gif",
    "display_url": "https://i.ibb.co/98W13PY/c1f64245afb2.gif",
    "width": "1",
    "height": "1",
    "size": "43",
    "time": "1552042565",
    "expiration": "0",
    "image": {
      "filename": "c1f64245afb2.gif",
      "name": "c1f64245afb2",
      "mime": "image/gif",
      "extension": "gif",
      "url": "https://i.ibb.co/w04Prt6/c1f64245afb2.gif"
    },
    "thumb": {
      "filename": "c1f64245afb2.gif",
      "name": "c1f64245afb2",
      "mime": "image/gif",
      "extension": "gif",
      "url": "https://i.ibb.co/2ndCYJK/c1f64245afb2.gif"
    },
    "delete_url": "https://ibb.co/2ndCYJK/670a7e48ddcb85ac340c717a41047e5c"
  },
  "success": true,
  "status": 200
}
```

### Error Response
```json
{
  "status_code": 400,
  "status_txt": "Bad Request",
  "error": {
    "message": "Empty upload source",
    "code": 130
  }
}
```

## Common Error Codes

| Code | Message | Solution |
|------|---------|----------|
| 100 | Invalid API key | Check your API key is correct |
| 110 | Invalid image | Ensure file is a valid image format |
| 130 | Empty upload source | Make sure image data is provided |
| 310 | File too large | Image must be under 32 MB |

## Testing in Browser Console

Open your browser console and run:

```javascript
// Test ImgBB API
const testImgBB = async () => {
  const apiKey = '9c862141b3c163e8a8e11109ce021ff3';
  const base64Image = 'R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7';
  
  const formData = new FormData();
  formData.append('image', base64Image);
  
  try {
    const response = await fetch(`https://api.imgbb.com/1/upload?key=${apiKey}`, {
      method: 'POST',
      body: formData
    });
    
    const data = await response.json();
    console.log('ImgBB Response:', data);
    
    if (data.success) {
      console.log('✅ Upload successful!');
      console.log('Image URL:', data.data.url);
      console.log('Display URL:', data.data.display_url);
    } else {
      console.error('❌ Upload failed:', data.error);
    }
  } catch (error) {
    console.error('❌ Request failed:', error);
  }
};

testImgBB();
```

## Testing with Actual Image File

```javascript
// Test with file input
const testFileUpload = async (file) => {
  const apiKey = '9c862141b3c163e8a8e11109ce021ff3';
  
  const formData = new FormData();
  formData.append('image', file);
  
  try {
    const response = await fetch(`https://api.imgbb.com/1/upload?key=${apiKey}`, {
      method: 'POST',
      body: formData
    });
    
    const data = await response.json();
    console.log('ImgBB Response:', data);
    
    if (data.success) {
      console.log('✅ Upload successful!');
      console.log('Image URL:', data.data.url);
      console.log('Display URL:', data.data.display_url);
      console.log('Viewer URL:', data.data.url_viewer);
    } else {
      console.error('❌ Upload failed:', data.error);
    }
  } catch (error) {
    console.error('❌ Request failed:', error);
  }
};

// Usage: Select a file and call testFileUpload(file)
```

## Verify API Key

Visit: https://api.imgbb.com/

1. Login with your account
2. Check your API key matches: `9c862141b3c163e8a8e11109ce021ff3`
3. Verify your account is active
4. Check if there are any rate limits

## Rate Limits

ImgBB Free Tier:
- Unlimited uploads
- Up to 32 MB per image
- No bandwidth limits
- No expiration (unless specified)

## Troubleshooting

### Issue: "Invalid API key"
**Solution**: 
- Verify API key is correct
- Check for extra spaces or characters
- Ensure you're logged into the correct account

### Issue: "Empty upload source"
**Solution**:
- Ensure image file is selected
- Check file is not corrupted
- Verify file is a valid image format

### Issue: "File too large"
**Solution**:
- Compress image before upload
- Maximum size is 32 MB
- Use image optimization tools

### Issue: CORS Error
**Solution**:
- ImgBB API supports CORS
- Check browser console for details
- Ensure request format is correct

## Angular Implementation Check

The current implementation in `complaints.service.ts`:

```typescript
uploadImageToImgBB(file: File): Observable<any> {
  const formData = new FormData();
  formData.append('image', file);
  
  // Add API key as query parameter
  const params = new HttpParams().set('key', this.imgbbApiKey);
  
  return this.http.post(this.imgbbApiUrl, formData, { params });
}
```

This should work correctly with your API key: `9c862141b3c163e8a8e11109ce021ff3`

## Debug Steps

1. **Check Console Logs**
   - Open browser DevTools (F12)
   - Go to Console tab
   - Look for ImgBB upload logs

2. **Check Network Tab**
   - Open DevTools Network tab
   - Filter by "upload"
   - Check request/response details

3. **Verify Request Format**
   - Request URL should be: `https://api.imgbb.com/1/upload?key=9c862141b3c163e8a8e11109ce021ff3`
   - Method: POST
   - Content-Type: multipart/form-data
   - Body: FormData with 'image' field

4. **Check Response**
   - Status: 200 OK
   - Response body should have `success: true`
   - `data.url` should contain image URL

## Quick Test

Run this in your browser console on the complaints page:

```javascript
// Quick test
fetch('https://api.imgbb.com/1/upload?key=9c862141b3c163e8a8e11109ce021ff3', {
  method: 'POST',
  body: (() => {
    const fd = new FormData();
    fd.append('image', 'R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7');
    return fd;
  })()
})
.then(r => r.json())
.then(d => console.log('Result:', d))
.catch(e => console.error('Error:', e));
```

Expected output:
```
Result: { success: true, data: { url: "...", ... }, status: 200 }
```

If you see this, your API key is working! ✅
