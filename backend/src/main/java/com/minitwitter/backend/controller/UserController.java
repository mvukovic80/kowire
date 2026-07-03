package com.minitwitter.backend.controller;

import com.minitwitter.backend.dto.UserResponse;
import com.minitwitter.backend.model.User;
import com.minitwitter.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(UserResponse.from(currentUser));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getProfile(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String username
    ) {
        return ResponseEntity.ok(userService.getProfile(username, currentUser.getId()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(
            @AuthenticationPrincipal User currentUser,
            @RequestParam String query
    ) {
        return ResponseEntity.ok(userService.searchUsers(query, currentUser.getId()));
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<UserResponse> follow(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String username
    ) {
        return ResponseEntity.ok(userService.follow(currentUser.getId(), username));
    }

    @DeleteMapping("/{username}/follow")
    public ResponseEntity<UserResponse> unfollow(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String username
    ) {
        return ResponseEntity.ok(userService.unfollow(currentUser.getId(), username));
    }
}
