package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.SosRequest;

import java.util.List;

@Repository
public interface SosRequestRepository extends JpaRepository<SosRequest, Long> {
    List<SosRequest> findByStatusOrderByCreatedAtDesc(String status);
    List<SosRequest> findBySenderIdOrderByCreatedAtDesc(Long senderId);
    List<SosRequest> findAllByOrderByCreatedAtDesc();
}
