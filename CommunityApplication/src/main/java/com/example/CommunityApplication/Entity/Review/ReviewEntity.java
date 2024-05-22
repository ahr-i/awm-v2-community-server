package com.example.CommunityApplication.Entity.Review;

import com.example.CommunityApplication.Entity.Location.Location;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "review_table")
@Entity
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewId;
    // 작성자 ID
    @Column
    private String userName;
    // 작성자 닉네임
    @Column
    private String reviewWriter;
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
    @JoinColumn(name = "locationId")
    @JsonBackReference
    private Location location;

    @JsonManagedReference
    @OneToMany(mappedBy = "reviewEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<LogReviewCountEntity> logReviewCountEntities = new ArrayList<>();
}
