package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.OtpVerification;

import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByEmailAndTypeAndIsUsedFalseOrderByCreatedAtDesc(String email, String type);

    Optional<OtpVerification> findTopByEmailAndOtpCodeAndTypeAndIsUsedFalseOrderByCreatedAtDesc(String email, String otpCode, String type);
}
