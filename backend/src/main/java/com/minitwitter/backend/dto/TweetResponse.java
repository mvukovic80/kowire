package com.minitwitter.backend.dto;

import com.minitwitter.backend.model.Tweet;

import java.time.Instant;

public record TweetResponse(
        String id,
        String authorId,
        String authorUsername,
        String authorDisplayName,
        String content,
        int likeCount,
        boolean likedByCurrentUser,
        Instant createdAt
) {
    public static TweetResponse from(Tweet tweet, String currentUserId) {
        boolean liked = tweet.getLikedByUserIds() != null
                && currentUserId != null
                && tweet.getLikedByUserIds().contains(currentUserId);

        return new TweetResponse(
                tweet.getId(),
                tweet.getAuthorId(),
                tweet.getAuthorUsername(),
                tweet.getAuthorDisplayName(),
                tweet.getContent(),
                tweet.getLikedByUserIds() == null ? 0 : tweet.getLikedByUserIds().size(),
                liked,
                tweet.getCreatedAt()
        );
    }
}
