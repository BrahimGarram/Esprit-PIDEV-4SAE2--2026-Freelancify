# Project Management API Documentation

## 🎯 Overview
RESTful API endpoints for the Mini Project Management System within the collaboration module.

**Base URL:** `http://localhost:8082/api` (assuming collaboration-service runs on port 8082)

**CORS:** Enabled for `http://localhost:4200` (Angular frontend)

---

## 📋 Table of Contents
1. [Task Management API](#task-management-api)
2. [Task Comments API](#task-comments-api)
3. [Team Members API](#team-members-api)
4. [Milestones API](#milestones-api)
5. [Sprints API](#sprints-api)
6. [Time Logs API](#time-logs-api)
7. [Workspace Statistics API](#workspace-statistics-api)

---

## 1. Task Management API

### Base Path: `/api/tasks`

#### Create Task
```http
POST /api/tasks
Content-Type: application/json

{
  "collaborationId": 1,
  "title": "Implement user authentication",
  "description": "Add JWT-based authentication",
  "assignedFreelancerId": 5,
  "priority": "HIGH",
  "status": "TODO",
  "deadline": "2024-12-31T23:59:59",
  "estimatedHours": 8,
  "milestoneId": 2,
  "sprintId": 3,
  "dependsOnTaskIds": [10, 11]
}

Response: 201 Created
```

#### Update Task
```http
PUT /api/tasks/{taskId}
Content-Type: application/json

{
  "title": "Updated title",
  "status": "IN_PROGRESS",
  "actualHours": 5
}

Response: 200 OK
```

#### Move Task (Kanban)
```http
PATCH /api/tasks/{taskId}/move?status=IN_PROGRESS&orderIndex=2

Response: 200 OK
```

#### Delete Task
```http
DELETE /api/tasks/{taskId}

Response: 204 No Content
```

#### Get Task by ID
```http
GET /api/tasks/{taskId}

Response: 200 OK
```

#### Get Tasks by Collaboration
```http
GET /api/tasks/collaboration/{collaborationId}

Response: 200 OK
[
  {
    "id": 1,
    "title": "Task 1",
    "status": "TODO",
    "priority": "HIGH",
    ...
  }
]
```

#### Get Tasks by Status (Kanban Column)
```http
GET /api/tasks/collaboration/{collaborationId}/status/IN_PROGRESS

Response: 200 OK
```

#### Get Tasks by Freelancer
```http
GET /api/tasks/freelancer/{freelancerId}

Response: 200 OK
```

#### Get Tasks by Milestone
```http
GET /api/tasks/milestone/{milestoneId}

Response: 200 OK
```

#### Get Tasks by Sprint
```http
GET /api/tasks/sprint/{sprintId}

Response: 200 OK
```

#### Get Subtasks
```http
GET /api/tasks/{taskId}/subtasks

Response: 200 OK
```

#### Get Overdue Tasks
```http
GET /api/tasks/collaboration/{collaborationId}/overdue

Response: 200 OK
```

---

## 2. Task Comments API

### Base Path: `/api/task-comments`

#### Create Comment
```http
POST /api/task-comments
Content-Type: application/json

{
  "taskId": 1,
  "userId": 5,
  "content": "Great progress! @john please review",
  "mentionedUserIds": [10],
  "attachments": "file1.pdf,file2.png"
}

Response: 201 Created
```

#### Update Comment
```http
PUT /api/task-comments/{commentId}
Content-Type: text/plain

Updated comment content

Response: 200 OK
```

#### Delete Comment
```http
DELETE /api/task-comments/{commentId}

Response: 204 No Content
```

#### Get Comments by Task
```http
GET /api/task-comments/task/{taskId}

Response: 200 OK
```

#### Get Comments by User
```http
GET /api/task-comments/user/{userId}

Response: 200 OK
```

---

## 3. Team Members API

### Base Path: `/api/team-members`

#### Add Team Member
```http
POST /api/team-members
Content-Type: application/json

{
  "collaborationId": 1,
  "freelancerId": 5,
  "role": "FRONTEND_DEVELOPER"
}

Response: 201 Created
```

**Available Roles:**
- `PROJECT_MANAGER`
- `FRONTEND_DEVELOPER`
- `BACKEND_DEVELOPER`
- `FULLSTACK_DEVELOPER`
- `DESIGNER`
- `QA_TESTER`
- `DEVOPS_ENGINEER`
- `BUSINESS_ANALYST`
- `TECHNICAL_WRITER`
- `OTHER`

#### Update Member Role
```http
PATCH /api/team-members/{memberId}/role?role=PROJECT_MANAGER

Response: 200 OK
```

#### Remove Team Member
```http
DELETE /api/team-members/{memberId}

Response: 204 No Content
```

#### Get Team Members
```http
GET /api/team-members/collaboration/{collaborationId}

Response: 200 OK
```

#### Get Freelancer Teams
```http
GET /api/team-members/freelancer/{freelancerId}

Response: 200 OK
```

#### Get Specific Team Member
```http
GET /api/team-members/collaboration/{collaborationId}/freelancer/{freelancerId}

Response: 200 OK
```

---

## 4. Milestones API

### Base Path: `/api/milestones`

#### Create Milestone
```http
POST /api/milestones
Content-Type: application/json

{
  "collaborationId": 1,
  "title": "Phase 1: Design",
  "description": "Complete all design mockups",
  "orderIndex": 1,
  "dueDate": "2024-12-31T23:59:59",
  "paymentAmount": 5000.00
}

Response: 201 Created
```

#### Update Milestone
```http
PUT /api/milestones/{milestoneId}
Content-Type: application/json

{
  "title": "Updated title",
  "paymentAmount": 6000.00
}

Response: 200 OK
```

#### Update Milestone Status
```http
PATCH /api/milestones/{milestoneId}/status?status=COMPLETED

Response: 200 OK
```

**Milestone Statuses:**
- `NOT_STARTED`
- `IN_PROGRESS`
- `COMPLETED`
- `CANCELLED`

#### Auto-Update Milestone Status
```http
POST /api/milestones/{milestoneId}/auto-update-status

Response: 200 OK
```
*Automatically calculates status based on linked task completion*

#### Delete Milestone
```http
DELETE /api/milestones/{milestoneId}

Response: 204 No Content
```

#### Get Milestone by ID
```http
GET /api/milestones/{milestoneId}

Response: 200 OK
```

#### Get Milestones by Collaboration
```http
GET /api/milestones/collaboration/{collaborationId}

Response: 200 OK
```

#### Get Milestones by Status
```http
GET /api/milestones/collaboration/{collaborationId}/status/IN_PROGRESS

Response: 200 OK
```

---

## 5. Sprints API

### Base Path: `/api/sprints`

#### Create Sprint
```http
POST /api/sprints
Content-Type: application/json

{
  "collaborationId": 1,
  "name": "Sprint 1",
  "goal": "Complete authentication module",
  "startDate": "2024-01-01T00:00:00",
  "endDate": "2024-01-14T23:59:59",
  "durationWeeks": 2
}

Response: 201 Created
```

#### Update Sprint
```http
PUT /api/sprints/{sprintId}
Content-Type: application/json

{
  "name": "Updated Sprint Name",
  "goal": "Updated goal"
}

Response: 200 OK
```

#### Update Sprint Status
```http
PATCH /api/sprints/{sprintId}/status?status=ACTIVE

Response: 200 OK
```

**Sprint Statuses:**
- `PLANNED`
- `ACTIVE`
- `COMPLETED`
- `CANCELLED`

#### Start Sprint
```http
POST /api/sprints/{sprintId}/start

Response: 200 OK
```
*Sets status to ACTIVE and validates no other active sprint exists*

#### Complete Sprint
```http
POST /api/sprints/{sprintId}/complete

Response: 200 OK
```

#### Delete Sprint
```http
DELETE /api/sprints/{sprintId}

Response: 204 No Content
```

#### Get Sprint by ID
```http
GET /api/sprints/{sprintId}

Response: 200 OK
```

#### Get Sprints by Collaboration
```http
GET /api/sprints/collaboration/{collaborationId}

Response: 200 OK
```

#### Get Active Sprint
```http
GET /api/sprints/collaboration/{collaborationId}/active

Response: 200 OK or 404 Not Found
```

#### Get Sprints by Status
```http
GET /api/sprints/collaboration/{collaborationId}/status/ACTIVE

Response: 200 OK
```

---

## 6. Time Logs API

### Base Path: `/api/time-logs`

#### Create Time Log (Manual Entry)
```http
POST /api/time-logs
Content-Type: application/json

{
  "taskId": 1,
  "freelancerId": 5,
  "startTime": "2024-01-01T09:00:00",
  "endTime": "2024-01-01T17:00:00",
  "description": "Worked on authentication"
}

Response: 201 Created
```

#### Start Timer
```http
POST /api/time-logs/start?taskId=1&freelancerId=5

Response: 201 Created
```
*Starts a timer for the task. Only one active timer allowed per freelancer.*

#### Stop Timer
```http
POST /api/time-logs/{timeLogId}/stop

Response: 200 OK
```
*Stops the timer and calculates duration*

#### Update Time Log
```http
PUT /api/time-logs/{timeLogId}
Content-Type: application/json

{
  "startTime": "2024-01-01T09:00:00",
  "endTime": "2024-01-01T18:00:00",
  "description": "Updated description"
}

Response: 200 OK
```

#### Approve Time Log
```http
POST /api/time-logs/{timeLogId}/approve

Response: 200 OK
```
*Approves timesheet entry and updates task actual hours*

#### Reject Time Log
```http
POST /api/time-logs/{timeLogId}/reject

Response: 200 OK
```

**Time Log Statuses:**
- `PENDING` - Awaiting approval
- `APPROVED` - Counted in actual hours
- `REJECTED` - Not counted

#### Delete Time Log
```http
DELETE /api/time-logs/{timeLogId}

Response: 204 No Content
```

#### Get Time Logs by Task
```http
GET /api/time-logs/task/{taskId}

Response: 200 OK
```

#### Get Time Logs by Freelancer
```http
GET /api/time-logs/freelancer/{freelancerId}

Response: 200 OK
```

#### Get Pending Time Logs
```http
GET /api/time-logs/freelancer/{freelancerId}/pending

Response: 200 OK
```

#### Get Active Time Logs
```http
GET /api/time-logs/freelancer/{freelancerId}/active

Response: 200 OK
```

#### Get Total Approved Hours
```http
GET /api/time-logs/task/{taskId}/total-hours

Response: 200 OK
8
```

---

## 7. Workspace Statistics API

### Base Path: `/api/workspace-stats`

#### Get Workspace Stats
```http
GET /api/workspace-stats/collaboration/{collaborationId}

Response: 200 OK
{
  "collaborationId": 1,
  "totalTasks": 50,
  "completedTasks": 30,
  "inProgressTasks": 15,
  "overdueTasks": 5,
  "progressPercentage": 60,
  "totalTeamMembers": 8,
  "totalMilestones": 5,
  "completedMilestones": 2,
  "totalSprints": 3,
  "activeSprints": 1,
  "tasksByStatus": {
    "BACKLOG": 10,
    "TODO": 15,
    "IN_PROGRESS": 15,
    "REVIEW": 5,
    "DONE": 30
  },
  "tasksByPriority": {
    "LOW": 10,
    "MEDIUM": 25,
    "HIGH": 12,
    "CRITICAL": 3
  },
  "totalEstimatedHours": 400,
  "totalActualHours": 350,
  "burnRate": 0.875
}
```

#### Get Team Workload
```http
GET /api/workspace-stats/collaboration/{collaborationId}/team-workload

Response: 200 OK
{
  "5": 8,   // Freelancer ID 5 has 8 active tasks
  "10": 12,
  "15": 6
}
```

#### Get Freelancer Stats
```http
GET /api/workspace-stats/collaboration/{collaborationId}/freelancer/{freelancerId}

Response: 200 OK
{
  "totalTasks": 15,
  "completedTasks": 10,
  "inProgressTasks": 3,
  "overdueTasks": 2,
  "totalEstimatedHours": 120,
  "totalActualHours": 110,
  "completionRate": 66
}
```

#### Get Milestone Progress
```http
GET /api/workspace-stats/collaboration/{collaborationId}/milestone-progress

Response: 200 OK
{
  "milestone_1": {
    "title": "Phase 1: Design",
    "totalTasks": 10,
    "completedTasks": 8,
    "progressPercentage": 80,
    "status": "IN_PROGRESS"
  },
  "milestone_2": {
    "title": "Phase 2: Development",
    "totalTasks": 20,
    "completedTasks": 5,
    "progressPercentage": 25,
    "status": "IN_PROGRESS"
  }
}
```

#### Get Sprint Burndown
```http
GET /api/workspace-stats/sprint/{sprintId}/burndown

Response: 200 OK
{
  "sprintName": "Sprint 1",
  "totalEstimatedHours": 80,
  "completedHours": 50,
  "remainingHours": 30,
  "totalTasks": 15,
  "completedTasks": 9,
  "progressPercentage": 62
}
```

---

## 🔐 Authentication & Authorization

All endpoints should be secured with JWT authentication. Add the following header:

```http
Authorization: Bearer <jwt_token>
```

**Recommended Role-Based Access:**
- **Enterprise/Company:** Full access to all endpoints for their collaborations
- **Project Manager:** Full access within assigned collaboration
- **Team Members:** Read access + update own tasks/time logs
- **Freelancers:** Read access + update assigned tasks + manage own time logs

---

## 📊 Response Codes

| Code | Description |
|------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 204 | No Content - Deletion successful |
| 400 | Bad Request - Validation error |
| 401 | Unauthorized - Authentication required |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource doesn't exist |
| 409 | Conflict - Business rule violation |
| 500 | Internal Server Error |

---

## 🎯 Common Use Cases

### 1. Kanban Board Workflow
```
1. GET /api/tasks/collaboration/{id}/status/TODO
2. PATCH /api/tasks/{taskId}/move?status=IN_PROGRESS
3. POST /api/task-comments (add progress update)
4. PATCH /api/tasks/{taskId}/move?status=DONE
```

### 2. Time Tracking Workflow
```
1. POST /api/time-logs/start (start timer)
2. POST /api/time-logs/{id}/stop (stop timer)
3. GET /api/time-logs/freelancer/{id}/pending (view pending)
4. POST /api/time-logs/{id}/approve (manager approves)
```

### 3. Sprint Planning Workflow
```
1. POST /api/sprints (create sprint)
2. PUT /api/tasks/{id} (assign tasks to sprint)
3. POST /api/sprints/{id}/start (activate sprint)
4. GET /api/workspace-stats/sprint/{id}/burndown (track progress)
5. POST /api/sprints/{id}/complete (close sprint)
```

### 4. Dashboard Loading
```
1. GET /api/workspace-stats/collaboration/{id}
2. GET /api/workspace-stats/collaboration/{id}/team-workload
3. GET /api/workspace-stats/collaboration/{id}/milestone-progress
4. GET /api/tasks/collaboration/{id}/overdue
```

---

## 🚀 Frontend Integration Tips

1. **Real-time Updates:** Consider WebSocket for live Kanban updates
2. **Drag & Drop:** Use `moveTask` endpoint with new status and orderIndex
3. **Mentions:** Parse @username in comments and populate mentionedUserIds
4. **Timer UI:** Show active timer badge, disable start if timer running
5. **Progress Bars:** Use progressPercentage from stats endpoints
6. **Burndown Chart:** Use sprint burndown data for visualization

---

## 📝 Notes

- All timestamps are in ISO 8601 format
- Pagination can be added to list endpoints if needed
- Consider adding search/filter query parameters
- WebSocket support recommended for real-time collaboration
- File upload endpoints needed for attachments

---

**Version:** 1.0.0  
**Last Updated:** 2024
