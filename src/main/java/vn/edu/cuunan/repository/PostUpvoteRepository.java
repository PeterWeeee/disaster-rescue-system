package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.PostUpvote;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostUpvoteRepository extends JpaRepository<PostUpvote, Long> {
    Optional<PostUpvote> findByPostIdAndUserId(Long postId, Long userId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    void deleteByPostIdAndUserId(Long postId, Long userId);
    List<PostUpvote> findByPostId(Long postId);
    List<PostUpvote> findByUserId(Long userId);
    long countByPostId(Long postId);
    long countByPostIdAndReactionType(Long postId, String reactionType);
}
