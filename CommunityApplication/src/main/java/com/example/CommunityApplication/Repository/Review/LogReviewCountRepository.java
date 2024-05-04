package com.example.CommunityApplication.Repository.Review;

import com.example.CommunityApplication.Entity.Board.LogBoardCountEntity;
import com.example.CommunityApplication.Entity.Review.LogReviewCountEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Transactional
public interface LogReviewCountRepository extends JpaRepository<LogReviewCountEntity, Integer> {
    Optional<LogReviewCountEntity> findLogReviewCountEntityByReviewEntity_ReviewIdAndCountCheckAndUserName(int reviewId, int countCheck, String userName);
}
