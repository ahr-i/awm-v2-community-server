package com.example.community_old.Controller.CommunityController;

import com.example.community_old.Communicator.Authentication.AuthDto;
import com.example.community_old.Communicator.Authentication.AuthenticationCommunicator;
import com.example.community_old.Dto.CommunityDto.BoardDto.BoardDto;
import com.example.community_old.Dto.CommunityDto.BoardDto.UserLogDto;
import com.example.community_old.JpaClass.CommunityTable.BoardEntity;
import com.example.community_old.Service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@RestController
public class BoardController {
    private final BoardService service;
    private final AuthenticationCommunicator authentication;

    @PostMapping("user/board/save/{locationId}")
    public ResponseEntity save(@PathVariable int locationId, @RequestPart("dto") BoardDto dto,
                               @RequestPart(value = "file" , required = false) MultipartFile file, @RequestHeader("Authorization") String jwt){
        AuthDto user = authentication.authentication(jwt);
        if(user == null) {
            return ResponseEntity.badRequest().body("Unauthorized JWT.");
        }

        return service.BoardSave(dto,locationId,file,user.getUserId());
    }

    @GetMapping("/board/findBoard/{postId}")
    @ResponseBody
    public ResponseEntity findByBoard(@PathVariable int postId){
        return service.findByPostAndComment(postId);
    }

    @GetMapping("/user/remove/{postId}")
    public ResponseEntity removeBoard(@PathVariable int postId, @RequestHeader("Authorization") String jwt){
        AuthDto user = authentication.authentication(jwt);
        if(user == null) {
            return ResponseEntity.badRequest().body("Unauthorized JWT.");
        }

        ResponseEntity responseEntity = service.removeBoardPost(postId, user.getUserId());
        return responseEntity;
    }

    @PostMapping("/user/update/{postId}")
    public ResponseEntity updateBoardDto(@PathVariable int postId, @RequestPart("dto") BoardDto dto
            , @RequestPart(value = "file",required = false)MultipartFile file, @RequestHeader("Authorization") String jwt) {
        AuthDto user = authentication.authentication(jwt);
        if(user == null) {
            return ResponseEntity.badRequest().body("Unauthorized JWT.");
        }
        Optional<BoardEntity> board = service.findBoard(postId);

        if(!board.isEmpty()) {
            String userId = user.getUserId();
            if(!userId.equals(board.get().getUserId())) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not the author of the post.");
        }else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The post has been deleted or cannot be found.");

        service.updatePost(dto, file, postId);
        return ResponseEntity.ok("The post has been successfully registered.");

    }


    @GetMapping("/board/paging/{locationId}")
    public ResponseEntity paging(@RequestParam(defaultValue = "0") int page,@PathVariable int locationId){
        Page<BoardDto> findPage = service.page(page, locationId);
        if(findPage==null) {
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.ok().body(findPage);
        }
    }

    /* Alarm */
    @PostMapping("user/log/save/{locationId}")
    public ResponseEntity logUserSave(@RequestBody UserLogDto dto, @PathVariable int locationId, @RequestHeader("Authorization") String jwt) {
        AuthDto user = authentication.authentication(jwt);
        if(user == null) {
            return ResponseEntity.badRequest().body("Unauthorized JWT.");
        }

        return service.saveLogPost(dto,locationId, user.getUserId(), user.getNickName());
    }

    @GetMapping("/log/findBoard/{id}")
    public ResponseEntity DetailLogBoard(@PathVariable int id){
        return service.findByDetailBoard(id);
    }
    @GetMapping("log/paging/{locationId}")
    public ResponseEntity  logPage(@PathVariable int locationId, @RequestParam(defaultValue = "0") int page){
        Page<UserLogDto> logPage = service.findLogPage(page,locationId);

        return logPage == null ? ResponseEntity.status(HttpStatus.NO_CONTENT).body("No content available.")
                : ResponseEntity.ok().body(logPage);
    }
    @DeleteMapping("user/logBoard/delete/{postId}")
    public ResponseEntity deleteLogBoard(@PathVariable int postId, @RequestHeader("Authorization") String jwt) {
        AuthDto user = authentication.authentication(jwt);
        if(user == null) {
            return ResponseEntity.badRequest().body("Unauthorized JWT.");
        }

        ResponseEntity responseEntity = service.deleteLogBoard(postId, user.getUserId());
        return responseEntity;
    }
    @PostMapping("/user/logBoard/likeCount/{postId}")
    public ResponseEntity likeCount(@PathVariable int postId, @RequestHeader("Authorization") String jwt){
        AuthDto user = authentication.authentication(jwt);
        if(user == null) {
            return ResponseEntity.badRequest().body("Unauthorized JWT.");
        }

        ResponseEntity responseEntity = service.checkLogBoardLike(postId, user.getUserId());
        return responseEntity;
    }
    @PostMapping("/user/logBoard/badCount/{postId}")
    public ResponseEntity badCount(@PathVariable int postId, @RequestHeader("Authorization") String jwt){
        AuthDto user = authentication.authentication(jwt);
        if(user == null) {
            return ResponseEntity.badRequest().body("Unauthorized JWT.");
        }

        ResponseEntity responseEntity = service.checkLogBoardBadCount(postId, user.getUserId());
        return responseEntity;
    }
}
