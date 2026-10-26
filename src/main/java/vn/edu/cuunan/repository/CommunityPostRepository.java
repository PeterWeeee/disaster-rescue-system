package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.CommunityPost;
import vn.edu.cuunan.entity.HazardType;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    List<CommunityPost> findAllByOrderByCreatedAtDesc();
    List<CommunityPost> findByHazardTypeOrderByCreatedAtDesc(HazardType hazardType);
}
