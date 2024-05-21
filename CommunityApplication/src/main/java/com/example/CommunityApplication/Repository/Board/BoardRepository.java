package com.example.CommunityApplication.Repository.Board;

import com.example.CommunityApplication.Entity.Board.BoardEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Transactional
public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {
    // 조회수 증가
    @Modifying
    @Query("update BoardEntity b set b.boardHits = b.boardHits + 1 where b.postId = :postId")
    void updateHit(@Param("postId") int postId);

    // 좋아요 증가
    @Modifying
    @Query("update BoardEntity b set b.likeCount = b.likeCount + 1 where b.postId = :postId")
    void updateLikeCountHit(@Param("postId") int postId);

    // 싫어요 증가
    @Modifying
    @Query("update BoardEntity b set b.badCount = b.badCount + 1 where b.postId = :postId")
    void updateBadCountHit(@Param("postId") int postId);

    // 신고 횟수 증가
    @Modifying
    @Query("update BoardEntity b set b.reportCount = b.reportCount + 1 where b.postId = :postId")
    void updateReportCountHit(@Param("postId") int postId);

    Page<BoardEntity> findAllByLocation_LocationId(int locationId, Pageable pageable);

    // 해당 장소의 게시글 반환
    List<BoardEntity> findAllByLocation_LocationId(int locationId);
}
