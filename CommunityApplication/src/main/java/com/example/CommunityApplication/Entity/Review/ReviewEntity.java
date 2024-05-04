package com.example.CommunityApplication.Entity.Review;

import com.example.CommunityApplication.Entity.Location.Location;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Table(name = "review_table")
@Entity
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewId;
    // 작성자 닉네임
    @Column(length = 20)
    private String boardWriter;
    // 작성자 ID
    @Column
    private String userName;
    // 내용
    @Column
    private String boardContent;
    // 신고 횟수
    @Column
    private int reportCount;
    // 좋아요 횟수
    @Column
    private int likeCount;
    // 싫어요 횟수
    @Column
    private int badCount;
    // 생성시간
    @CreationTimestamp
    private LocalDateTime createTime;

    @ManyToOne
    @JoinColumn(name = "location_id")
    public Location location;
}
