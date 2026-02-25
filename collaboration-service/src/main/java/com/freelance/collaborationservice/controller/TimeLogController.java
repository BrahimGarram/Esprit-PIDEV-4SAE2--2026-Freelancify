package com.freelance.collaborationservice.controller;

import com.freelance.collaborationservice.dto.CreateTimeLogRequest;
import com.freelance.collaborationservice.dto.TimeLogDTO;
import com.freelance.collaborationservice.service.TimeLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class TimeLogController {

    private final TimeLogService timeLogService;

    @PostMapping
    @PreAuthorize("hasAnyRole('FREELANCER', 'ADMIN')")
    public ResponseEntity<TimeLogDTO> createTimeLog(@Valid @RequestBody CreateTimeLogRequest request) {
        log.info("REST request to create time log for task: {}", request.getTaskId());
        TimeLogDTO timeLog = timeLogService.createTimeLog(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(timeLog);
    }

    @PostMapping("/start")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ADMIN')")
    public ResponseEntity<TimeLogDTO> startTimer(
            @RequestParam Long taskId,
            @RequestParam Long freelancerId) {
        log.info("REST request to start timer for task {} by freelancer {}", taskId, freelancerId);
        TimeLogDTO timeLog = timeLogService.startTimer(taskId, freelancerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(timeLog);
    }

    @PostMapping("/{timeLogId}/stop")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ADMIN')")
    public ResponseEntity<TimeLogDTO> stopTimer(@PathVariable Long timeLogId) {
        log.info("REST request to stop timer for time log: {}", timeLogId);
        TimeLogDTO timeLog = timeLogService.stopTimer(timeLogId);
        return ResponseEntity.ok(timeLog);
    }

    @PutMapping("/{timeLogId}")
    @PreAuthorize("hasAnyRole('FREELANCER', 'ADMIN')")
    public ResponseEntity<TimeLogDTO> updateTimeLog(
            @PathVariable Long timeLogId,
            @Valid @RequestBody CreateTimeLogRequest request) {
        log.info("REST request to update time log: {}", timeLogId);
        TimeLogDTO timeLog = timeLogService.updateTimeLog(timeLogId, request);
        return ResponseEntity.ok(timeLog);
    }

    @PostMapping("/{timeLogId}/approve")
    @PreAuthorize("hasAnyRole('ENTERPRISE', 'ADMIN')")
    public ResponseEntity<TimeLogDTO> approveTimeLog(@PathVariable Long timeLogId) {
        log.info("REST request to approve time log: {}", timeLogId);
        TimeLogDTO timeLog = timeLogService.approveTimeLog(timeLogId);
        return ResponseEntity.ok(timeLog);
    }

    @PostMapping("/{timeLogId}/reject")
    public ResponseEntity<TimeLogDTO> rejectTimeLog(@PathVariable Long timeLogId) {
        log.info("REST request to reject time log: {}", timeLogId);
        TimeLogDTO timeLog = timeLogService.rejectTimeLog(timeLogId);
        return ResponseEntity.ok(timeLog);
    }

    @DeleteMapping("/{timeLogId}")
    public ResponseEntity<Void> deleteTimeLog(@PathVariable Long timeLogId) {
        log.info("REST request to delete time log: {}", timeLogId);
        timeLogService.deleteTimeLog(timeLogId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TimeLogDTO>> getTimeLogsByTask(@PathVariable Long taskId) {
        log.info("REST request to get time logs for task: {}", taskId);
        List<TimeLogDTO> timeLogs = timeLogService.getTimeLogsByTask(taskId);
        return ResponseEntity.ok(timeLogs);
    }

    @GetMapping("/freelancer/{freelancerId}")
    public ResponseEntity<List<TimeLogDTO>> getTimeLogsByFreelancer(@PathVariable Long freelancerId) {
        log.info("REST request to get time logs for freelancer: {}", freelancerId);
        List<TimeLogDTO> timeLogs = timeLogService.getTimeLogsByFreelancer(freelancerId);
        return ResponseEntity.ok(timeLogs);
    }

    @GetMapping("/freelancer/{freelancerId}/pending")
    public ResponseEntity<List<TimeLogDTO>> getPendingTimeLogs(@PathVariable Long freelancerId) {
        log.info("REST request to get pending time logs for freelancer: {}", freelancerId);
        List<TimeLogDTO> timeLogs = timeLogService.getPendingTimeLogs(freelancerId);
        return ResponseEntity.ok(timeLogs);
    }

    @GetMapping("/freelancer/{freelancerId}/active")
    public ResponseEntity<List<TimeLogDTO>> getActiveTimeLogs(@PathVariable Long freelancerId) {
        log.info("REST request to get active time logs for freelancer: {}", freelancerId);
        List<TimeLogDTO> timeLogs = timeLogService.getActiveTimeLogs(freelancerId);
        return ResponseEntity.ok(timeLogs);
    }

    @GetMapping("/task/{taskId}/total-hours")
    public ResponseEntity<Integer> getTotalApprovedHours(@PathVariable Long taskId) {
        log.info("REST request to get total approved hours for task: {}", taskId);
        Integer totalHours = timeLogService.getTotalApprovedHours(taskId);
        return ResponseEntity.ok(totalHours);
    }
}
