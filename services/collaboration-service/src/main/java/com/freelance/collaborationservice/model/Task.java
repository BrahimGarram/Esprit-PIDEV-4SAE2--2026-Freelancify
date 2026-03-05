package com.freelance.collaborationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "collaboration_id", nullable = false)
    private Long collaborationId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(name = "assigned_freelancer_id", nullable = false)
    private Long assignedFreelancerId;

    @Column(name = "assigned_to")
    private Long assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.TODO;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "estimated_hours", nullable = false)
    private Integer estimatedHours = 0;

    @Column(name = "actual_hours", nullable = false)
    private Integer actualHours = 0;

    @Column(columnDefinition = "TEXT")
    private String attachments;

    @Column(name = "milestone_id")
    private Long milestoneId;

    @Column(name = "parent_task_id")
    private Long parentTaskId;

    @Column(name = "sprint_id")
    private Long sprintId;

    @ElementCollection
    @CollectionTable(name = "task_dependencies", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "depends_on_task_id")
    private List<Long> dependsOnTaskIds = new ArrayList<>();

    @Column(name = "order_index")
    private Integer orderIndex = 0;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (status == TaskStatus.DONE && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }
}
