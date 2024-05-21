package com.example.CommunityApplication.Entity.Location;

import com.example.CommunityApplication.Entity.Board.BoardEntity;
import com.example.CommunityApplication.Entity.Review.ReviewEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Collate;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int locationId;
    @Column
    private String category;
    @Column
    private Double latitude;
    @Column
    private Double longitude;
    @Column
    private Integer score;
    @Column
    private Integer visitCount;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "location", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BoardEntity> entities = new ArrayList<>();
    @OneToMany(mappedBy = "location", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReviewEntity> reviewEntities = new ArrayList<>();
    @Column
    private String title;
    @Column
    private String description;
}
