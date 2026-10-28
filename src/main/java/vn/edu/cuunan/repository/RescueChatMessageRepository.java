package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.RescueChatMessage;

import java.util.List;

@Repository
public interface RescueChatMessageRepository extends JpaRepository<RescueChatMessage, Long> {

    List<RescueChatMessage> findBySosRequestIdOrderByCreatedAtAsc(Long sosRequestId);

    long countBySosRequestId(Long sosRequestId);

    List<RescueChatMessage> findByAssignmentIdOrderByCreatedAtAsc(Long assignmentId);
}
