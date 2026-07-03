package com.minitwitter.backend.service;

import com.minitwitter.backend.dto.TweetRequest;
import com.minitwitter.backend.dto.TweetResponse;
import com.minitwitter.backend.exception.ApiException;
import com.minitwitter.backend.model.Tweet;
import com.minitwitter.backend.model.User;
import com.minitwitter.backend.repository.TweetRepository;
import com.minitwitter.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetResponse createTweet(String authorId, TweetRequest request) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        Tweet tweet = Tweet.builder()
                .authorId(author.getId())
                .authorUsername(author.getUsername())
                .authorDisplayName(author.getDisplayName())
                .content(request.content())
                .createdAt(Instant.now())
                .build();

        Tweet saved = tweetRepository.save(tweet);
        return TweetResponse.from(saved, authorId);
    }

    /**
     * Feed: tweets from people the current user follows, plus their own tweets.
     * Falls back to a global feed if the user follows no one yet, so new users
     * see content instead of an empty page.
     */
    public List<TweetResponse> getFeed(String currentUserId, int page, int size) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        Set<String> authorIds = new HashSet<>(currentUser.getFollowingIds());
        authorIds.add(currentUserId);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<Tweet> tweets = tweetRepository.findByAuthorIdInOrderByCreatedAtDesc(authorIds, pageRequest);

        return tweets.stream()
                .map(t -> TweetResponse.from(t, currentUserId))
                .toList();
    }

    public List<TweetResponse> getGlobalFeed(String currentUserId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Tweet> tweets = tweetRepository.findAllByOrderByCreatedAtDesc(pageRequest);

        return tweets.stream()
                .map(t -> TweetResponse.from(t, currentUserId))
                .toList();
    }

    public List<TweetResponse> getUserTweets(String username, String currentUserId, int page, int size) {
        User author = userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> ApiException.notFound("User not found"));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Tweet> tweets = tweetRepository.findByAuthorIdOrderByCreatedAtDesc(author.getId(), pageRequest);

        return tweets.stream()
                .map(t -> TweetResponse.from(t, currentUserId))
                .toList();
    }

    public TweetResponse toggleLike(String currentUserId, String tweetId) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> ApiException.notFound("Tweet not found"));

        if (tweet.getLikedByUserIds().contains(currentUserId)) {
            tweet.getLikedByUserIds().remove(currentUserId);
        } else {
            tweet.getLikedByUserIds().add(currentUserId);
        }

        Tweet saved = tweetRepository.save(tweet);
        return TweetResponse.from(saved, currentUserId);
    }

    public void deleteTweet(String currentUserId, String tweetId) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> ApiException.notFound("Tweet not found"));

        if (!tweet.getAuthorId().equals(currentUserId)) {
            throw ApiException.forbidden("You can only delete your own tweets");
        }

        tweetRepository.delete(tweet);
    }
}
