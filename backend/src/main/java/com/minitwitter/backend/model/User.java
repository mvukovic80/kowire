package com.minitwitter.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    // BCrypt-hashed password, never returned to clients
    private String password;

    private String displayName;

    private String bio;

    @Builder.Default
    private Set<String> followerIds = new HashSet<>();

    @Builder.Default
    private Set<String> followingIds = new HashSet<>();

    @CreatedDate
    private Instant createdAt;

    // Defensive getters: @Builder.Default only initializes these when the
    // builder is used. If a document is ever read back without these fields
    // set (e.g. hand-seeded data), the Mongo converter would otherwise leave
    // them null and callers that mutate the set (follow/unfollow) would NPE.
    public Set<String> getFollowerIds() {
        if (followerIds == null) {
            followerIds = new HashSet<>();
        }
        return followerIds;
    }

    public Set<String> getFollowingIds() {
        if (followingIds == null) {
            followingIds = new HashSet<>();
        }
        return followingIds;
    }
}
