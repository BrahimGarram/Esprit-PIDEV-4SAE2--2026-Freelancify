# Roles & Permissions — Collaborations & Collaboration Requests

This document defines the four roles and their permissions on the **collaborations** and **collaboration_requests** tables, and how they map to the current implementation.

---

## Roles

| Role | Description |
|------|-------------|
| **Enterprise (Company / Startup)** | Owner of collaborations; creates and manages them. |
| **Freelancer** | Applies via `collaboration_requests`; cannot create collaborations. |
| **Client** | If same as enterprise owner → same as Enterprise. If project manager → limited (read, update progress; no delete, no budget change). |
| **Admin** | System-level: view all, moderate, force delete, suspend/block. |

---

## 1. Enterprise (Company / Startup)

Enterprise is the **owner** of collaborations (via `company_id` → company → `owner_id`).

### CREATE (Collaboration)

| Requirement | Status | Notes |
|-------------|--------|--------|
| Fill form, submit required fields, budget/skills/duration/deadline | Done | `POST /api/collaborations` with `CreateCollaborationRequest`. |
| System validates enterprise account | Partial | Backend does not yet validate that caller is company owner; relies on frontend passing `companyId`. |
| Collaboration saved with `status = OPEN` | Done | `CollaborationService.create()` sets `OPEN`. |
| ML calculates top freelancer matches / notifies freelancers | Not implemented | No ML or notification service. |
| Collaboration visible in marketplace | Done | OPEN collaborations returned by `getOpen()` / `getOpenWithFilters()`. |

### READ

| Requirement | Status | Notes |
|-------------|--------|--------|
| View all its collaborations | Done | `GET /api/collaborations?companyId=X`. |
| Filter by status (OPEN, IN_PROGRESS, COMPLETED) | Done | `companyId` + `status` query params. |
| View applications (`collaboration_requests`) | Done | `GET /api/collaboration-requests/collaboration/{id}` (enterprise must own collaboration via company). |
| View compatibility score per freelancer | Not implemented | No `compatibility_score` in model or API. |
| Milestones and progress tracking | Partial | `milestoneStructure` exists on collaboration; no separate progress/milestone API. |
| Cannot access other companies’ data | Done | Backend: update/delete/status and request status require `companyId` ownership. |

### UPDATE

| Requirement | Status | Notes |
|-------------|--------|--------|
| Before freelancer accepted: modify budget, skills, description, deadline, complexity | Done | `PUT /api/collaborations/{id}?companyId=X`; allowed when status OPEN/ON_HOLD. |
| Recalculate ML / update suggested freelancers | Not implemented | No ML. |
| After accepted (IN_PROGRESS): add milestones, update progress, change status, extend deadline | Partial | Update allows status/milestone/deadline; “progress” not a dedicated field. |
| Locked when contract signed: budget, required skills | Done | `CollaborationService.update()` rejects budget/skills change when status not OPEN/ON_HOLD. |

### DELETE

| Requirement | Status | Notes |
|-------------|--------|--------|
| If `status = OPEN`: delete permanently | Done | `DELETE /api/collaborations/{id}?companyId=X`; backend allows only when OPEN (or admin). |
| If IN_PROGRESS: only CANCEL; record kept for history | Done | Delete disallowed for non-OPEN; use status update to CANCELLED. |

---

## 2. Freelancer

Freelancers do **not** create collaborations; they interact only via **collaboration_requests**.

### CREATE (Application)

| Requirement | Status | Notes |
|-------------|--------|--------|
| Apply to OPEN collaboration with proposal and custom price | Done | `POST /api/collaboration-requests` with `CreateCollaborationRequestDTO`. |
| Request saved with status PENDING; enterprise notified | Done | Status PENDING; notification not implemented. |

### READ

| Requirement | Status | Notes |
|-------------|--------|--------|
| Browse OPEN collaborations | Done | `GET /api/collaborations?status=OPEN` (+ optional filters). |
| Filter by budget, skills, duration | Done | `getOpenWithFilters(skills, budgetMin, budgetMax, estimatedDuration, industry)`. |
| View compatibility score (% match) | Not implemented | No score in API. |
| View own applications and status (PENDING/ACCEPTED/REJECTED) | Done | `GET /api/collaboration-requests/freelancer/{freelancerId}`. |
| Cannot see private enterprise analytics or modify collaboration | Done | No access to company-owned endpoints without companyId; no update on collaboration. |

### UPDATE

| Requirement | Status | Notes |
|-------------|--------|--------|
| Edit proposal if PENDING | Done | `PATCH /api/collaboration-requests/{id}?freelancerId=X` with `UpdateProposalDTO`. |
| Withdraw application | Done | `DELETE /api/collaboration-requests/{id}/withdraw?freelancerId=X` (only PENDING). |
| After ACCEPTED: no proposal edit; follow milestone workflow | Done | Backend rejects proposal update when not PENDING. |

### DELETE

| Requirement | Status | Notes |
|-------------|--------|--------|
| Withdraw (delete) application if PENDING | Done | Withdraw endpoint. |
| Cannot delete collaboration or change collaboration status | Done | No such endpoints for freelancer. |

---

## 3. Client (if different from Enterprise)

| Scenario | Status | Notes |
|----------|--------|--------|
| Client = enterprise owner | Same as Enterprise | Use same companyId and ownership checks. |
| Client = project manager in company | Not implemented | No CLIENT/project-manager role; no “can read + update progress, no delete/budget” policy in backend. Would require role and company-membership model. |

---

## 4. Admin

### CREATE

| Requirement | Status | Notes |
|-------------|--------|--------|
| Usually does not create; can create test collaborations / enterprise accounts | Partial | Admin can call same create endpoints; no special “test” or “admin-only” flag. |

### READ

| Requirement | Status | Notes |
|-------------|--------|--------|
| View ALL collaborations | Done | `GET /api/collaborations` with no params; admin Collaborations page shows all. |
| View ALL applications | Done | `GET /api/collaboration-requests` (no params); admin page has “All applications” section. |
| View analytics dashboard | Done | Admin dashboard shows Collaboration and Application count cards; links to Collaborations page. |
| Detect abnormal activity / monitor fraud | Partial | No automated rules; admin can review all data. |

### UPDATE

| Requirement | Status | Notes |
|-------------|--------|--------|
| Change collaboration status | Done | `PATCH /api/collaborations/{id}/status?adminOverride=true`; admin can set any status including ARCHIVED. |
| Suspend enterprise / block freelancer | Not in this service | Would live in user/company or auth service. |
| Edit suspicious collaborations | Done | `PUT /api/collaborations/{id}?companyId=X&adminOverride=true`; admin can edit any collaboration. |

### DELETE

| Requirement | Status | Notes |
|-------------|--------|--------|
| Force delete fraudulent collaboration | Done | `DELETE /api/collaborations/{id}?companyId=X&adminOverride=true`; admin can delete any status. |
| Remove inappropriate content | Done | Same as force delete. |
| Archive old collaborations | Done | Admin can set status to ARCHIVED (or use Delete). Status ARCHIVED added to enum. |

---

## Tables Summary

| Table | Purpose |
|-------|---------|
| **collaborations** | One row per collaboration (project) created by an enterprise (company). Status: OPEN → MATCHED → IN_PROGRESS → COMPLETED / CANCELLED / ON_HOLD / ARCHIVED. |
| **collaboration_requests** | One row per freelancer application to a collaboration. Status: PENDING → ACCEPTED or REJECTED. |

---

## Implementation notes

- **Auth:** Collaboration-service does not validate JWT or roles. It trusts `companyId`, `freelancerId`, and `adminOverride` from the client. For production, an API gateway or backend auth should verify that the authenticated user is the company owner, the freelancer, or admin before calling these APIs.
- **Client role:** “Project manager” and restricted permissions require a user/company-membership and role model (e.g. in user-service) and then checks in collaboration-service or gateway.
- **ML & notifications:** Not implemented; can be added as separate services that react to collaboration/request events.
