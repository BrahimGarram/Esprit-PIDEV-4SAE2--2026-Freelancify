# Claim Status Workflow

## Status Lifecycle

```
┌─────────────┐
│   Pending   │ ← User creates claim (default)
└──────┬──────┘
       │
       ├──────────────────────────────────┐
       │                                  │
       ▼                                  ▼
┌──────────────┐                   ┌──────────┐
│ Under_Review │                   │ Rejected │
│ (Admin views)│                   │  (Admin) │
└──────┬───────┘                   └──────────┘
       │
       ▼
┌──────────────┐
│   Resolved   │
│ (Admin adds  │
│ resolution)  │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│    Closed    │
│ (User accepts│
│  or Admin)   │
└──────────────┘
```

## Status Definitions

### 1. Pending (Default)
- **Set by**: System (automatic on claim creation)
- **Meaning**: Claim has been submitted and is waiting for admin review
- **User can**: View, edit (title/description/priority), delete
- **Admin can**: View, change to Under_Review

### 2. Under_Review
- **Set by**: Admin (when viewing claim details)
- **Meaning**: Admin is actively reviewing the claim
- **User can**: View only (no editing)
- **Admin can**: Add resolution note → Resolved, or Reject

### 3. Resolved
- **Set by**: Admin (when adding resolution note)
- **Meaning**: Admin has provided a solution/response
- **User can**: View resolution, accept (→ Closed)
- **Admin can**: Close claim

### 4. Rejected
- **Set by**: Admin (when rejecting claim)
- **Meaning**: Claim was rejected by admin
- **User can**: View only
- **Admin can**: Reopen or close

### 5. Closed
- **Set by**: User (accepting solution) OR Admin (closing claim)
- **Meaning**: Claim is finalized and no longer active
- **User can**: View only (read-only)
- **Admin can**: Reopen if needed

## User Permissions

### Regular Users Can:
- ✅ Create claims (status: Pending)
- ✅ Edit their own claims (title, description, priority only)
- ✅ Delete their own claims (soft delete)
- ✅ View all their claims
- ✅ Accept solutions (Resolved → Closed)
- ❌ Cannot change claim status directly
- ❌ Cannot edit claims in Under_Review, Resolved, Rejected, or Closed status

### Admins Can:
- ✅ View all claims
- ✅ Change claim status
- ✅ Add resolution notes
- ✅ Reject claims
- ✅ Close claims
- ✅ Reopen claims

## Edit Functionality

### What Users Can Edit:
1. **Title** - The claim title/type
2. **Description** - Detailed description of the issue
3. **Priority** - Urgent, High, Medium, Low

### What Users CANNOT Edit:
1. **Status** - Managed by admin workflow only
2. **Resolution Note** - Admin only
3. **Attachments** - Cannot change after creation
4. **Created Date** - System managed
5. **User ID** - System managed

### Edit Restrictions by Status:
- **Pending**: ✅ Can edit (title, description, priority)
- **Under_Review**: ❌ Cannot edit (admin is reviewing)
- **Resolved**: ❌ Cannot edit (solution provided)
- **Rejected**: ❌ Cannot edit (claim rejected)
- **Closed**: ❌ Cannot edit (claim finalized)

## Implementation Details

### Frontend (User View)
```typescript
// Edit form only shows:
- Title input
- Description textarea
- Priority dropdown
- Info note: "Status changes are managed by administrators only"

// Status is preserved from original claim
updateRequest = {
  idReclamation: claim.id,
  title: editClaim.title,
  description: editClaim.description,
  claimPriority: editClaim.claimPriority,
  claimStatus: claim.claimStatus // Keep original, don't allow change
};
```

### Backend Validation
```java
@PutMapping("/update-claim")
public Complaints updateClaim(@RequestBody Complaints c, @RequestParam("userId") Long userId) {
    // Verify claim belongs to user
    Optional<Complaints> existing = ic.retrieveClaimForUser(c.getIdReclamation(), userId);
    if (existing.isEmpty()) {
        return null; // Not found or doesn't belong to user
    }
    
    // Update with new data
    c.setUserId(userId);
    c.setUpdatedAt(new Date()); // Auto-set update timestamp
    return ic.updateComplaint(c);
}
```

## Status Change Triggers

### Automatic Status Changes:
1. **Create Claim** → Status: Pending
2. **Soft Delete** → isVisible: false (status unchanged)

### Admin-Triggered Status Changes:
1. **Admin views claim** → Pending → Under_Review
2. **Admin adds resolution** → Under_Review → Resolved
3. **Admin rejects** → Any status → Rejected
4. **Admin closes** → Any status → Closed

### User-Triggered Status Changes:
1. **User accepts solution** → Resolved → Closed

## Best Practices

### For Users:
1. Provide clear, detailed descriptions
2. Set appropriate priority levels
3. Only edit claims while in Pending status
4. Review admin resolutions before accepting

### For Admins:
1. Change to Under_Review when starting review
2. Always add resolution notes when resolving
3. Provide clear rejection reasons
4. Close claims only when fully resolved

## API Endpoints

### User Endpoints:
```
POST   /report/create-claim        - Create new claim (status: Pending)
GET    /report/retrieve-all-complaints?userId={id} - Get user's claims
PUT    /report/update-claim?userId={id} - Update claim (title/desc/priority)
DELETE /report/drop-claim/{id}?userId={id} - Soft delete claim
```

### Admin Endpoints (Future):
```
GET    /admin/claims               - Get all claims
PUT    /admin/claims/{id}/status   - Change claim status
PUT    /admin/claims/{id}/resolve  - Add resolution note
PUT    /admin/claims/{id}/reject   - Reject claim
PUT    /admin/claims/{id}/close    - Close claim
```

## Database Schema

```sql
CREATE TABLE complaints (
    id_reclamation BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP,
    updated_at DATE,
    claim_status VARCHAR(50), -- Pending, Under_Review, Resolved, Rejected, Closed
    claim_priority VARCHAR(50), -- Urgent, High, Medium, Low
    resolution_note TEXT,
    resolved_at DATE,
    is_visible BOOLEAN DEFAULT TRUE
);
```

## Status Badges (UI)

```css
.status-pending { background: #fef3c7; color: #92400e; }
.status-in-review { background: #dbeafe; color: #1e40af; }
.status-resolved { background: #d1fae5; color: #065f46; }
.status-rejected { background: #fee2e2; color: #991b1b; }
.status-closed { background: #e5e7eb; color: #374151; }
```

## Future Enhancements

1. **Email Notifications**
   - Notify user when status changes
   - Notify admin when new claim created

2. **Comments/Messages**
   - Allow back-and-forth communication
   - Keep claim in Under_Review during discussion

3. **Claim History**
   - Track all status changes
   - Show who changed status and when

4. **Escalation**
   - Auto-escalate if Pending > 48 hours
   - Priority-based SLA tracking

5. **Analytics**
   - Average resolution time
   - Claims by status
   - User satisfaction ratings

---

**Current Implementation**: ✅ Complete
**User Edit**: ✅ Title, Description, Priority only
**Status Management**: ✅ Admin-controlled workflow
**Soft Delete**: ✅ Implemented
