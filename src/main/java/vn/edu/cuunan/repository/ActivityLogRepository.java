package vn.edu.cuunan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.ActivityLog;
import vn.edu.cuunan.entity.User;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findTop100ByOrderByCreatedAtDesc();

    Page<ActivityLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<ActivityLog> findByUserOrderByCreatedAtDesc(User user);

    List<ActivityLog> findByActionOrderByCreatedAtDesc(String action);
}
