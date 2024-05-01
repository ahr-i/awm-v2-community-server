package com.example.CommunityApplication.Controller.Board;

import com.example.CommunityApplication.Dto.BoardDto.BoardDto;
import com.example.CommunityApplication.Service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class BoardController {
    private final BoardService service;

    // 게시글 생성, jwt 토큰 받는 코드 추가 - user가 회원이 맞는지 Authentication 서버에서 확인
    @PostMapping("user/post/save/{locationId}")
    public ResponseEntity save(@PathVariable int locationId/*, @RequestPart(value = "dto", required = false) BoardDto dto,
                               @RequestPart(value = "file", required = false) MultipartFile file*/) {
        return service.saveBoard(null, locationId, null);
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

    // 게시글 삭제, jwt 토큰 받는 코드 추가 - user가 회원이 맞는지 Authentication 서버에서 확인
    @PostMapping("/user/post/remove/{postId}")
    public ResponseEntity removeBoard(@PathVariable int postId) {
        return service.removeBoard(postId);
    }

    // 게시글 수정, jwt 토큰 받는 코드 추가 - user가 회원이 맞는지 Authentication 서버에서 확인
    @PostMapping("/user/post/update/{postId}")
    public ResponseEntity updateBoard(@PathVariable int postId, @RequestPart("dto") BoardDto dto,
                                      @RequestPart(value = "file", required = false) MultipartFile file) {
        return service.updateBoard(dto, file, postId);
    }

    // 게시글 좋아요, jwt 토큰 받는 코드 추가 - user가 회원이 맞는지 Authentication 서버에서 확인
    @PostMapping("/user/logBoard/like/{postId}")
    public ResponseEntity likeCount(@PathVariable int postId){
        return service.checkLogBoardLike(postId);
    }

    // 게시글 싫어요, jwt 토큰 받는 코드 추가 - user가 회원이 맞는지 Authentication 서버에서 확인
    @PostMapping("/user/logBoard/bad/{postId}")
    public ResponseEntity badCount(@PathVariable int postId){
        return service.checkLogBoardBad(postId);
    }

    // 게시글 신고, jwt 토큰 받는 코드 추가 - user가 회원이 맞는지 Authentication 서버에서 확인
    @PostMapping("/user/logBoard/report/{postId}")
    public ResponseEntity reportCount(@PathVariable int postId){
        return service.checkLogBoardReport(postId);
    }
}
