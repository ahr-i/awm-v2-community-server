package com.example.CommunityApplication.Repository.Review;

import com.example.CommunityApplication.Entity.Board.BoardEntity;
import com.example.CommunityApplication.Entity.Review.ReviewEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Transactional
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {
    List<ReviewEntity> findAllByLocation_LocationId(int locationId);

    @Query("SELECT r FROM ReviewEntity r WHERE r.location.locationId = :locationId")
    List<ReviewEntity> findReviewEntities(int locationId);

    // 좋아요 증가
    @Modifying
    @Query(value = "update ReviewEntity  b set b.likeCount = b.likeCount + 1 where b.reviewId = :reviewId")
    void updateLikeCountHit(@Param("reviewId") int reviewId);
    // 싫어요 증가
    @Modifying
    @Query(value = "update ReviewEntity b set b.badCount = b.badCount + 1 where b.reviewId = :reviewId")
    void updateBadCountHit(@Param("reviewId") int reviewId);
    // 신고 횟수 증가
    @Modifying
    @Query(value = "update ReviewEntity b set b.reportCount = b.reportCount + 1 where b.reviewId = :reviewId")
    void updateReportCountHit(@Param("reviewId") int reviewId);
}
