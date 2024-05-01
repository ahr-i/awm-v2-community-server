package com.example.CommunityApplication.Repository.Location;

import com.example.CommunityApplication.Entity.Location.Location;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface LocationRepository extends JpaRepository<Location, Integer> {

}
