package com.example.community_old.Service;

import com.example.community_old.Dto.CommunityDto.BoardDto.BoardDto;
import com.example.community_old.Dto.CommunityDto.BoardDto.UserLogDto;
import com.example.community_old.Dto.CommunityDto.Response;
import com.example.community_old.JpaClass.CommunityTable.BoardEntity;
import com.example.community_old.JpaClass.CommunityTable.CommentEntity;
import com.example.community_old.JpaClass.CommunityTable.LogBoardCountEntity;
import com.example.community_old.JpaClass.CommunityTable.UserPostEntity;
import com.example.community_old.JpaClass.LocationTable.Location;
import com.example.community_old.Repository.CommunityRepository.BoardRepository;
import com.example.community_old.Repository.CommunityRepository.CommentRepository;
import com.example.community_old.Repository.CommunityRepository.LogBoardCountEntityRepository;
import com.example.community_old.Repository.CommunityRepository.UserPostRepository;
import com.example.community_old.Repository.LocationRepository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final CommentRepository commentRepository;
    private final BoardRepository repository;
    private final LocationRepository locationRepository;
    private final UserPostRepository logBoardRepository;
    private final LogBoardCountEntityRepository logBoardCountEntityRepository;

    public Optional<BoardEntity> findBoard(int postId){
        Optional<BoardEntity> byId = repository.findById(postId);
        if(byId.isPresent()) return byId;
        else return null;
    }

    public ResponseEntity BoardSave(BoardDto dto, int locationId, MultipartFile file, String userId) {
        try {
            Optional<Location> locationEntity = locationRepository.findById(locationId);

            if (locationEntity.isPresent()) {
                BoardEntity boardEntity = BoardDto.SaveToBoardEntity(dto, locationEntity, userId, file);
                repository.save(boardEntity);
                return ResponseEntity.ok().body("Registration is complete.");
            }
        } catch (IOException e) {
            log.info("IO exception {}", e.getMessage());
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the file.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot register the post.");
    }

    @Transactional
    public void updateHit(int postId) {
        repository.updateHit(postId);
    }
    public ResponseEntity findByPostAndComment(int postId) {
        Optional<BoardEntity> boardEntity = repository.findById(postId);
        List<CommentEntity> comment = commentRepository.findAllByEntityOrderByCreatTimeDesc(boardEntity.get());
        if (boardEntity.isPresent()) {
            updateHit(postId);
            BoardDto dto = BoardDto.DetailToBoardDto(boardEntity.get());

            Long commentCount = commentRepository.countAllByEntity(boardEntity.get());
            com.example.community_old.Dto.CommunityDto.Response response = new Response(dto, comment, commentCount);

            return ResponseEntity.ok().body(response);

        } else return ResponseEntity.status(HttpStatus.NO_CONTENT).body("The post does not exist.");
    }

    public ResponseEntity removeBoardPost(int postId, String userId) {
        Optional<BoardEntity> byId = repository.findById(postId);

        if (byId.isPresent()) {
            if (userId.equals(byId.get().getUserId())) {
                return ResponseEntity.ok().body("Deletion is complete.");
            } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not match.");
        } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The post has been deleted or the path does not exist.");
    }

    public String findUser(int postId) {
        Optional<BoardEntity> byId = repository.findById(postId);

        if (byId.isPresent()) {
            return byId.get().getUserId();
        } else return null;
    }

    public Page<BoardDto> page(int page, int locationId) {
        List<BoardEntity> allBy = repository.findAllByLocation_LocationId(locationId);

        allBy.stream().filter(entity -> entity.getReportCount() >= 10).forEach(entity -> repository.delete(entity));
        PageRequest createdDate = PageRequest.of(page, 3, Sort.by("createTime").descending());
        Page<BoardEntity> findPost = repository.findAllByLocation_LocationId(locationId, createdDate);

        if (!findPost.isEmpty()) {
            Page<BoardDto> sendPostdto = BoardDto.PageToBoardPostDto(findPost, commentRepository);
            return sendPostdto;
        } else return null;


    }

    public void updatePost(BoardDto dto, MultipartFile file, int postId) {
        Optional<BoardEntity> byId = repository.findById(postId);
        try {
            BoardEntity updateBoard = BoardDto.updatePost(dto, byId.get(),file);
            repository.save(updateBoard);

        }catch (IOException e) {
            log.info("Exceeded capacity.");
        }
    }

    public ResponseEntity saveLogPost(UserLogDto dto, int locationId, String userId, String nickName) {
        Optional<Location> byId = locationRepository.findById(locationId);

        if (byId.isPresent()) {
            logBoardRepository.save(UserLogDto.TransferUserEntity(dto, byId, userId, nickName));
            return ResponseEntity.ok().body("Registration is complete.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot register the post.");
    }

    public ResponseEntity findByDetailBoard(int id) {
        Optional<UserPostEntity> byId = logBoardRepository.findById(id);

        if (byId.isPresent()) {
            UserLogDto userLogDto = UserLogDto.TransferUserLogDto(byId.get());
            return ResponseEntity.ok().body(userLogDto);
        } else return ResponseEntity.status(HttpStatus.NO_CONTENT).body("The post does not exist or has been deleted.");
    }

    public Page<UserLogDto> findLogPage(int page, int locationId) {
        PageRequest createAt = PageRequest.of(page, 5, Sort.by("createAt").descending());
        Page<UserPostEntity> logPage = logBoardRepository.findAllByLocation_LocationId(locationId, createAt);

        return logPage.isEmpty() ? null : logPage.map(UserLogDto::TransferPageUserLogDto);
    }

    public ResponseEntity deleteLogBoard(int postId, String userId) {
        Optional<UserPostEntity> byId = logBoardRepository.findById(postId);

        if (byId.isPresent()) {
            if (userId.equals(byId.get().getUserId())) {
                logBoardRepository.delete(byId.get());
                return ResponseEntity.ok().body("Deletion is complete.");
            } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not match.");
        } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The post does not exist or it is a bad request.");
    }

    public void likeCountPlus(int postId) {
        logBoardRepository.updateHit(postId);
    }
    public void badCountPlus(int postId) {
        logBoardRepository.updateBadCountHit(postId);
    }

    public ResponseEntity checkLogBoardLike(int postId, String userId) {
        Optional<LogBoardCountEntity> likePost = logBoardCountEntityRepository.
                findByLogBoardEntity_IdAndCountCheckAndUserId(postId, 1, userId);
        Optional<UserPostEntity> byId = logBoardRepository.findById(postId);

        if (!likePost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have already liked this post.");
        } else {
            if (byId.isPresent()) {
                logBoardCountEntityRepository.save(LogBoardCountEntity.likeCount(userId, byId.get()));
                likeCountPlus(postId);
                return ResponseEntity.status(HttpStatus.OK).body("Like registered.");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The post does not exist or it is a bad request.");
    }

    public ResponseEntity checkLogBoardBadCount(int postId, String userId) {
        Optional<UserPostEntity> byId = logBoardRepository.findById(postId);
        Optional<LogBoardCountEntity> logPost =
                logBoardCountEntityRepository.findByLogBoardEntity_IdAndCountCheckAndUserId(postId, 0, userId);

        if (!logPost.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have already disliked this post.");
        else {
            if (byId.isPresent()) {
                logBoardCountEntityRepository.save(LogBoardCountEntity.BadCount(byId.get(), userId));
                badCountPlus(postId);
                return ResponseEntity.status(HttpStatus.OK).body("Dislike registered.");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The post does not exist or it is a bad request.");
    }
}