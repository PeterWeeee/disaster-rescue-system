package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.CommunityPost;
import vn.edu.cuunan.entity.HazardType;
import vn.edu.cuunan.entity.VerificationStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    List<CommunityPost> findAllByOrderByCreatedAtDesc();
    List<CommunityPost> findByHazardTypeOrderByCreatedAtDesc(HazardType hazardType);
    Optional<CommunityPost> findFirstByLocationName(String locationName);
    List<CommunityPost> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
    long countByAuthorId(Long authorId);
    List<CommunityPost> findByVerificationStatusOrderByCreatedAtDesc(VerificationStatus verificationStatus);
}
