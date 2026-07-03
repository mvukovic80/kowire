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
@Document(collection = "tweets")
public class Tweet {

    @Id
    private String id;

    @Indexed
    private String authorId;

    private String authorUsername;

    private String authorDisplayName;

    private String content;

    @Builder.Default
    private Set<String> likedByUserIds = new HashSet<>();

    @CreatedDate
    @Indexed
    private Instant createdAt;

    // Defensive getter: see User.getFollowerIds() for why this guards against null.
    public Set<String> getLikedByUserIds() {
        if (likedByUserIds == null) {
            likedByUserIds = new HashSet<>();
        }
        return likedByUserIds;
    }
}
