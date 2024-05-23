package com.example.CommunityApplication.Controller.Comment;

import com.example.CommunityApplication.Dto.BoardDto.CommentDto;
import com.example.CommunityApplication.Service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;

    // 댓글 등록
    @PostMapping("user/comment/save/{postId}")
    public ResponseEntity saveComment(@PathVariable int postId, @RequestPart CommentDto dto, @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken){
        String jwtToken = authToken.replace("Bearer ", "");
        return service.save(dto, postId, jwtToken);
    }

    // 댓글 삭제
    @PostMapping("user/comment/remove/{commentId}")
    public ResponseEntity removeComment(@PathVariable int commentId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken){
        String jwtToken = authToken.replace("Bearer ", "");
        return service.removeComment(commentId, jwtToken);
    }
}
