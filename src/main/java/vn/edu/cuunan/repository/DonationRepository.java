package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.Donation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    Optional<Donation> findByTransactionCode(String transactionCode);

    List<Donation> findTop15ByStatusOrderByPaidAtDesc(String status);

    List<Donation> findAllByStatusOrderByPaidAtDesc(String status);

    long countByStatus(String status);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.status = :status")
    BigDecimal sumAmountByStatus(String status);
}
