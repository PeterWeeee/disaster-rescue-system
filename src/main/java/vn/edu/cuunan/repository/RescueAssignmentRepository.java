package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.RescueAssignment;

import java.util.List;
import java.util.Optional;

@Repository
public interface RescueAssignmentRepository extends JpaRepository<RescueAssignment, Long> {
    List<RescueAssignment> findByTeamIdOrderByAssignedAtDesc(Long teamId);

    default List<RescueAssignment> findByTeamId(Long teamId) {
        return findByTeamIdOrderByAssignedAtDesc(teamId);
    }

    List<RescueAssignment> findBySosRequestId(Long sosRequestId);
    List<RescueAssignment> findBySosRequestIdOrderByAssignedAtDesc(Long sosRequestId);

    @EntityGraph(attributePaths = {"team"})
    Optional<RescueAssignment> findTopBySosRequestIdOrderByAssignedAtDesc(Long sosRequestId);

    long countByStatus(String status);

    @Query("SELECT ra FROM RescueAssignment ra JOIN FETCH ra.sosRequest WHERE ra.team.id = :teamId ORDER BY ra.assignedAt DESC")
    List<RescueAssignment> findByTeamIdWithSosRequest(@Param("teamId") Long teamId);

    @Query("SELECT ra FROM RescueAssignment ra JOIN FETCH ra.sosRequest WHERE ra.team.leader.id = :leaderId ORDER BY ra.assignedAt DESC")
    List<RescueAssignment> findByLeaderIdWithSosRequest(@Param("leaderId") Long leaderId);

    @Query("SELECT ra FROM RescueAssignment ra JOIN FETCH ra.sosRequest JOIN FETCH ra.team LEFT JOIN FETCH ra.assignedBy ORDER BY ra.assignedAt DESC")
    List<RescueAssignment> findAllWithDetails();
}
