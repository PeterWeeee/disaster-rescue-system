package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.RescueTeam;

import java.util.List;
import java.util.Optional;

@Repository
public interface RescueTeamRepository extends JpaRepository<RescueTeam, Long> {
    List<RescueTeam> findByStatus(String status);
    List<RescueTeam> findByStatusNot(String status);
    Optional<RescueTeam> findByLeaderId(Long leaderId);
    Optional<RescueTeam> findByLeaderUsername(String username);
    long countByStatus(String status);
}
