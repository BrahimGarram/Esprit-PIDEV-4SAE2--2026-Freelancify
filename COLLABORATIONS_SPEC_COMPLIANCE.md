# Collaborations Module – Spec Compliance

This document checks the implementation against the CRUD specification for **Collaboration** and **Collaboration Requests**.

**Backend:** Collaborations, companies, and collaboration requests are implemented in a dedicated **collaboration-service** (port **8083**), separate from **project-service** (port 8082). The frontend calls `http://localhost:8083/api/companies`, `http://localhost:8083/api/collaborations`, and `http://localhost:8083/api/collaboration-requests`.

---

## Entity 1: Collaboration

### 1️⃣ CREATE – Create a Collaboration

| Requirement | Status | Notes |
|-------------|--------|--------|
| **Required fields** | ✅ | company_id, title, description, collaboration_type, required_skills, budget_min, budget_max, estimated_duration, complexity_level, deadline, confidentiality_option |
| **Optional fields** | ✅ | max_freelancers_needed, milestone_structure, attachments, **industry** (added per spec “filter by industry”) |
| Validate company identity/permissions | ⚠️ | Frontend sends companyId; collaboration-service has no JWT – ownership not validated server-side. Can be added when securing collaboration-service. |
| Validate budget range (min ≤ max) | ✅ | `CollaborationService.create()` and update |
| Validate required skills format | ⚠️ | Stored as string (comma or JSON); no strict format validation |
| Store in DB, status = OPEN | ✅ | Default status OPEN on create |
| Trigger ML matching / compatibility scores | ❌ | Not implemented (placeholder for future) |
| Notify top-ranked freelancers | ❌ | Not implemented (placeholder for future) |

### 2️⃣ READ – View Collaborations

| Requirement | Status | Notes |
|-------------|--------|--------|
| **Enterprise:** View all its collaborations | ✅ | GET with `companyId` |
| **Enterprise:** Filter by status (OPEN, MATCHED, IN_PROGRESS, COMPLETED, CANCELLED, ON_HOLD) | ✅ | GET with `companyId` + `status` |
| **Enterprise:** View freelancer applications | ✅ | Collaboration detail → list of applications |
| **Enterprise:** View selected freelancer / track milestones | ⚠️ | Applications list + accept/reject. “Selected freelancer” and milestone tracking are partial (milestone_structure stored, no dedicated milestone UI). |
| **Freelancer:** Browse OPEN collaborations | ✅ | GET with `status=OPEN` |
| **Freelancer:** Filter by Skills, Budget, Duration, Industry | ✅ | GET params: skills, budgetMin, budgetMax, estimatedDuration, industry; backend `getOpenWithFilters()` |
| **Freelancer:** View compatibility score (%) | ❌ | Not implemented (ML placeholder) |
| **Freelancer:** Apply to collaboration | ✅ | Create Collaboration Request |
| **Freelancer:** Save to favorites | ❌ | Not implemented (no favorites entity) |
| **Admin:** View all collaborations | ✅ | GET without companyId |
| **Admin:** Monitor / analytics dashboard | ❌ | No analytics dashboard for collaborations |
| **Admin:** Force cancel/delete fraudulent | ✅ | DELETE with `adminOverride=true` |

### 3️⃣ UPDATE – Modify Collaboration

| Requirement | Status | Notes |
|-------------|--------|--------|
| **When OPEN:** Enterprise can update budget, skills, description, deadline, complexity | ✅ | Update endpoint; company ownership enforced via companyId |
| **When OPEN:** Recalculate ML / re-rank / notify | ❌ | Not implemented |
| **When IN_PROGRESS:** Add/update milestones, deadline, progress, status | ✅ | milestoneStructure, deadline, status in update |
| **When IN_PROGRESS:** Locked budget & required skills | ✅ | Service rejects budget/skills update when status not OPEN/ON_HOLD |
| Status lifecycle (OPEN → MATCHED → IN_PROGRESS → COMPLETED, etc.) | ✅ | All 6 statuses supported; no strict state-machine validation |
| Only enterprise owner can update | ✅ | Check `existsByIdAndCompanyId`; admin can use companyId of target |

### 4️⃣ DELETE – Delete Collaboration

| Requirement | Status | Notes |
|-------------|--------|--------|
| **Before start (OPEN):** Enterprise can delete permanently | ✅ | DELETE allowed only when status = OPEN (unless adminOverride) |
| **After start (IN_PROGRESS):** Only cancellation allowed | ✅ | Delete rejected; use PATCH status → CANCELLED |
| Record archived (not deleted) | ⚠️ | Cancelled rows kept with status CANCELLED; no separate “archived” flag |
| Admin force delete | ✅ | DELETE with `adminOverride=true` |

### Security & audit

| Requirement | Status | Notes |
|-------------|--------|--------|
| Only enterprise owner update/delete | ✅ | Via companyId in request |
| Freelancers cannot modify collaboration data | ✅ | No update/delete for collaborations by freelancer |
| Admin override | ✅ | adminOverride flag on delete and status |
| All actions logged | ⚠️ | log.info in services; no dedicated audit table |

---

## Entity 2: Collaboration Requests

| Requirement | Status | Notes |
|-------------|--------|--------|
| **CREATE:** collaboration_id, freelancer_id, proposal_message, proposed_price, status=PENDING | ✅ | CreateCollaborationRequestDTO; default PENDING |
| **READ:** Freelancer sees their applications | ✅ | GET by freelancerId |
| **READ:** Enterprise sees all for project | ✅ | GET by collaborationId |
| **UPDATE:** Freelancer edits proposal if PENDING | ✅ | PATCH with proposalMessage, proposedPrice |
| **UPDATE:** Enterprise sets ACCEPTED/REJECTED | ✅ | PATCH status with companyId |
| **When ACCEPTED:** Collaboration status → MATCHED | ✅ | CollaborationRequestService calls setStatusToMatched() |
| **DELETE:** Freelancer withdraw if PENDING | ✅ | DELETE withdraw endpoint |

---

## Summary

- **Fully implemented:** CRUD for Collaboration and Collaboration Requests, required/optional fields, status rules, locked fields when not OPEN, enterprise/freelancer/admin views, filters (skills, budget, duration, industry), apply/accept/reject/withdraw.
- **Partially implemented:** Company ownership enforced by client-supplied companyId (no JWT in collaboration-service); milestone structure stored but no full “track milestones” UI; logging only (no audit table).
- **Not implemented:** ML matching, compatibility scores, notifications, favorites, admin analytics dashboard, “block company” / “suspend visibility”.

---

## Changes made in this check

1. **Backend:** Added optional `industry` on Collaboration (entity, DTOs, create/update). Added `getOpenWithFilters(skills, budgetMin, budgetMax, estimatedDuration, industry)` and GET params on `/api/collaborations`.
2. **Frontend:** Added industry to create/edit/detail; added freelancer filter inputs (Skills, Min/Max budget, Duration, Industry) and wired them to the new API params.
3. **Architecture:** Collaborations moved to a separate **collaboration-service** (folder `collaboration-service/`, port 8083). Project-service (port 8082) now only handles projects and tasks. Frontend collaboration/company/request APIs point to port 8083.
