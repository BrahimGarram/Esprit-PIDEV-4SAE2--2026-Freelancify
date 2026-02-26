# Soft Delete - Testing Checklist

## ✅ Pre-Testing Setup

- [ ] Backend service is running on port 8089
- [ ] Frontend is running on port 4200
- [ ] MySQL database is running
- [ ] User is logged in
- [ ] Database has `is_visible` column in `complaints` table

## 🧪 Test Scenarios

### 1. Create Claim Test
- [ ] Navigate to complaints page
- [ ] Fill in claim form
- [ ] Submit claim
- [ ] **Expected**: Claim appears in list
- [ ] **Verify in DB**: `is_visible = true`

```sql
SELECT id_reclamation, title, is_visible 
FROM complaints 
WHERE user_id = YOUR_USER_ID 
ORDER BY created_at DESC 
LIMIT 1;
```

### 2. View Claims Test
- [ ] Load complaints page
- [ ] **Expected**: Only visible claims shown
- [ ] **Verify**: No claims with `is_visible = false` appear

### 3. Soft Delete Test
- [ ] Click on a claim to open modal
- [ ] Click "Delete Claim" button
- [ ] Confirm deletion in dialog
- [ ] **Expected**: 
  - [ ] Success toast appears
  - [ ] Modal closes
  - [ ] Claim disappears from list
  - [ ] Other claims still visible

### 4. Database Verification Test
After deleting a claim:

```sql
-- Check the deleted claim still exists
SELECT id_reclamation, title, is_visible, updated_at
FROM complaints 
WHERE id_reclamation = DELETED_CLAIM_ID;

-- Expected result:
-- is_visible = false (not deleted from DB)
-- updated_at = recent timestamp
```

- [ ] Record exists in database
- [ ] `is_visible = false`
- [ ] `updated_at` is recent

### 5. Cannot Access Deleted Claim Test
- [ ] Try to access deleted claim via API:
```bash
GET /freelancity/report/retrieve-claim/{deleted-claim-id}?userId={user-id}
```
- [ ] **Expected**: Returns `null` or 404
- [ ] User cannot view deleted claim

### 6. List Claims Excludes Deleted Test
```bash
GET /freelancity/report/retrieve-all-complaints?userId={user-id}
```
- [ ] **Expected**: Deleted claims not in response
- [ ] Only visible claims returned

### 7. Cannot Edit Deleted Claim Test
- [ ] Try to update a deleted claim:
```bash
PUT /freelancity/report/update-claim?userId={user-id}
{
  "idReclamation": DELETED_CLAIM_ID,
  "title": "Updated Title",
  ...
}
```
- [ ] **Expected**: Update fails or returns null
- [ ] Deleted claim cannot be modified

### 8. Multiple Deletes Test
- [ ] Create 3 claims
- [ ] Delete claim #1
- [ ] Delete claim #3
- [ ] **Expected**: 
  - [ ] Only claim #2 visible to user
  - [ ] All 3 claims exist in database
  - [ ] Claims #1 and #3 have `is_visible = false`

### 9. Different User Test
- [ ] User A creates and deletes a claim
- [ ] Login as User B
- [ ] **Expected**: User B cannot see User A's deleted claim
- [ ] **Verify**: Proper user isolation

### 10. Audit Trail Test
```sql
-- View all claims including deleted ones
SELECT 
    id_reclamation,
    user_id,
    title,
    is_visible,
    created_at,
    updated_at,
    CASE 
        WHEN is_visible = true THEN 'ACTIVE'
        ELSE 'DELETED'
    END as status
FROM complaints
WHERE user_id = YOUR_USER_ID
ORDER BY created_at DESC;
```
- [ ] Can see complete history
- [ ] Deleted claims marked appropriately
- [ ] Timestamps show when deleted

## 🔍 Edge Cases

### 11. Delete Non-Existent Claim
```bash
DELETE /freelancity/report/drop-claim/99999?userId={user-id}
```
- [ ] **Expected**: Graceful error handling
- [ ] No database errors

### 12. Delete Another User's Claim
```bash
DELETE /freelancity/report/drop-claim/{other-user-claim-id}?userId={your-user-id}
```
- [ ] **Expected**: 403 Forbidden or no action
- [ ] Claim not deleted
- [ ] Security check works

### 13. Delete Already Deleted Claim
- [ ] Delete a claim
- [ ] Try to delete it again
- [ ] **Expected**: Graceful handling
- [ ] No errors

### 14. Create After Delete
- [ ] Delete all claims
- [ ] Create new claim
- [ ] **Expected**: 
  - [ ] New claim visible
  - [ ] `is_visible = true`
  - [ ] Old deleted claims still hidden

## 📊 Performance Tests

### 15. Query Performance Test
```sql
-- Should use index
EXPLAIN SELECT * FROM complaints 
WHERE user_id = 1 AND is_visible = true;

-- Check if index is used
-- Expected: Uses idx_complaints_user_visible
```
- [ ] Query uses index
- [ ] Fast response time

### 16. Large Dataset Test
- [ ] Create 100 claims
- [ ] Delete 50 claims
- [ ] Load claims page
- [ ] **Expected**: 
  - [ ] Only 50 visible claims shown
  - [ ] Fast load time
  - [ ] No performance issues

## 🔐 Security Tests

### 17. JWT Token Test
- [ ] Try to delete without authentication
- [ ] **Expected**: 401 Unauthorized

### 18. Authorization Test
- [ ] Try to delete claim owned by another user
- [ ] **Expected**: 403 Forbidden or no action

### 19. SQL Injection Test
```bash
DELETE /freelancity/report/drop-claim/1' OR '1'='1?userId=1
```
- [ ] **Expected**: Proper parameter handling
- [ ] No SQL injection vulnerability

## 🎯 Integration Tests

### 20. Full Workflow Test
- [ ] Create claim with attachment
- [ ] View claim details
- [ ] Edit claim
- [ ] Delete claim
- [ ] **Expected**: All operations work correctly
- [ ] Claim properly soft deleted

### 21. Frontend-Backend Integration
- [ ] Frontend shows correct claims
- [ ] Delete button works
- [ ] Modal closes after delete
- [ ] List refreshes correctly
- [ ] Toast notifications appear

## 📱 UI/UX Tests

### 22. User Feedback Test
- [ ] Delete claim
- [ ] **Expected**:
  - [ ] Confirmation dialog appears
  - [ ] Success toast after delete
  - [ ] Smooth animation
  - [ ] No UI glitches

### 23. Responsive Design Test
- [ ] Test on desktop
- [ ] Test on tablet
- [ ] Test on mobile
- [ ] **Expected**: Delete works on all devices

## 🔄 Rollback Tests

### 24. Database Rollback Test
```sql
-- Manually restore a deleted claim
UPDATE complaints 
SET is_visible = true 
WHERE id_reclamation = DELETED_CLAIM_ID;
```
- [ ] Claim reappears in user's list
- [ ] Can be accessed normally

### 25. Admin Restore Test (Future Feature)
- [ ] Admin views deleted claims
- [ ] Admin restores claim
- [ ] **Expected**: Claim visible to user again

## 📝 Documentation Tests

### 26. API Documentation Test
- [ ] API endpoints documented
- [ ] Soft delete behavior explained
- [ ] Examples provided

### 27. Code Comments Test
- [ ] Service methods commented
- [ ] Repository methods documented
- [ ] Controller endpoints explained

## ✅ Final Verification

### 28. Complete System Test
- [ ] All CRUD operations work
- [ ] Soft delete functioning
- [ ] No hard deletes occurring
- [ ] Audit trail maintained
- [ ] Performance acceptable
- [ ] Security measures in place
- [ ] User experience smooth

## 🎉 Test Results Summary

```
Total Tests: 28
Passed: ___
Failed: ___
Skipped: ___

Pass Rate: ____%
```

## 🐛 Issues Found

| Test # | Issue Description | Severity | Status |
|--------|------------------|----------|--------|
|        |                  |          |        |
|        |                  |          |        |

## 📊 Performance Metrics

| Metric | Target | Actual | Status |
|--------|--------|--------|--------|
| Delete Response Time | < 500ms | ___ ms | ⏱️ |
| List Load Time | < 1s | ___ s | ⏱️ |
| Database Query Time | < 100ms | ___ ms | ⏱️ |

## ✅ Sign-Off

- [ ] All critical tests passed
- [ ] No blocking issues
- [ ] Performance acceptable
- [ ] Security verified
- [ ] Ready for production

**Tested By**: _______________  
**Date**: _______________  
**Signature**: _______________

---

## 🚀 Quick Test Commands

### Create Test Claim
```bash
curl -X POST "http://localhost:8089/freelancity/report/create-claim?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Claim",
    "description": "Test Description",
    "claimPriority": "Medium"
  }'
```

### List Claims
```bash
curl "http://localhost:8089/freelancity/report/retrieve-all-complaints?userId=1"
```

### Delete Claim
```bash
curl -X DELETE "http://localhost:8089/freelancity/report/drop-claim/1?userId=1"
```

### Check Database
```sql
-- View all claims with visibility status
SELECT 
    id_reclamation,
    title,
    is_visible,
    CASE 
        WHEN is_visible = true THEN '✅ VISIBLE'
        ELSE '❌ DELETED'
    END as status
FROM complaints
ORDER BY created_at DESC;
```

---

**Happy Testing! 🧪**
