# Complaints Feature - Architecture & Flow

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Angular Frontend                         │
│                    (Port 4200)                               │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │  complaints.component.ts                           │    │
│  │  - View/Edit/Delete claims                         │    │
│  │  - Modal management                                │    │
│  │  - File upload handling                            │    │
│  └────────────────────────────────────────────────────┘    │
│                         │                                    │
│                         ▼                                    │
│  ┌────────────────────────────────────────────────────┐    │
│  │  complaints.service.ts                             │    │
│  │  - API calls to backend                            │    │
│  │  - ImgBB integration                               │    │
│  └────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                         │
                         │ HTTP Requests
                         ▼
┌─────────────────────────────────────────────────────────────┐
│              Spring Boot Backend                             │
│         Complaints Service (Port 8089)                       │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │  ComplaintsController.java                         │    │
│  │  - REST API endpoints                              │    │
│  │  - Request validation                              │    │
│  │  - User authorization                              │    │
│  └────────────────────────────────────────────────────┘    │
│                         │                                    │
│                         ▼                                    │
│  ┌────────────────────────────────────────────────────┐    │
│  │  ComplaintsService.java                            │    │
│  │  - Business logic                                  │    │
│  │  - Data validation                                 │    │
│  └────────────────────────────────────────────────────┘    │
│                         │                                    │
│                         ▼                                    │
│  ┌────────────────────────────────────────────────────┐    │
│  │  ComplaintsRepository.java                         │    │
│  │  - Database operations                             │    │
│  │  - JPA queries                                     │    │
│  └────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                  MySQL Database                              │
│              (Port 3306)                                     │
│                                                              │
│  Tables:                                                     │
│  - complaints                                                │
│  - claim_attachment                                          │
└─────────────────────────────────────────────────────────────┘

                    ┌──────────────┐
                    │   ImgBB CDN  │
                    │  (External)  │
                    └──────────────┘
                         ▲
                         │ Image Upload
                         │ (Optional)
                    complaints.service.ts
```

## 🔄 Data Flow

### 1. Create Claim Flow

```
User fills form
    │
    ▼
Select file (optional)
    │
    ├─── Is Image? ───┐
    │                 │
    NO               YES
    │                 │
    │                 ▼
    │         Upload to ImgBB
    │                 │
    │                 ├─── Success ───┐
    │                 │                │
    │                 │               Get URL
    │                 │                │
    │                 └─── Fail ──────┤
    │                                  │
    └──────────────────────────────────┤
                                       │
                                       ▼
                            Create claim with file
                                       │
                                       ▼
                              Save to database
                                       │
                                       ▼
                              Show success message
```

### 2. View Claim Details Flow

```
User clicks claim
    │
    ▼
Open modal
    │
    ▼
Display claim info
    │
    ├─── Has attachment? ───┐
    │                       │
    NO                     YES
    │                       │
    │                       ▼
    │              Show attachment details
    │                       │
    │                       ▼
    │              Enable download button
    │                       │
    └───────────────────────┘
                │
                ▼
        User can edit/delete
```

### 3. Edit Claim Flow

```
User clicks "Edit Claim"
    │
    ▼
Enter edit mode
    │
    ▼
Modify fields
    │
    ▼
Click "Save Changes"
    │
    ▼
Validate input
    │
    ▼
Send PUT request
    │
    ├─── Success ───┐
    │               │
    │               ▼
    │       Update local data
    │               │
    │               ▼
    │       Show success toast
    │               │
    │               ▼
    │       Exit edit mode
    │               │
    └───────────────┘
            │
            ▼
    Refresh claim list
```

### 4. Delete Claim Flow

```
User clicks "Delete Claim"
    │
    ▼
Show confirmation dialog
    │
    ├─── Cancel ───┐
    │              │
    │              ▼
    │         Do nothing
    │
    └─── Confirm ───┐
                    │
                    ▼
            Send DELETE request
                    │
                    ├─── Success ───┐
                    │               │
                    │               ▼
                    │       Remove from database
                    │               │
                    │               ▼
                    │       Show success toast
                    │               │
                    │               ▼
                    │       Close modal
                    │               │
                    └───────────────┘
                            │
                            ▼
                    Refresh claim list
```

### 5. Download Attachment Flow

```
User clicks "Download Attachment"
    │
    ▼
Get file URL
    │
    ├─── External URL (ImgBB) ───┐
    │                            │
    │                            ▼
    │                    Open in new tab
    │
    └─── Local URL ───┐
                      │
                      ▼
              Construct full URL
                      │
                      ▼
              http://localhost:8089/uploads/claims/file.ext
                      │
                      ▼
              Open/Download file
```

## 🗄️ Database Schema

```sql
-- Complaints Table
CREATE TABLE complaints (
    id_reclamation BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    claim_status ENUM('Pending', 'Under_Review', 'Resolved', 'Rejected', 'Closed'),
    claim_priority ENUM('Low', 'Medium', 'High', 'Urgent'),
    resolution_note TEXT,
    created_at DATETIME,
    updated_at DATETIME,
    resolved_at DATETIME
);

-- Claim Attachment Table
CREATE TABLE claim_attachment (
    id_attachment BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_name VARCHAR(255),
    file_url VARCHAR(500) NOT NULL,
    file_type VARCHAR(100),
    file_size BIGINT,
    uploaded_at DATETIME,
    uploaded_by_id BIGINT,
    claim_id BIGINT,
    FOREIGN KEY (claim_id) REFERENCES complaints(id_reclamation)
);
```

## 🔐 Security Flow

```
Frontend Request
    │
    ▼
Include JWT Token
    │
    ▼
Backend receives request
    │
    ▼
Validate JWT Token
    │
    ├─── Invalid ───┐
    │               │
    │               ▼
    │       Return 401 Unauthorized
    │
    └─── Valid ───┐
                  │
                  ▼
          Extract user ID from token
                  │
                  ▼
          Verify user owns claim
                  │
                  ├─── Not owner ───┐
                  │                 │
                  │                 ▼
                  │         Return 403 Forbidden
                  │
                  └─── Owner ───┐
                                │
                                ▼
                        Process request
                                │
                                ▼
                        Return response
```

## 📊 State Management

### Component State

```typescript
// Modal state
selectedClaim: Complaint | null = null;
isEditMode: boolean = false;

// Loading states
loading: boolean = false;
creating: boolean = false;
updating: boolean = false;
deleting: boolean = false;

// Data
complaints: Complaint[] = [];
currentUserId: number | null = null;

// Filters
statusFilter: 'ALL' | ClaimStatus = 'ALL';
priorityFilter: 'ALL' | ClaimPriority = 'ALL';

// Form data
newClaim: { ... };
editClaim: { ... };
```

## 🎨 UI Component Hierarchy

```
complaints.component
    │
    ├── Dashboard Header
    │   ├── Title
    │   └── Refresh Button
    │
    ├── Create Claim Widget
    │   ├── Form Fields
    │   │   ├── Type Dropdown
    │   │   ├── Priority Dropdown
    │   │   ├── Description Textarea
    │   │   └── File Upload
    │   └── Submit Button
    │
    ├── Claims List Widget
    │   ├── Filters
    │   │   ├── Status Filter
    │   │   └── Priority Filter
    │   │
    │   └── Claim Items (clickable)
    │       ├── Icon
    │       ├── Title
    │       ├── Description
    │       ├── Badges (Priority, Status)
    │       └── Metadata (Date, Attachment)
    │
    └── Modal (when claim selected)
        ├── Modal Header
        │   ├── Title
        │   └── Close Button
        │
        ├── Modal Body
        │   ├── Claim Information Section
        │   ├── Timeline Section
        │   ├── Resolution Note Section
        │   ├── Attachment Details Section
        │   └── Edit Form (when in edit mode)
        │
        └── Modal Footer
            ├── Close Button
            ├── Edit Button
            ├── Save Button (edit mode)
            ├── Cancel Button (edit mode)
            └── Delete Button
```

## 🔄 Lifecycle Hooks

```typescript
ngOnInit()
    │
    ▼
loadUserAndComplaints()
    │
    ├── Get current user
    │   │
    │   ▼
    │   Store user ID
    │
    └── Load complaints
        │
        ▼
        Display in list
```

## 📡 API Request/Response Examples

### Create Claim
```http
POST /freelancity/report/create-claim?userId=1
Content-Type: multipart/form-data

title: Payment issue
description: Client hasn't paid for completed work
priority: High
file: [binary data]

Response:
{
  "idReclamation": 123,
  "userId": 1,
  "title": "Payment issue",
  "description": "Client hasn't paid for completed work",
  "claimStatus": "Pending",
  "claimPriority": "High",
  "createdAt": "2024-01-15T10:30:00",
  "claimAttachment": {
    "fileName": "invoice.pdf",
    "fileUrl": "/uploads/claims/1234567890_invoice.pdf",
    "fileType": "application/pdf",
    "fileSize": 102400
  }
}
```

### Update Claim
```http
PUT /freelancity/report/update-claim?userId=1
Content-Type: application/json

{
  "idReclamation": 123,
  "title": "Payment issue - Updated",
  "description": "Client still hasn't paid",
  "claimPriority": "Urgent",
  "claimStatus": "Under_Review"
}

Response: [Updated Complaint object]
```

### Delete Claim
```http
DELETE /freelancity/report/drop-claim/123?userId=1

Response: 204 No Content
```

---

This architecture ensures:
- ✅ Separation of concerns
- ✅ Secure user authorization
- ✅ Efficient data flow
- ✅ Scalable design
- ✅ Clear component hierarchy
