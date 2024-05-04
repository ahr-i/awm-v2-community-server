package com.example.CommunityApplication.Service;

import com.example.CommunityApplication.ApplicationProperties;
import com.example.CommunityApplication.Dto.ReviewDto.ReviewDto;
import com.example.CommunityApplication.Entity.Location.Location;
import com.example.CommunityApplication.Entity.Review.LogReviewCountEntity;
import com.example.CommunityApplication.Entity.Review.ReviewEntity;
import com.example.CommunityApplication.HttpRequest.HttpRequest;
import com.example.CommunityApplication.Repository.Location.LocationRepository;
import com.example.CommunityApplication.Repository.Review.LogReviewCountRepository;
import com.example.CommunityApplication.Repository.Review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final LocationRepository locationRepository;
    private final ReviewRepository reviewRepository;
    private final LogReviewCountRepository logReviewCountRepository;
    private final HttpRequest httpRequest;
    private final ApplicationProperties applicationProperties;

    public ResponseEntity saveReview(ReviewDto dto, int locationId){
        try {
            Optional<Location> locationEntity = locationRepository.findById(locationId);
            // 게시글을 등록할 장소가 존재하지 않는 경우
            if (locationEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 장소를 찾을 수 없습니다.");
            }
            // Authentication 서버에서 회원이 맞는지 확인 요청
            // 회원이 아닌 경우
            String testToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImRuanN3bnMxOTkyIiwicHJvdmlkZXIiOiJGb3JtTG9naW4iLCJyb2xlIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzE0NTM0NDY2LCJleHAiOjE3MTQ1Nzc2NjZ9.821ewha25klODAq4AkCfrAEkf3N1DyzYdppzG4yKeVA";
            String userName = httpRequest.sendGetRequest(applicationProperties.getAuthServerUrl(), testToken);
            if (userName == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원이 아닙니다.");
            }

            // 성공 시 실행
            ReviewEntity reviewEntity = ReviewDto.convertToEntity(dto, locationEntity);
            reviewRepository.save(reviewEntity);
            return ResponseEntity.ok().body("등록이 완료 되었습니다.");
        } catch (Exception e) {
            log.info("Exception : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("글을 등록할 수 없습니다.");
        }
    }

    public ResponseEntity findReview(int locationId){
        try {
            List<ReviewEntity> allBy = reviewRepository.findAllByLocation_LocationIdOrderByCreateTimeDesc(locationId);

            // 신고 횟수가 10 이상인 리뷰 삭제
            List<ReviewEntity> notDeletedReviews = allBy.stream()
                    .filter(reviewEntity -> reviewEntity.getReportCount() < 10)
                    .collect(Collectors.toList());

            // 삭제된 리뷰를 제외한 리스트를 반환
            return ResponseEntity.ok().body(notDeletedReviews);
        } catch(Exception e) {
            log.error("Exception : " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity deleteReview(int reviewId){
        Optional<ReviewEntity> optionalReviewEntity = reviewRepository.findById(reviewId);

        try {
            // 리뷰가 없는 경우
            if (optionalReviewEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제된 글이거나 찾을 수 없습니다.");
            }
            // 리뷰는 존재하나 해당 리뷰의 작성자가 아니거나 회원이 아닌 경우
            String reviewAuthorId = optionalReviewEntity.get().getUserName();
            String userName = httpRequest.sendGetRequest(applicationProperties.getAuthServerUrl(), "사용자 JWT 토큰");
            if (userName == null || !userName.equals(reviewAuthorId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("해당 리뷰의 작성자가 아닙니다.");
            }

            // 성공 시 실행
            ReviewEntity reviewEntity = optionalReviewEntity.get();
            reviewRepository.delete(reviewEntity);
            return ResponseEntity.status(HttpStatus.OK).body("삭제가 완료되었습니다.");
        } catch (Exception e) {
            log.info("Exception : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public void likeCountPlus(int reviewId) {
        reviewRepository.updateLikeCountHit(reviewId);
    }

    public void badCountPlus(int reviewId) {
        reviewRepository.updateBadCountHit(reviewId);
    }

    public void reportCountPlus(int reviewId) {reviewRepository.updateReportCountHit(reviewId);}

    public ResponseEntity checkLogReviewLike(int reviewId){
        Optional<ReviewEntity> optionalReviewEntity = reviewRepository.findById(reviewId);
        try {
            // 리뷰가 없는 경우
            if (optionalReviewEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제된 글이거나 찾을 수 없습니다.");
            }
            // 리뷰는 존재하나 회원이 아닌 경우
            String userName = httpRequest.sendGetRequest(applicationProperties.getAuthServerUrl(), "사용자 JWT 토큰");
            if (userName == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원이 아닙니다.");
            }
            // 이미 좋아요를 누른 경우
            Optional<LogReviewCountEntity> optionalLogReviewCountEntity = logReviewCountRepository
                    .findLogReviewCountEntityByReviewEntity_ReviewIdAndCountCheckAndUserName(reviewId, 1, userName);
            if (!optionalLogReviewCountEntity.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 글에는 이미 좋아요를 눌렀습니다.");
            }

            // 성공 시 실행
            ReviewEntity reviewEntity = optionalReviewEntity.get();
            logReviewCountRepository.save(LogReviewCountEntity.setCount(reviewEntity.getUserName(), reviewEntity));
            likeCountPlus(reviewId);
            return ResponseEntity.status(HttpStatus.OK).body("게시글에 좋아요를 눌렀습니다.");
        } catch (Exception e) {
            log.info("Exception : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity checkLogReviewBad(int reviewId){
        Optional<ReviewEntity> optionalReviewEntity = reviewRepository.findById(reviewId);
        try {
            // 리뷰가 없는 경우
            if (optionalReviewEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제된 글이거나 찾을 수 없습니다.");
            }
            // 리뷰는 존재하나 회원이 아닌 경우
            String userName = httpRequest.sendGetRequest(applicationProperties.getAuthServerUrl(), "사용자 JWT 토큰");
            if (userName == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원이 아닙니다.");
            }
            // 이미 싫어요를 누른 경우
            Optional<LogReviewCountEntity> optionalLogReviewCountEntity = logReviewCountRepository
                    .findLogReviewCountEntityByReviewEntity_ReviewIdAndCountCheckAndUserName(reviewId, 1, userName);
            if (!optionalLogReviewCountEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 글에는 이미 싫어요를 눌렀습니다.");
            }

            // 성공 시 실행
            ReviewEntity reviewEntity = optionalReviewEntity.get();
            logReviewCountRepository.save(LogReviewCountEntity.setCount(reviewEntity.getUserName(), reviewEntity));
            badCountPlus(reviewId);
            return ResponseEntity.status(HttpStatus.OK).body("게시글에 싫어요를 눌렀습니다.");
        } catch (Exception e) {
            log.info("Exception : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity checkLogReviewReport(int reviewId){
        Optional<ReviewEntity> optionalReviewEntity = reviewRepository.findById(reviewId);
        try {
            // 리뷰가 없는 경우
            if (optionalReviewEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제된 글이거나 찾을 수 없습니다.");
            }
            // 리뷰는 존재하나 회원이 아닌 경우
            String userName = httpRequest.sendGetRequest(applicationProperties.getAuthServerUrl(), "사용자 JWT 토큰");
            if (userName == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원이 아닙니다.");
            }

            // 성공 시 실행
            reportCountPlus(reviewId);
            return ResponseEntity.status(HttpStatus.OK).body("게시글을 신고하였습니다.");
        } catch (Exception e) {
            log.info("Exception : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
