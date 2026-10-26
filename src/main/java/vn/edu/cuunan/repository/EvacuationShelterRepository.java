package vn.edu.cuunan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.cuunan.entity.EvacuationShelter;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvacuationShelterRepository extends JpaRepository<EvacuationShelter, Long> {
    List<EvacuationShelter> findByStatusNotOrderByNameAsc(String status);
    Optional<EvacuationShelter> findByName(String name);
}
