package com.example.CommunityApplication.Entity.Review;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class LogReviewCountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String userName;
    @CreationTimestamp
    private LocalDateTime createTime;
    @ManyToOne
    @JoinColumn(name = "reviewId")
    @JsonBackReference
    private ReviewEntity reviewEntity;
    private int countCheck;

    //좋아요 or 싫어요 할때는 1을 추가
    public static LogReviewCountEntity setCount(String userId, ReviewEntity countEntity) {
        LogReviewCountEntity entity = new LogReviewCountEntity();
        entity.setUserName(userId);
        entity.setCountCheck(1);
        entity.setReviewEntity(countEntity);
        return entity;
    }
}
