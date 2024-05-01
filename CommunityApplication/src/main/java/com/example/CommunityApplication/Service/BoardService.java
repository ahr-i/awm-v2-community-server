package com.example.CommunityApplication.Service;

import com.example.CommunityApplication.ApplicationProperties;
import com.example.CommunityApplication.Dto.BoardDto.BoardDto;
import com.example.CommunityApplication.Dto.Response;
import com.example.CommunityApplication.Entity.Board.BoardEntity;
import com.example.CommunityApplication.Entity.Board.LogBoardCountEntity;
import com.example.CommunityApplication.Entity.Comment.CommentEntity;
import com.example.CommunityApplication.Entity.Location.Location;
import com.example.CommunityApplication.HttpRequest.HttpRequest;
import com.example.CommunityApplication.Repository.Board.BoardRepository;
import com.example.CommunityApplication.Repository.Board.LogBoardCountEntityRepository;
import com.example.CommunityApplication.Repository.CommentRepository;
import com.example.CommunityApplication.Repository.Location.LocationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final LocationRepository locationRepository;
    private final LogBoardCountEntityRepository logBoardCountEntityRepository;
    private final HttpRequest httpRequest;
    private final ApplicationProperties applicationProperties;

    // 게시글 저장
    public ResponseEntity saveBoard(BoardDto dto, int locationId, MultipartFile file) {
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
            BoardEntity boardEntity = BoardDto.SaveToBoardEntity(dto, locationEntity, file);
            boardRepository.save(boardEntity);
            return ResponseEntity.ok().body("등록이 완료 되었습니다.");
        } catch (Exception e) {
            log.info("Exception : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("글을 등록할 수 없습니다.");
        }
    }

    // 게시글 리스트 불러오기
    public Page<BoardDto> page(int page, int locationId) {
        List<BoardEntity> allBy = boardRepository.findAllByLocation_LocationId(locationId);
        // 신고 횟수가 10 이상인 경우 해당 게시글 삭제
        allBy.stream().filter(boardEntity -> boardEntity.getReportCount() >= 10)
                .forEach(boardEntity -> boardRepository.delete(boardEntity));

        PageRequest createdDate = PageRequest.of(page, 5, Sort.by("createTime").descending());
        Page<BoardEntity> findBoard = boardRepository.findAllByLocation_LocationId(locationId, createdDate);

        if (!findBoard.isEmpty()) {
            Page<BoardDto> responseBoardDto = BoardDto.PageToBoardPostDto(findBoard, commentRepository);
            return responseBoardDto;
        } else {
            return null;
        }
    }

    // 게시글 상세 내용 불러오기
    public ResponseEntity findByBoardAndComment(int postId) {
        try {
            // 게시글 조회
            Optional<BoardEntity> boardEntity = boardRepository.findById(postId);

            if (boardEntity.isPresent()) {
                // 코멘트 조회, 여기서 자꾸 에러 발생함
                List<CommentEntity> comment = commentRepository.findAllByEntity_postIdOrderByCreateTimeDesc(boardEntity.get().getPostId());
                // 조회수 업데이트
                updateHit(postId); // 조회수 + 1
                // DTO 생성
                BoardDto dto = BoardDto.DetailToBoardDto(boardEntity.get());
                // 코멘트 수 조회
                Long commentCount = commentRepository.countAllByEntity(boardEntity.get());
                // 응답 생성
                Response response = new Response(dto, comment, commentCount);
                return ResponseEntity.ok().body(response);
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("해당 게시글이 존재하지 않습니다.");
            }
        } catch (Exception e) {
            log.error("Exception: " + e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @Transactional
    public void updateHit(int postId) {
        boardRepository.updateHit(postId);
    }

    // 게시글 삭제
    public ResponseEntity removeBoard(int postId) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(postId);

        try {
            // 게시글이 없는 경우
            if (optionalBoardEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제된 글이거나 찾을 수 없습니다.");
            }
            // 게시글은 존재하나 해당 게시글의 작성자가 아니거나 회원이 아닌 경우
            String boardAuthorId = optionalBoardEntity.get().getUserName();
            String userName = httpRequest.sendGetRequest(applicationProperties.getAuthServerUrl(), "사용자 JWT 토큰");
            if (userName == null || !userName.equals(boardAuthorId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("해당 게시글의 작성자가 아닙니다.");
            }

            // 성공 시 실행
            BoardEntity boardEntity = optionalBoardEntity.get();
            boardRepository.delete(boardEntity);
            return ResponseEntity.status(HttpStatus.OK).body("삭제가 완료되었습니다.");
        } catch (Exception e) {
            log.info("Exception : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 게시글 수정
    public ResponseEntity updateBoard(BoardDto dto, MultipartFile file, int postId) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(postId);

        try {
            // 게시글이 없는 경우
            if (optionalBoardEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제된 글이거나 찾을 수 없습니다.");
            }
            // 게시글은 존재하나 해당 게시글의 작성자가 아니거나 회원이 아닌 경우
            String boardAuthorId = optionalBoardEntity.get().getUserName();
            String userName = httpRequest.sendGetRequest(applicationProperties.getAuthServerUrl(), "사용자 JWT 토큰");
            if (userName == null || !userName.equals(boardAuthorId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("해당 게시글의 작성자가 아닙니다.");
            }

            // 성공 시 실행
            BoardEntity updateBoard = BoardDto.updatePost(dto, optionalBoardEntity.get(), file);
            boardRepository.save(updateBoard); // 이거 궁금
            return ResponseEntity.status(HttpStatus.OK).body("게시글이 정상적으로 수정되었습니다.");
        } catch (Exception e) {
            log.info("Exception : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public void likeCountPlus(int postId) {
        boardRepository.updateLikeCountHit(postId);
    }

    public void badCountPlus(int postId) {
        boardRepository.updateBadCountHit(postId);
    }

    // 게시글 좋아요
    public ResponseEntity checkLogBoardLike(int postId){
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(postId);
        try {
            // 게시글이 없는 경우
            if (optionalBoardEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제된 글이거나 찾을 수 없습니다.");
            }
            // 게시글은 존재하나 회원이 아닌 경우
            String userName = httpRequest.sendGetRequest(applicationProperties.getAuthServerUrl(), "사용자 JWT 토큰");
            if (userName == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원이 아닙니다.");
            }
            // 이미 좋아요를 누른 경우
            Optional<LogBoardCountEntity> optionalLogBoardCountEntity = logBoardCountEntityRepository
                    .findLogBoardCountEntityByBoardEntity_PostIdAndCountCheckAndUserName(postId, 1, userName);
            if (!optionalLogBoardCountEntity.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 글에는 이미 좋아요를 눌렀습니다.");
            }

            // 성공 시 실행
            BoardEntity boardEntity = optionalBoardEntity.get();
            logBoardCountEntityRepository.save(LogBoardCountEntity.setCount(boardEntity.getUserName(), boardEntity));
            likeCountPlus(postId);
            return ResponseEntity.status(HttpStatus.OK).body("게시글에 좋아요를 눌렀습니다.");
        } catch (Exception e) {
            log.info("Exception : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 게시글 싫어요
    public ResponseEntity checkLogBoardBad(int postId){
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(postId);
        try {
            // 게시글이 없는 경우
            if (optionalBoardEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제된 글이거나 찾을 수 없습니다.");
            }
            // 게시글은 존재하나 회원이 아닌 경우
            String userName = httpRequest.sendGetRequest(applicationProperties.getAuthServerUrl(), "사용자 JWT 토큰");
            if (userName == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원이 아닙니다.");
            }
            // 이미 싫어요를 누른 경우
            Optional<LogBoardCountEntity> optionalLogBoardCountEntity = logBoardCountEntityRepository
                    .findLogBoardCountEntityByBoardEntity_PostIdAndCountCheckAndUserName(postId, 1, userName);
            if (!optionalLogBoardCountEntity.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 글에는 이미 싫어요를 눌렀습니다.");
            }

            // 성공 시 실행
            BoardEntity boardEntity = optionalBoardEntity.get();
            logBoardCountEntityRepository.save(LogBoardCountEntity.setCount(boardEntity.getUserName(), boardEntity));
            badCountPlus(postId);
            return ResponseEntity.status(HttpStatus.OK).body("게시글에 싫어요를 눌렀습니다.");
        } catch (Exception e) {
            log.info("Exception : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 게시글 신고
    public ResponseEntity checkLogBoardReport(int postId){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}