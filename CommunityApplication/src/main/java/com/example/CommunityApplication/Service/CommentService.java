package com.example.CommunityApplication.Service;

import com.example.CommunityApplication.ApplicationProperties;
import com.example.CommunityApplication.Dto.BoardDto.CommentDto;
import com.example.CommunityApplication.Entity.Board.BoardEntity;
import com.example.CommunityApplication.Entity.Comment.CommentEntity;
import com.example.CommunityApplication.HttpRequest.HttpRequest;
import com.example.CommunityApplication.Repository.Board.BoardRepository;
import com.example.CommunityApplication.Repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final HttpRequest httpRequest;
    private final ApplicationProperties applicationProperties;

    public ResponseEntity save(CommentDto commentDto, int postId, String jwtToken) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(postId);
        try {
            // 코멘트를 등록할 장소가 존재하지 않는 경우
            if (false /*optionalBoardEntity.isEmpty()*/) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시글을 찾을 수 없습니다.");
            }
            // Authentication 서버에서 회원이 맞는지 확인 요청
            // 회원이 아닌 경우
            String userId = httpRequest.sendGetRequest(applicationProperties.getAuthServerUrl(), jwtToken);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원이 아닙니다.");
            }

            // 성공 시 실행
            CommentEntity commentEntity = CommentDto.convertToEntity(commentDto, optionalBoardEntity);
            commentRepository.save(commentEntity);
            return ResponseEntity.ok().body("등록이 완료 되었습니다.");
        } catch (Exception e) {
            log.info("Exception : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 등록에 실패 했습니다.");
        }
    }

    public ResponseEntity removeComment(int commentId, String jwtToken) {
        Optional<CommentEntity> optionalCommentEntity = commentRepository.findById(commentId);
        try {
            // 삭제할 코멘트가 존재하지 않는 경우
            if (optionalCommentEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 댓글을 찾을 수 없습니다.");
            }
            // 댓글은 존재하나 해당 댓글의 작성자가 아니거나 회원이 아닌 경우
            String commentAuthorId = optionalCommentEntity.get().getUserName();
            String userName = httpRequest.sendGetRequest(applicationProperties.getAuthServerUrl(), jwtToken);
            if (userName == null || !userName.equals(commentAuthorId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("해당 댓글의 작성자가 아닙니다.");
            }

            // 성공 시 실행
            CommentEntity commentEntity = optionalCommentEntity.get();
            commentRepository.delete(commentEntity);
            return ResponseEntity.ok().body("삭제가 완료되었습니다.");
        } catch (Exception e) {
            log.info("Exception : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제에 실패 했습니다.");
        }
    }
}
