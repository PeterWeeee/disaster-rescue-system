package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.EquipmentAssignment;
import vn.edu.cuunan.entity.RescueAssignment;

import java.util.List;

@Repository
public interface EquipmentAssignmentRepository extends JpaRepository<EquipmentAssignment, Long> {

    List<EquipmentAssignment> findByRescueAssignment(RescueAssignment rescueAssignment);

    List<EquipmentAssignment> findByRescueAssignmentId(Long assignmentId);

    List<EquipmentAssignment> findByRescueAssignmentIdAndStatus(Long assignmentId, String status);

    boolean existsByRescueAssignmentIdAndStatus(Long assignmentId, String status);
}
