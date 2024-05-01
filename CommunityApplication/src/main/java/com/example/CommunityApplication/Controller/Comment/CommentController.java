package com.example.CommunityApplication.Controller.Comment;

import com.example.CommunityApplication.Dto.BoardDto.CommentDto;
import com.example.CommunityApplication.Service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;

    // 댓글 등록
    @PostMapping("user/comment/save/{postId}")
    public ResponseEntity saveComment(@PathVariable int postId, @RequestBody CommentDto dto){
        return service.save(dto, postId);
    }

    // 댓글 삭제
    @PostMapping("user/comment/remove/{commentId}")
    public ResponseEntity removeComment(@PathVariable int commentId){
        return service.removeComment(commentId);
    }
}
