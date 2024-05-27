package com.example.CommunityApplication.Dto.BoardDto;

import com.example.CommunityApplication.Entity.Board.BoardEntity;
import com.example.CommunityApplication.Entity.Location.Location;
import com.example.CommunityApplication.Repository.CommentRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@ToString
@Slf4j
public class BoardDto {
    // 게시글 ID
    private int postId;
    // 작성자 ID
    private String userName;
    // 작성자 닉네임
    private String boardWriter;
    // 게시글 제목
    private String boardTitle;
    // 게시글 내용
    private String boardContent;
    // 해당 게시글의 장소ID
    private int locationId;
    // 게시글 조회수
    private int boardHit;
    // 생성시간
    private LocalDateTime createTime;
    // 게시글 좋아요
    private int likeCount;
    // 게시글 싫어요
    private int badCount;
    // 신고횟수
    private int reportCount;
    // 이미지 파일
    private String imageUrl;
    // 게시글의 댓글 수
    private long commentCount;

    private static ModelMapper mapper = new ModelMapper();

    // 게시글 저장을 위한 BoardEntity객체 반환, BoardDto -> BoardEntity
    public static BoardEntity SaveToBoardEntity(BoardDto dto, Optional<Location> location, String imageUrl) throws IOException {
        BoardEntity entity = mapper.map(dto, BoardEntity.class);
        entity.setLocation(location.get());
        entity.setBoardHits(0);
        entity.setLikeCount(0);
        entity.setBadCount(0);
        entity.setReportCount(0);
        entity.setImageUrl(imageUrl);

        return entity;
    }

    // 게시글의 상세 정보 반환, BoardEntity -> BoardDto
    public static BoardDto DetailToBoardDto(BoardEntity entity) {
        BoardDto dto = mapper.map(entity, BoardDto.class);
        return dto;
    }

    // 게시글에 달린 댓글 페이지로 반환
    public static Page<BoardDto> PageToBoardPostDto(Page<BoardEntity> entities, CommentRepository repository) {
        return entities.map(entity -> {
            BoardDto dto = new BoardDto();
            dto.setBoardTitle(entity.getBoardTitle());
            dto.setBoardContent(entity.getBoardContent());
            dto.setBoardHit(entity.getBoardHits());
            dto.setPostId(entity.getPostId());
            dto.setBoardWriter(entity.getBoardWriter());
            dto.setCreateTime(entity.getCreateTime());
            dto.setCommentCount(repository.countAllByBoardEntity(entity));
            dto.setImageUrl(entity.getImageUrl());
            return dto;
        });
    }

    // 게시글 수정
    public static BoardEntity updatePost(BoardDto dto, BoardEntity entity, String imageUrl) throws IOException {
        entity.setBoardTitle(dto.getBoardTitle());
        entity.setBoardContent(dto.getBoardContent());
        entity.setImageUrl(imageUrl);
        return entity;
    }
}
