package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.SosRequest;

import java.util.List;

@Repository
public interface SosRequestRepository extends JpaRepository<SosRequest, Long> {
    List<SosRequest> findByStatusOrderByCreatedAtDesc(String status);
    List<SosRequest> findBySenderIdOrderByCreatedAtDesc(Long senderId);
    List<SosRequest> findByContactPhoneOrderByCreatedAtDesc(String contactPhone);
    List<SosRequest> findAllByOrderByCreatedAtDesc();
    List<SosRequest> findByStatusInOrderByCreatedAtDesc(List<String> statuses);
    long countByStatus(String status);
    long countByUrgencyLevel(String urgencyLevel);

    @Query("SELECT COALESCE(SUM(s.peopleCount), 0) FROM SosRequest s WHERE s.status = 'RESOLVED'")
    Long sumRescuedPeople();
}
