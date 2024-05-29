package com.example.community_old.Repository.CommunityRepository;

import com.example.community_old.JpaClass.CommunityTable.BoardEntity;
import com.example.community_old.JpaClass.CommunityTable.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CommentRepository extends JpaRepository<CommentEntity,Integer> {

    List<CommentEntity> findAllByEntityOrderByCreatTimeDesc(BoardEntity entity);
    Long countAllByEntity(BoardEntity entity);
}
