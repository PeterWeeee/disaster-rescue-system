package vn.edu.cuunan;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import vn.edu.cuunan.repository.RescueEquipmentRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("local")
class DisasterRescueApplicationTests {

    @Autowired
    private RescueEquipmentRepository rescueEquipmentRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void initializesReferencePricesForEquipmentInEveryTeam() {
        var equipment = rescueEquipmentRepository.findAll();
        assertFalse(equipment.isEmpty());
        assertTrue(equipment.stream().allMatch(item -> item.getUnitPrice() != null
                && item.getUnitPrice().compareTo(java.math.BigDecimal.ZERO) > 0));
    }
}
