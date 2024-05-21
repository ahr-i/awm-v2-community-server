package com.example.CommunityApplication.Repository.Board;

import com.example.CommunityApplication.Entity.Board.LogBoardCountEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Transactional
public interface LogBoardCountEntityRepository extends JpaRepository<LogBoardCountEntity, Integer> {
    //Optional<LogBoardCountEntity> findByBoardEntity_PostIdAndCountCheckAndUserName(int postId, String userName);

    Optional<LogBoardCountEntity> findByBoardEntityPostIdAndUserName(int postId, String userName);
}
