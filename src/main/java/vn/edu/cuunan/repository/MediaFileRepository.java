package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.MediaFile;
import vn.edu.cuunan.entity.SosRequest;

import java.util.List;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    List<MediaFile> findBySosRequestOrderByCreatedAtDesc(SosRequest sosRequest);

    List<MediaFile> findBySosRequestIdOrderByCreatedAtDesc(Long sosId);
}
