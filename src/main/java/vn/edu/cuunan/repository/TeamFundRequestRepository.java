package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.TeamFundRequest;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TeamFundRequestRepository extends JpaRepository<TeamFundRequest, Long> {

    List<TeamFundRequest> findByRescueTeamIdOrderByCreatedAtDesc(Long teamId);

    List<TeamFundRequest> findByStatusOrderByCreatedAtDesc(String status);

    List<TeamFundRequest> findAllByOrderByCreatedAtDesc();

    long countByStatus(String status);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM TeamFundRequest r WHERE r.status = 'APPROVED'")
    BigDecimal sumApprovedAmount();

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM TeamFundRequest r WHERE r.rescueTeam.id = :teamId AND r.status = 'APPROVED'")
    BigDecimal sumApprovedAmountByTeam(@Param("teamId") Long teamId);
}
