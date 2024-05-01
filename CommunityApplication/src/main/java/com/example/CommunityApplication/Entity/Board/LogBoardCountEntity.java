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
    private String userName;
    @CreationTimestamp
    private LocalDateTime createTime;
    @ManyToOne
    @JoinColumn(name = "postId")
    private BoardEntity boardEntity;
    private int countCheck;

    //좋아요 할때는 1을 추가
    public static LogBoardCountEntity setCount(String userId, BoardEntity countEntity) {
        LogBoardCountEntity entity = new LogBoardCountEntity();
        entity.setUserName(userId);
        entity.setCountCheck(1);
        entity.setBoardEntity(countEntity);
        return entity;
    }
}
