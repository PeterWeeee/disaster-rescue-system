package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.RescueEquipment;
import vn.edu.cuunan.entity.RescueTeam;

import java.util.List;
import java.util.Optional;

@Repository
public interface RescueEquipmentRepository extends JpaRepository<RescueEquipment, Long> {

    List<RescueEquipment> findByTeamOrderByCategoryAscEquipmentNameAsc(RescueTeam team);

    Optional<RescueEquipment> findByTeamAndEquipmentCode(RescueTeam team, String equipmentCode);

    List<RescueEquipment> findByTeamId(Long teamId);

    long countByTeam(RescueTeam team);
}
