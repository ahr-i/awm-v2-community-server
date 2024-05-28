package com.example.CommunityApplication.Controller.Board;

import com.example.CommunityApplication.Dto.BoardDto.BoardDto;
import com.example.CommunityApplication.Service.BoardService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class BoardController {
    private final BoardService service;

    // 게시글 생성
    @PostMapping("user/post/save/{locationId}")
    public ResponseEntity save(@PathVariable int locationId, @RequestPart(value = "dto", required = true) BoardDto dto,
                               @RequestPart(value = "file", required = false) MultipartFile file,
                               @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken) {
        // Authorization에서 "Bearer " 부분 제거
        String jwtToken = authToken.replace("Bearer ", "");
        return service.saveBoard(dto, locationId, file, jwtToken);
    }


    // 게시글 전체 리스트 조회
    @GetMapping("user/post/lists/{locationId}")
    public ResponseEntity paging(@RequestParam(defaultValue = "0") int page, @PathVariable int locationId) {
        Page<BoardDto> findPage = service.page(page, locationId);
        if (findPage == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok().body(findPage);
        }
    }

    // 게시글 상세 조회
    @GetMapping("/user/post/view/{postId}")
    public ResponseEntity findByBoard(@PathVariable int postId) {
        return service.findByBoardAndComment(postId);
    }

    // 게시글 삭제
    @PostMapping("/user/post/remove/{postId}")
    public ResponseEntity removeBoard(@PathVariable int postId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken) {
        // Authorization에서 "Bearer " 부분 제거
        String jwtToken = authToken.replace("Bearer ", "");
        return service.removeBoard(postId, jwtToken);
    }

    // 게시글 수정
    @PostMapping("/user/post/update/{postId}")
    public ResponseEntity updateBoard(@PathVariable int postId, @RequestPart("dto") BoardDto dto,
                                      @RequestPart(value = "file", required = false) MultipartFile file,
                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken) {
        // Authorization에서 "Bearer " 부분 제거
        String jwtToken = authToken.replace("Bearer ", "");
        return service.updateBoard(dto, file, postId, jwtToken);
    }

    // 게시글 좋아요
    @PostMapping("/user/logBoard/like/{postId}")
    public ResponseEntity likeCount(@PathVariable int postId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken){
        // Authorization에서 "Bearer " 부분 제거
        String jwtToken = authToken.replace("Bearer ", "");
        return service.checkLogBoardLike(postId, jwtToken);
    }

    // 게시글 싫어요
    @PostMapping("/user/logBoard/bad/{postId}")
    public ResponseEntity badCount(@PathVariable int postId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken){
        // Authorization에서 "Bearer " 부분 제거
        String jwtToken = authToken.replace("Bearer ", "");
        return service.checkLogBoardBad(postId, jwtToken);
    }

    // 게시글 신고
    @PostMapping("/user/logBoard/report/{postId}")
    public ResponseEntity reportCount(@PathVariable int postId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken){
        // Authorization에서 "Bearer " 부분 제거
        String jwtToken = authToken.replace("Bearer ", "");
        return service.checkLogBoardReport(postId, jwtToken);
    }
}
