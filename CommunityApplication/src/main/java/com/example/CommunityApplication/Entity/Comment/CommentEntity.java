package com.example.CommunityApplication.Entity.Comment;

import com.example.CommunityApplication.Entity.Board.BoardEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "comment_table")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    // 댓글 작성자 ID
    @Column
    private String userName;
    // 댓글 작성자 닉네임
    @Column
    private String commentWriter;
    // 댓글 내용
    @Column
    private String commentContent;
    // 신고
    @Column
    private int report;
    // 좋아요
    @Column
    private int likeCount;
    // 싫어요
    @Column
    private int badCount;
    // 생성시간
    @CreationTimestamp
    private LocalDateTime createTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    @JsonBackReference
    private BoardEntity boardEntity;
}
