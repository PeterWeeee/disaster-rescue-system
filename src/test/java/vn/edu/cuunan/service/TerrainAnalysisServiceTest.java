package vn.edu.cuunan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.edu.cuunan.dto.RouteTerrainAnalysisDto;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.SosRequest;

import static org.junit.jupiter.api.Assertions.*;

class TerrainAnalysisServiceTest {

    private TerrainAnalysisService terrainAnalysisService;
    private RescueTeam team;
    private SosRequest sosRequest;

    @BeforeEach
    void setUp() {
        terrainAnalysisService = new TerrainAnalysisService();

        team = new RescueTeam();
        team.setId(1L);
        team.setTeamName("Đội Cứu Hộ Hải Châu");
        team.setCurrentLatitude(16.0678);
        team.setCurrentLongitude(108.2208);
        team.setEquipmentNotes("Xuồng cao tốc, áo phao");

        sosRequest = SosRequest.builder()
                .id(100L)
                .contactName("Nguyễn Văn A")
                .contactPhone("0912345678")
                .latitude(16.0750)
                .longitude(108.2250)
                .peopleCount(4)
                .addressDetail("Đường Trưng Nữ Vương ngập sâu 1.5m nước chảy xiết")
                .description("Cần xuồng máy cứu người già và trẻ em")
                .hasElderly(true)
                .hasChildren(true)
                .hasInjured(true)
                .build();
    }

    @Test
    @DisplayName("Phân tích địa hình phát hiện vùng ngập sâu dựa trên từ khóa mô tả")
    void testAnalyzeRouteAndTerrain_DeepFlood() {
        RouteTerrainAnalysisDto result = terrainAnalysisService.analyzeRouteAndTerrain(team, sosRequest, 50L);

        assertNotNull(result);
        assertEquals("FLOOD_DEEP", result.getTerrainType());
        assertNotNull(result.getTerrainTitle());
        assertTrue(result.getTerrainTitle().contains("NGẬP LỤT SÂU") || result.getTerrainTitle().contains("CHIA CẮT"));
        assertTrue(result.getRouteDistanceKm() > 0, "Khoảng cách phải lớn hơn 0");
        assertTrue(result.getRouteDurationMinutes() > 0, "Thời gian dự kiến phải lớn hơn 0");

        // Kiểm tra danh sách phương tiện: BOAT_MOTOR phải được khuyên dùng cho ngập sâu
        assertNotNull(result.getRecommendedVehicles());
        assertFalse(result.getRecommendedVehicles().isEmpty());
        boolean hasBoatMotorRecommended = result.getRecommendedVehicles().stream()
                .anyMatch(v -> "BOAT_MOTOR".equals(v.getId()) && v.isRecommended());
        assertTrue(hasBoatMotorRecommended, "Phương tiện khuyên dùng cho ngập sâu phải là xuồng máy cứu hộ");

        // Kiểm tra danh mục trang bị: phải có áo phao trẻ em, người già, cáng cứu thương và túi sơ cứu do hasChildren, hasElderly, hasInjured
        assertNotNull(result.getRecommendedEquipment());
        boolean hasChildVest = result.getRecommendedEquipment().stream()
                .anyMatch(e -> "CHILD_VEST".equals(e.getId()));
        boolean hasStretcher = result.getRecommendedEquipment().stream()
                .anyMatch(e -> "STRETCHER".equals(e.getId()));
        boolean hasFirstAid = result.getRecommendedEquipment().stream()
                .anyMatch(e -> "FIRST_AID_KIT".equals(e.getId()));

        assertTrue(hasChildVest, "Phải gợi ý áo phao trẻ em khi có trẻ nhỏ");
        assertTrue(hasStretcher, "Phải gợi ý cáng cứu thương khi có người bị thương");
        assertTrue(hasFirstAid, "Phải gợi ý túi sơ cứu khi có người bị thương");
    }

    @Test
    @DisplayName("Phân tích địa hình phát hiện sạt lở đồi núi")
    void testAnalyzeRouteAndTerrain_LandslideMountain() {
        sosRequest.setAddressDetail("Đèo Hải Vân, đoạn dốc sạt lở taluy bùn đất đá");
        sosRequest.setDescription("Đường đất trơn trượt sạt mái taluy cây đổ chắn ngang đường");
        sosRequest.setHasElderly(false);
        sosRequest.setHasChildren(false);
        sosRequest.setHasInjured(false);

        RouteTerrainAnalysisDto result = terrainAnalysisService.analyzeRouteAndTerrain(team, sosRequest, 51L);

        assertNotNull(result);
        assertEquals("LANDSLIDE_MOUNTAIN", result.getTerrainType());

        // Kiểm tra phương tiện: TRUCK_4X4 được khuyên dùng
        boolean hasTruckRecommended = result.getRecommendedVehicles().stream()
                .anyMatch(v -> "TRUCK_4X4".equals(v.getId()) && v.isRecommended());
        assertTrue(hasTruckRecommended, "Địa hình đồi núi sạt lở phải khuyên dùng xe gầm cao 2 cầu 4x4");

        // Trang bị có dây thừng leo núi và cưa máy
        boolean hasRope = result.getRecommendedEquipment().stream()
                .anyMatch(e -> "CLIMBING_ROPE".equals(e.getId()));
        assertTrue(hasRope, "Địa hình sạt lở phải có dây cứu nạn chịu lực");
    }

    @Test
    @DisplayName("Tính toán đường đi vẫn an toàn và fallback khi tọa độ null")
    void testAnalyzeRouteAndTerrain_NullCoordinates() {
        team.setCurrentLatitude(null);
        team.setCurrentLongitude(null);

        RouteTerrainAnalysisDto result = terrainAnalysisService.analyzeRouteAndTerrain(team, sosRequest, 52L);

        assertNotNull(result);
        assertNotNull(result.getRouteDistanceKm());
        assertNotNull(result.getRouteDurationMinutes());
    }
}
