package com.example.CommunityApplication.Controller.Review;

import com.example.CommunityApplication.Dto.ReviewDto.ReviewDto;
import com.example.CommunityApplication.Service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;

    // 리뷰 생성
    @PostMapping("/user/review/save/{locationId}")
    public ResponseEntity save(@PathVariable int locationId, @RequestPart(value = "dto", required = true)ReviewDto dto,
                               @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken){
        String jwtToken = authToken.replace("Bearer ", "");
        return service.saveReview(dto, locationId, jwtToken);
    }
    // 리뷰 조회
    @GetMapping("/user/review/view/{locationId}")
    public ResponseEntity find(@PathVariable int locationId){
        return service.findReview(locationId);
    }
    // 리뷰 삭제
    @PostMapping("/user/review/remove/{reviewId}")
    public ResponseEntity remove(@PathVariable int reviewId,
                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken){
        String jwtToken = authToken.replace("Bearer ", "");
        return service.deleteReview(reviewId, jwtToken);
    }
    // 좋아요
    @PostMapping("/user/logReview/like/{reviewId}")
    public ResponseEntity likeCount(@PathVariable int reviewId,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken){
        String jwtToken = authToken.replace("Bearer ", "");
        return service.checkLogReviewLike(reviewId, jwtToken);
    }
    // 싫어요
    @PostMapping("/user/logReview/bad/{reviewId}")
    public ResponseEntity badCount(@PathVariable int reviewId,
                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken){
        String jwtToken = authToken.replace("Bearer ", "");
        return service.checkLogReviewBad(reviewId, jwtToken);
    }
    // 신고
    @PostMapping("/user/logReview/report/{reviewId}")
    public ResponseEntity reportCount(@PathVariable int reviewId,
                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken){
        String jwtToken = authToken.replace("Bearer ", "");
        return service.checkLogReviewReport(reviewId, jwtToken);
    }
}
