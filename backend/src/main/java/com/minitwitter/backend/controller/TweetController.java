package com.minitwitter.backend.controller;

import com.minitwitter.backend.dto.TweetRequest;
import com.minitwitter.backend.dto.TweetResponse;
import com.minitwitter.backend.model.User;
import com.minitwitter.backend.service.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tweets")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;

    @PostMapping
    public ResponseEntity<TweetResponse> createTweet(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody TweetRequest request
    ) {
        return ResponseEntity.ok(tweetService.createTweet(currentUser.getId(), request));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<TweetResponse>> getFeed(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(tweetService.getFeed(currentUser.getId(), page, size));
    }

    @GetMapping("/explore")
    public ResponseEntity<List<TweetResponse>> getExploreFeed(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(tweetService.getGlobalFeed(currentUser.getId(), page, size));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<TweetResponse>> getUserTweets(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(tweetService.getUserTweets(username, currentUser.getId(), page, size));
    }

    @PostMapping("/{tweetId}/like")
    public ResponseEntity<TweetResponse> toggleLike(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String tweetId
    ) {
        return ResponseEntity.ok(tweetService.toggleLike(currentUser.getId(), tweetId));
    }

    @DeleteMapping("/{tweetId}")
    public ResponseEntity<Void> deleteTweet(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String tweetId
    ) {
        tweetService.deleteTweet(currentUser.getId(), tweetId);
        return ResponseEntity.noContent().build();
    }
}
