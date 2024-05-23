package com.example.CommunityApplication.Entity.Board;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class LogBoardCountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    // 좋아요, 싫어요를 누른 유저의 Id
    private String userName;
    // 좋아요, 싫어요를 할 시 1로 set
    private int countCheck;
    @CreationTimestamp
    private LocalDateTime createTime;
    @ManyToOne
    @JoinColumn(name = "postId")
    private BoardEntity boardEntity;


    //좋아요 or 싫어요 할때는 1을 추가
    public static LogBoardCountEntity setCount(String userId, BoardEntity boardEntity) {
        LogBoardCountEntity entity = new LogBoardCountEntity();
        entity.setUserName(userId);
        entity.setCountCheck(1);
        entity.setBoardEntity(boardEntity);
        return entity;
    }
}
