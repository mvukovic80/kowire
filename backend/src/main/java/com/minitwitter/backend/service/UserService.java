package com.minitwitter.backend.service;

import com.minitwitter.backend.dto.UserResponse;
import com.minitwitter.backend.exception.ApiException;
import com.minitwitter.backend.model.User;
import com.minitwitter.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserOrThrow(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found"));
    }

    public UserResponse getProfile(String username, String currentUserId) {
        User user = userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> ApiException.notFound("User not found"));

        boolean isFollowed = currentUserId != null
                && user.getFollowerIds() != null
                && user.getFollowerIds().contains(currentUserId);

        return UserResponse.from(user, isFollowed);
    }

    public List<UserResponse> searchUsers(String query, String currentUserId) {
        return userRepository.findByUsernameContainingIgnoreCase(query)
                .stream()
                .map(u -> UserResponse.from(u, currentUserId != null
                        && u.getFollowerIds() != null
                        && u.getFollowerIds().contains(currentUserId)))
                .toList();
    }

    public UserResponse follow(String currentUserId, String targetUsername) {
        User target = userRepository.findByUsername(targetUsername.toLowerCase())
                .orElseThrow(() -> ApiException.notFound("User not found"));

        if (target.getId().equals(currentUserId)) {
            throw ApiException.badRequest("You cannot follow yourself");
        }

        User current = getUserOrThrow(currentUserId);

        current.getFollowingIds().add(target.getId());
        target.getFollowerIds().add(current.getId());

        userRepository.save(current);
        User savedTarget = userRepository.save(target);

        return UserResponse.from(savedTarget, true);
    }

    public UserResponse unfollow(String currentUserId, String targetUsername) {
        User target = userRepository.findByUsername(targetUsername.toLowerCase())
                .orElseThrow(() -> ApiException.notFound("User not found"));

        User current = getUserOrThrow(currentUserId);

        current.getFollowingIds().remove(target.getId());
        target.getFollowerIds().remove(current.getId());

        userRepository.save(current);
        User savedTarget = userRepository.save(target);

        return UserResponse.from(savedTarget, false);
    }
}
