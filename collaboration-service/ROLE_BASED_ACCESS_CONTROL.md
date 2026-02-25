# Role-Based Access Control (RBAC) - Collaboration Service

## Roles Overview

The system supports four roles:
1. **FREELANCER** - Individual contractors who work on projects
2. **ENTERPRISE** - Companies that create and manage collaborations
3. **ADMIN** - System administrators with full access
4. **CLIENT** - Clients who can view and interact with projects (limited access)

## Access Control Matrix

### Collaboration Management

| Endpoint | FREELANCER | ENTERPRISE | ADMIN | CLIENT |
|----------|------------|------------|-------|--------|
| GET /api/collaborations | ✅ Browse | ✅ Browse | ✅ All | ✅ Browse |
| GET /api/collaborations/{id} | ✅ View | ✅ View | ✅ View | ✅ View |
| POST /api/collaborations | ❌ | ✅ Create | ✅ Create | ❌ |
| PUT /api/collaborations/{id} | ❌ | ✅ Own only | ✅ All | ❌ |
| DELETE /api/collaborations/{id} | ❌ | ✅ Own only | ✅ All | ❌ |

### Collaboration Requests

| Endpoint | FREELANCER | ENTERPRISE | ADMIN | CLIENT |
|----------|------------|------------|-------|--------|
| GET /api/collaboration-requests | ✅ Own | ✅ Own | ✅ All | ❌ |
| POST /api/collaboration-requests | ✅ Apply | ❌ | ✅ | ❌ |
| PUT /api/collaboration-requests/{id} | ❌ | ✅ Accept/Reject | ✅ All | ❌ |
| DELETE /api/collaboration-requests/{id} | ✅ Own only | ❌ | ✅ All | ❌ |

### Workspace & Project Management

| Endpoint | FREELANCER | ENTERPRISE | ADMIN | CLIENT |
|----------|------------|------------|-------|--------|
| GET /api/workspaces/{id} | ✅ Team member | ✅ Owner | ✅ All | ✅ If invited |
| GET /api/tasks | ✅ Team member | ✅ Owner | ✅ All | ✅ View only |
| POST /api/tasks | ✅ Team member | ✅ Owner | ✅ All | ❌ |
| PUT /api/tasks/{id} | ✅ Assigned | ✅ Owner | ✅ All | ❌ |
| DELETE /api/tasks/{id} | ❌ | ✅ Owner | ✅ All | ❌ |

### Sprints & Milestones

| Endpoint | FREELANCER | ENTERPRISE | ADMIN | CLIENT |
|----------|------------|------------|-------|--------|
| GET /api/sprints | ✅ Team member | ✅ Owner | ✅ All | ✅ View only |
| POST /api/sprints | ❌ | ✅ Owner | ✅ All | ❌ |
| PUT /api/sprints/{id} | ❌ | ✅ Owner | ✅ All | ❌ |
| GET /api/milestones | ✅ Team member | ✅ Owner | ✅ All | ✅ View only |
| POST /api/milestones | ❌ | ✅ Owner | ✅ All | ❌ |
| PUT /api/milestones/{id} | ❌ | ✅ Owner | ✅ All | ❌ |

### Time Tracking

| Endpoint | FREELANCER | ENTERPRISE | ADMIN | CLIENT |
|----------|------------|------------|-------|--------|
| GET /api/time-logs | ✅ Own | ✅ Team logs | ✅ All | ❌ |
| POST /api/time-logs | ✅ Own work | ❌ | ✅ All | ❌ |
| PUT /api/time-logs/{id} | ✅ Own only | ❌ | ✅ All | ❌ |
| PATCH /api/time-logs/{id}/approve | ❌ | ✅ Team logs | ✅ All | ❌ |

### Team Management

| Endpoint | FREELANCER | ENTERPRISE | ADMIN | CLIENT |
|----------|------------|------------|-------|--------|
| GET /api/team-members | ✅ Team member | ✅ Owner | ✅ All | ✅ View only |
| POST /api/team-members | ❌ | ✅ Owner | ✅ All | ❌ |
| PUT /api/team-members/{id} | ❌ | ✅ Owner | ✅ All | ❌ |
| DELETE /api/team-members/{id} | ❌ | ✅ Owner | ✅ All | ❌ |

### Comments

| Endpoint | FREELANCER | ENTERPRISE | ADMIN | CLIENT |
|----------|------------|------------|-------|--------|
| GET /api/tasks/{id}/comments | ✅ Team member | ✅ Owner | ✅ All | ✅ If invited |
| POST /api/tasks/{id}/comments | ✅ Team member | ✅ Owner | ✅ All | ✅ If invited |
| PUT /api/comments/{id} | ✅ Own only | ✅ Own only | ✅ All | ✅ Own only |
| DELETE /api/comments/{id} | ✅ Own only | ✅ Owner | ✅ All | ❌ |

## Implementation Notes

### 1. Controller-Level Security
Use `@PreAuthorize` annotations on controller methods:
```java
@PreAuthorize("hasAnyRole('ENTERPRISE', 'ADMIN')")
public ResponseEntity<CollaborationDTO> create(...)
```

### 2. Service-Level Validation
Services validate ownership and team membership:
```java
// Check if user is team member
if (!isTeamMember(userId, collaborationId) && !isAdmin(userId)) {
    throw new AccessDeniedException("Not a team member");
}
```

### 3. Admin Override
Admin users can bypass ownership checks using `adminOverride` parameter:
```java
@RequestParam(required = false, defaultValue = "false") boolean adminOverride
```

### 4. JWT Token Extraction
Extract user info from JWT in services:
```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
```

## Security Best Practices

1. **Always validate ownership** before allowing modifications
2. **Check team membership** for workspace operations
3. **Use @PreAuthorize** for role-based endpoint protection
4. **Validate in service layer** for business logic security
5. **Log security events** for audit trails
6. **Return 403 Forbidden** for access denied (not 404)
7. **Never expose sensitive data** in error messages

## Testing Roles

### Test Users (from Keycloak)
- **company1** - ENTERPRISE role
- **sarah.manager** - ENTERPRISE role
- **mike.backend** - FREELANCER role
- **john.frontend** - FREELANCER role
- **emma.designer** - FREELANCER role
- **admin** - ADMIN role (if created)

All test users have password: `password123`
