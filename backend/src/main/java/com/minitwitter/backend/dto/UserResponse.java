package com.minitwitter.backend.dto;

import com.minitwitter.backend.model.User;

import java.time.Instant;

public record UserResponse(
        String id,
        String username,
        String displayName,
        String bio,
        int followerCount,
        int followingCount,
        boolean isFollowedByCurrentUser,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return from(user, false);
    }

    public static UserResponse from(User user, boolean isFollowedByCurrentUser) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getBio(),
                user.getFollowerIds() == null ? 0 : user.getFollowerIds().size(),
                user.getFollowingIds() == null ? 0 : user.getFollowingIds().size(),
                isFollowedByCurrentUser,
                user.getCreatedAt()
        );
    }
}
