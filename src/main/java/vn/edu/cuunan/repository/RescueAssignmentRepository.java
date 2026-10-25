package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.RescueAssignment;

import java.util.List;

@Repository
public interface RescueAssignmentRepository extends JpaRepository<RescueAssignment, Long> {
    List<RescueAssignment> findByTeamIdOrderByAssignedAtDesc(Long teamId);
    List<RescueAssignment> findBySosRequestId(Long sosRequestId);
}
