package com.freelance.userservice.controller;

import com.freelance.userservice.service.FollowService;
import com.freelance.userservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}")
    public ResponseEntity<Void> follow(@PathVariable Long userId, Authentication authentication) {
        String keycloakId = JwtUtil.extractKeycloakId((Jwt) authentication.getPrincipal());
        followService.follow(userId, keycloakId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unfollow(@PathVariable Long userId, Authentication authentication) {
        String keycloakId = JwtUtil.extractKeycloakId((Jwt) authentication.getPrincipal());
        followService.unfollow(userId, keycloakId);
        return ResponseEntity.ok().build();
    }
}
