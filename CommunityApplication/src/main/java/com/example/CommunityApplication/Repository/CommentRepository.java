package com.example.CommunityApplication.Repository;

import com.example.CommunityApplication.Entity.Board.BoardEntity;
import com.example.CommunityApplication.Entity.Comment.CommentEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Transactional
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
//    @Query("SELECT c FROM CommentEntity c WHERE c.boardEntity.postId = :postId ORDER BY c.createTime DESC")
//    List<CommentEntity> findCommentOrderByCreateTimeDesc(int postId);

    List<CommentEntity> findAllByBoardEntityPostIdOrderByCreateTimeDesc(int postId);

    Long countAllByBoardEntity(BoardEntity boardEntity);
}
