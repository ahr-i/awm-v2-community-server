package com.example.CommunityApplication.Repository.Board;

import com.example.CommunityApplication.Entity.Board.LogBoardCountEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Transactional
public interface LogBoardCountEntityRepository extends JpaRepository<LogBoardCountEntity, Integer> {
    //Optional<LogBoardCountEntity> findByLogBoardEntity_IdAndCountCheckAndUserId(int Id, int countCheck, String userName);

    Optional<LogBoardCountEntity> findLogBoardCountEntityByBoardEntity_PostIdAndCountCheckAndUserName(int postId, int countCheck, String userName);
}
