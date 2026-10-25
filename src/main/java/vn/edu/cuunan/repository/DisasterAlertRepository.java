package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.DisasterAlert;

import java.util.List;

@Repository
public interface DisasterAlertRepository extends JpaRepository<DisasterAlert, Long> {
    List<DisasterAlert> findByIsBroadcastTrueOrderByCreatedAtDesc();
    List<DisasterAlert> findAllByOrderByCreatedAtDesc();
}
