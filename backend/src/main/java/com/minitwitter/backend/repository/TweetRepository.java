package com.minitwitter.backend.repository;

import com.minitwitter.backend.model.Tweet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface TweetRepository extends MongoRepository<Tweet, String> {

    List<Tweet> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Tweet> findByAuthorIdOrderByCreatedAtDesc(String authorId, Pageable pageable);

    List<Tweet> findByAuthorIdInOrderByCreatedAtDesc(Collection<String> authorIds, Pageable pageable);

    long countByAuthorId(String authorId);
}
