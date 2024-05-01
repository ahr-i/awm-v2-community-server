package com.example.CommunityApplication.Dto;

import com.example.CommunityApplication.Dto.BoardDto.BoardDto;
import com.example.CommunityApplication.Entity.Comment.CommentEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    BoardDto boardDto;
    List<CommentEntity> entityList;
    Long commentCount;

    public Response(BoardDto dto, List<CommentEntity> commentEntities, Long commentCount) {
        this.boardDto = dto;
        this.entityList = commentEntities;
        if (commentCount != null) this.commentCount = commentCount;
        else this.commentCount = Long.valueOf(0);
    }
}
