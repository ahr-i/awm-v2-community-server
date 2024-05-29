package com.example.community_old.Service;

import com.example.community_old.Dto.CommunityDto.BoardDto.CommentDto;
import com.example.community_old.JpaClass.CommunityTable.BoardEntity;
import com.example.community_old.JpaClass.CommunityTable.CommentEntity;
import com.example.community_old.Repository.CommunityRepository.BoardRepository;
import com.example.community_old.Repository.CommunityRepository.CommentRepository;
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

    private final CommentRepository repository;
    private final BoardRepository boardRepository;

    public ResponseEntity save(CommentDto dto, int postId, String userId){

        Optional<BoardEntity> byId = boardRepository.findById(postId);

        if(byId.isPresent()) {
            CommentEntity entity = CommentDto.TransferCommentEntity(dto,byId.get(),userId);
            repository.save(entity);
            return ResponseEntity.ok().body("Comment registration is complete.");
        }else if(!byId.isPresent())  return ResponseEntity.status(HttpStatus.NO_CONTENT).body("The post does not exist.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to register the comment.");
    }
}
