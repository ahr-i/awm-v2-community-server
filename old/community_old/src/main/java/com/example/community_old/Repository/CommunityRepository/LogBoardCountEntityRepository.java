package com.example.community_old.Repository.CommunityRepository;

import com.example.community_old.JpaClass.CommunityTable.LogBoardCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface LogBoardCountEntityRepository extends JpaRepository<LogBoardCountEntity,Integer> {
    Optional<LogBoardCountEntity> findByLogBoardEntity_IdAndCountCheckAndUserId(int Id, int countCheck, String userId);
}
