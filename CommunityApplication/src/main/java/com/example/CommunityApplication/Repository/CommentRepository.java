package com.example.CommunityApplication.Repository;

import com.example.CommunityApplication.Entity.Board.BoardEntity;
import com.example.CommunityApplication.Entity.Comment.CommentEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Transactional
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> findAllByEntityOrderByCreateTimeDesc(BoardEntity boardEntity);

    List<CommentEntity> findAllByEntity_postIdOrderByCreateTimeDesc(int postId);

    Long countAllByEntity(BoardEntity boardEntity);
}
