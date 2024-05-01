package com.example.CommunityApplication.Entity.Board;

import com.example.CommunityApplication.Entity.Comment.CommentEntity;
import com.example.CommunityApplication.Entity.Location.Location;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "board_table")
@Entity
public class BoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId;
    // 작성자 닉네임
    @Column(length = 20)
    private String boardWriter;
    // 작성자 ID
    @Column
    private String userName;
    // 제목
    @Column
    private String boardTitle;
    // 내용
    @Column
    private String boardContent;
    // 조회수
    @Column
    private int boardHits;
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
    // 이미지 파일
    @Column(columnDefinition = "LONGBLOB")
    @Lob
    private byte[] imageFile;

    @ManyToOne
    @JoinColumn(name = "location_id")
    public Location location;

    @JsonManagedReference
    @OneToMany(mappedBy = "entity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<CommentEntity> entityList = new ArrayList<>();
}
