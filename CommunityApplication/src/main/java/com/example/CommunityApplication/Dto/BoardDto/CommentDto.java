package com.example.CommunityApplication.Dto.BoardDto;

import com.example.CommunityApplication.Entity.Board.BoardEntity;
import com.example.CommunityApplication.Entity.Comment.CommentEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
public class CommentDto {
    private String userName;
    private String commentWriter;
    private String commentContent;
    private LocalDateTime createTime;
    private int postId;

    public static CommentEntity convertToEntity(CommentDto dto, Optional<BoardEntity> boardEntity) {
        CommentEntity entity = new CommentEntity();
        entity.setUserName(dto.userName);
        entity.setCommentWriter(dto.commentWriter);
        entity.setCommentContent(dto.commentContent);
        entity.setReport(0);
        entity.setLikeCount(0);
        entity.setEntity(boardEntity.get());

        return entity;
    }
}
