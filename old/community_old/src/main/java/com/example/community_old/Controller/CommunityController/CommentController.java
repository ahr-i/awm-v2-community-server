package com.example.community_old.Controller.CommunityController;

import com.example.community_old.Communicator.AuthDto;
import com.example.community_old.Communicator.AuthenticationCommunicator;
import com.example.community_old.Dto.CommunityDto.BoardDto.CommentDto;
import com.example.community_old.Service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user/comment/")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;
    private final AuthenticationCommunicator authentication;

    @PostMapping("/save/{postId}")
    public ResponseEntity save(@RequestBody CommentDto dto, @PathVariable int postId, @RequestHeader("Authorization") String jwt){
        AuthDto user = authentication.authentication(jwt);
        if(user == null) {
            return ResponseEntity.badRequest().body("Unauthorized JWT.");
        }

        return  service.save(dto, postId, user.getUserId());
    }

}
