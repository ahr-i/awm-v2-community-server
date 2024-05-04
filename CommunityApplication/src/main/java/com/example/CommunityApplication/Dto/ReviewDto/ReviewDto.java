package com.example.CommunityApplication.Dto.ReviewDto;

import com.example.CommunityApplication.Entity.Location.Location;
import com.example.CommunityApplication.Entity.Review.ReviewEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@ToString
@Slf4j
public class ReviewDto {
    // 작성자 닉네임
    private String boardWriter;
    // 작성자 ID
    private String userName;
    // 내용
    private String boardContent;
    // 해당 게시글의 장소Id
    private int locationId;
    // 신고 횟수
    private int reportCount;
    // 좋아요 횟수
    private int likeCount;
    // 싫어요 횟수
    private int badCount;
    // 생성시간
    private LocalDateTime createTime;

    private static ModelMapper mapper = new ModelMapper();

    public static ReviewDto convertToDto(ReviewEntity entity){
        ReviewDto dto = mapper.map(entity, ReviewDto.class);
        return dto;
    }
    public static ReviewEntity convertToEntity(ReviewDto dto, Optional<Location> optionalLocation){
        ReviewEntity entity = mapper.map(dto, ReviewEntity.class);
        entity.setLocation(optionalLocation.get());
        entity.setLikeCount(0);
        entity.setReportCount(0);
        return entity;
    }
}
