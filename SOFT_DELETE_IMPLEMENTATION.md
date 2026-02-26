# Soft Delete Implementation - Complaints Feature

## 🎯 Overview

The complaints feature now implements a **soft delete** pattern instead of hard deleting records from the database. When a user "deletes" a claim, it remains in the database but is hidden from view.

## 🔍 What is Soft Delete?

**Soft Delete** is a data management pattern where records are marked as deleted rather than being physically removed from the database. This provides several benefits:

- ✅ **Data Recovery**: Deleted records can be restored if needed
- ✅ **Audit Trail**: Maintain complete history of all claims
- ✅ **Compliance**: Meet regulatory requirements for data retention
- ✅ **Analytics**: Analyze deleted claims for insights
- ✅ **Referential Integrity**: Avoid breaking foreign key relationships

## 🏗️ Implementation Details

### Database Schema

```sql
ALTER TABLE complaints 
ADD COLUMN is_visible BOOLEAN DEFAULT TRUE;
```

### Entity Changes

**Complaints.java**
```java
@Entity
public class Complaints {
    // ... other fields ...
    
    private boolean isVisible;  // ← New field
    
    // Getters and setters
}
```

### Repository Changes

**ComplaintsRepository.java**
```java
@Repository
public interface ComplaintsRepository extends JpaRepository<Complaints, Long> {
    
    // Find only visible complaints for a user
    List<Complaints> findByUserIdAndIsVisibleTrue(Long userId);
    
    // Find visible complaint for a user
    Optional<Complaints> findByIdReclamationAndUserIdAndIsVisibleTrue(
        Long idReclamation, Long userId
    );
}
```

### Service Changes

**ComplaintsService.java**

#### 1. Create Claim - Set isVisible to true
```java
@Override
public Complaints addClaim(Complaints complaints) {
    // ... other initialization ...
    
    // Set isVisible to true by default
    complaints.setVisible(true);
    
    return cr.save(complaints);
}
```

#### 2. Retrieve Claims - Filter by isVisible
```java
@Override
public List<Complaints> retrieveComplaintsByUser(Long userId) {
    // Only return visible complaints
    return cr.findByUserIdAndIsVisibleTrue(userId);
}

@Override
public Optional<Complaints> retrieveClaimForUser(Long id, Long userId) {
    // Only return if visible
    return cr.findByIdReclamationAndUserIdAndIsVisibleTrue(id, userId);
}
```

#### 3. Delete Claim - Soft delete by setting isVisible to false
```java
@Override
public void deleteComplaint(Long id) {
    // Soft delete: set isVisible to false instead of actually deleting
    Optional<Complaints> complaintOpt = cr.findById(id);
    if (complaintOpt.isPresent()) {
        Complaints complaint = complaintOpt.get();
        complaint.setVisible(false);
        complaint.setUpdatedAt(new Date());
        cr.save(complaint);
    }
}
```

### Controller Changes

**ComplaintsController.java**

```java
@GetMapping("/retrieve-all-complaints")
public List<Complaints> retrieveAllComplaints(@RequestParam("userId") Long userId) {
    // Only return visible complaints
    return ic.retrieveComplaintsByUser(userId);
}

@DeleteMapping("/drop-claim/{claim-id}")
void deleteClaim(@PathVariable("claim-id") Long claimId,
                 @RequestParam("userId") Long userId) {
    // Verify ownership and soft delete
    Optional<Complaints> existing = ic.retrieveClaimForUser(claimId, userId);
    if (existing.isPresent()) {
        ic.deleteComplaint(claimId);  // Soft delete
    }
}
```

## 🔄 Data Flow

### Before Soft Delete (Hard Delete)
```
User clicks "Delete"
    │
    ▼
Confirm deletion
    │
    ▼
DELETE FROM complaints WHERE id = ?
    │
    ▼
Record permanently removed
    │
    ▼
Cannot be recovered ❌
```

### After Soft Delete
```
User clicks "Delete"
    │
    ▼
Confirm deletion
    │
    ▼
UPDATE complaints SET is_visible = false WHERE id = ?
    │
    ▼
Record still exists in database
    │
    ▼
Hidden from user's view
    │
    ▼
Can be recovered by admin ✅
```

## 📊 Database State Examples

### Before Delete
```sql
SELECT * FROM complaints WHERE user_id = 1;

| id | user_id | title          | is_visible |
|----|---------|----------------|------------|
| 1  | 1       | Payment issue  | true       |
| 2  | 1       | Late delivery  | true       |
| 3  | 1       | Poor quality   | true       |
```

### After User Deletes Claim #2
```sql
SELECT * FROM complaints WHERE user_id = 1;

| id | user_id | title          | is_visible |
|----|---------|----------------|------------|
| 1  | 1       | Payment issue  | true       |
| 2  | 1       | Late delivery  | false      | ← Still in DB
| 3  | 1       | Poor quality   | true       |
```

### What User Sees (Frontend Query)
```sql
SELECT * FROM complaints 
WHERE user_id = 1 AND is_visible = true;

| id | user_id | title          | is_visible |
|----|---------|----------------|------------|
| 1  | 1       | Payment issue  | true       |
| 3  | 1       | Poor quality   | true       |
```

## 🔐 Security Considerations

### User Permissions
- ✅ Users can only soft delete their own claims
- ✅ Deleted claims are completely hidden from the user
- ✅ Users cannot access deleted claims via API

### Admin Access (Future Enhancement)
```java
// Admin can view all claims including deleted ones
@GetMapping("/admin/all-complaints")
@PreAuthorize("hasRole('ADMIN')")
public List<Complaints> getAllComplaintsIncludingDeleted() {
    return cr.findAll();  // Returns all, including isVisible=false
}

// Admin can restore deleted claims
@PutMapping("/admin/restore-claim/{id}")
@PreAuthorize("hasRole('ADMIN')")
public Complaints restoreClaim(@PathVariable Long id) {
    Optional<Complaints> complaintOpt = cr.findById(id);
    if (complaintOpt.isPresent()) {
        Complaints complaint = complaintOpt.get();
        complaint.setVisible(true);
        return cr.save(complaint);
    }
    return null;
}
```

## 📈 Benefits

### 1. Data Recovery
```java
// Restore a deleted claim
public Complaints restoreClaim(Long claimId) {
    Optional<Complaints> complaint = cr.findById(claimId);
    if (complaint.isPresent()) {
        Complaints c = complaint.get();
        c.setVisible(true);
        return cr.save(c);
    }
    return null;
}
```

### 2. Audit Trail
```sql
-- View all claims including deleted ones (admin only)
SELECT 
    id,
    title,
    created_at,
    updated_at,
    is_visible,
    CASE 
        WHEN is_visible = false THEN 'DELETED'
        ELSE 'ACTIVE'
    END as status
FROM complaints
WHERE user_id = 1
ORDER BY created_at DESC;
```

### 3. Analytics
```sql
-- Analyze deletion patterns
SELECT 
    DATE(updated_at) as deletion_date,
    COUNT(*) as deleted_count
FROM complaints
WHERE is_visible = false
GROUP BY DATE(updated_at)
ORDER BY deletion_date DESC;

-- Most common reasons for deletion
SELECT 
    claim_status,
    COUNT(*) as deleted_count
FROM complaints
WHERE is_visible = false
GROUP BY claim_status;
```

## 🧪 Testing

### Test Scenarios

#### 1. Create and Delete Claim
```bash
# Create claim
POST /freelancity/report/create-claim?userId=1
{
  "title": "Test Claim",
  "description": "Test Description",
  "claimPriority": "Medium"
}

# Response: { "idReclamation": 123, "isVisible": true, ... }

# Delete claim (soft delete)
DELETE /freelancity/report/drop-claim/123?userId=1

# Response: 204 No Content

# Try to retrieve deleted claim
GET /freelancity/report/retrieve-claim/123?userId=1

# Response: null (claim is hidden)
```

#### 2. Verify Database State
```sql
-- Check if claim still exists in database
SELECT * FROM complaints WHERE id_reclamation = 123;

-- Result: Record exists with is_visible = false
```

#### 3. List User Claims
```bash
# Get all visible claims
GET /freelancity/report/retrieve-all-complaints?userId=1

# Response: Array of claims (deleted claim not included)
```

## 🔧 Migration Guide

### For Existing Data

If you have existing complaints without the `isVisible` field:

```sql
-- Set all existing complaints to visible
UPDATE complaints 
SET is_visible = true 
WHERE is_visible IS NULL;

-- Make the column NOT NULL with default value
ALTER TABLE complaints 
MODIFY COLUMN is_visible BOOLEAN NOT NULL DEFAULT TRUE;
```

## 📝 Frontend Changes

The frontend automatically handles soft delete:

```typescript
// Frontend service (no changes needed)
deleteClaim(claimId: number, userId: number): Observable<void> {
  const params = new HttpParams().set('userId', userId.toString());
  return this.http.delete<void>(
    `${this.apiUrl}/drop-claim/${claimId}`, 
    { params }
  );
}

// Backend handles soft delete transparently
// User sees claim disappear from list
// But it's still in database with isVisible=false
```

## 🎯 Best Practices

### 1. Always Filter by isVisible
```java
// ✅ Good - Filters by isVisible
List<Complaints> findByUserIdAndIsVisibleTrue(Long userId);

// ❌ Bad - Returns all including deleted
List<Complaints> findByUserId(Long userId);
```

### 2. Update Timestamp on Soft Delete
```java
complaint.setVisible(false);
complaint.setUpdatedAt(new Date());  // ← Track when deleted
cr.save(complaint);
```

### 3. Add Deleted By Field (Optional)
```java
@Entity
public class Complaints {
    // ... other fields ...
    
    private boolean isVisible;
    private Long deletedBy;  // User ID who deleted
    private Date deletedAt;  // When deleted
}
```

### 4. Implement Restore Functionality
```java
public Complaints restoreClaim(Long claimId, Long userId) {
    Optional<Complaints> opt = cr.findByIdReclamationAndUserId(claimId, userId);
    if (opt.isPresent()) {
        Complaints c = opt.get();
        c.setVisible(true);
        c.setDeletedBy(null);
        c.setDeletedAt(null);
        return cr.save(c);
    }
    return null;
}
```

## 🚀 Future Enhancements

### 1. Admin Dashboard
- View all deleted claims
- Restore deleted claims
- Permanently delete old claims

### 2. Automatic Cleanup
```java
@Scheduled(cron = "0 0 0 * * ?")  // Daily at midnight
public void cleanupOldDeletedClaims() {
    LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);
    List<Complaints> oldDeleted = cr.findByIsVisibleFalseAndUpdatedAtBefore(cutoffDate);
    cr.deleteAll(oldDeleted);  // Hard delete after 90 days
}
```

### 3. Recycle Bin Feature
- User can view their deleted claims
- User can restore within 30 days
- Auto-delete after 30 days

## 📊 Performance Considerations

### Index Recommendations
```sql
-- Add index for better query performance
CREATE INDEX idx_complaints_user_visible 
ON complaints(user_id, is_visible);

-- Add index for cleanup queries
CREATE INDEX idx_complaints_visible_updated 
ON complaints(is_visible, updated_at);
```

### Query Optimization
```java
// ✅ Efficient - Uses index
@Query("SELECT c FROM Complaints c WHERE c.userId = :userId AND c.isVisible = true")
List<Complaints> findVisibleByUser(@Param("userId") Long userId);

// ❌ Less efficient - Filters in application
List<Complaints> all = cr.findByUserId(userId);
return all.stream().filter(Complaints::isVisible).collect(Collectors.toList());
```

---

## ✅ Summary

The soft delete implementation provides:
- ✅ Data preservation for audit and recovery
- ✅ User-friendly delete experience
- ✅ Admin capabilities for data management
- ✅ Compliance with data retention policies
- ✅ Analytics on deletion patterns
- ✅ No breaking changes to frontend

The user experience remains the same - they click delete and the claim disappears. But behind the scenes, the data is safely preserved! 🎉
