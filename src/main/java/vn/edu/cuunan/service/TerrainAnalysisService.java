package vn.edu.cuunan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.edu.cuunan.dto.RouteTerrainAnalysisDto;
import vn.edu.cuunan.dto.RouteTerrainAnalysisDto.EquipmentItemDto;
import vn.edu.cuunan.dto.RouteTerrainAnalysisDto.VehicleOptionDto;
import vn.edu.cuunan.entity.RescueTeam;
import vn.edu.cuunan.entity.SosRequest;
import vn.edu.cuunan.util.GeoUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class TerrainAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(TerrainAnalysisService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(2500))
            .build();

    /**
     * Phân tích tuyến đường, địa hình và đề xuất danh mục trang thiết bị & phương tiện tương thích.
     */
    public RouteTerrainAnalysisDto analyzeRouteAndTerrain(RescueTeam team, SosRequest sos, Long assignmentId) {
        RouteTerrainAnalysisDto dto = new RouteTerrainAnalysisDto();
        dto.setAssignmentId(assignmentId);
        dto.setSosId(sos.getId());
        dto.setTeamName(team.getTeamName());

        double teamLat = team.getCurrentLatitude() != null ? team.getCurrentLatitude() : 16.0544;
        double teamLng = team.getCurrentLongitude() != null ? team.getCurrentLongitude() : 108.2022;
        double sosLat = sos.getLatitude() != null ? sos.getLatitude() : 16.0544;
        double sosLng = sos.getLongitude() != null ? sos.getLongitude() : 108.2022;

        dto.setTeamLocation(new double[]{teamLat, teamLng});
        dto.setSosLocation(new double[]{sosLat, sosLng});

        // 1. Tính toán lộ trình thực tế qua OSRM (với Fallback Haversine an toàn)
        boolean hasRoadRoute = fetchOsrmRouting(dto, teamLat, teamLng, sosLat, sosLng);

        // 2. Phân tích loại địa hình dựa trên bối cảnh ca SOS và khả năng tiếp cận đường bộ
        determineTerrainType(dto, sos, hasRoadRoute);

        // 3. Đề xuất phương tiện di chuyển phù hợp
        generateRecommendedVehicles(dto);

        // 4. Đề xuất danh mục trang bị tác chiến
        generateRecommendedEquipment(dto, sos);

        return dto;
    }

    private boolean fetchOsrmRouting(RouteTerrainAnalysisDto dto, double lat1, double lon1, double lat2, double lon2) {
        double straightDistanceKm = GeoUtils.calculateDistance(lat1, lon1, lat2, lon2);

        try {
            // OSRM API: lon,lat;lon,lat
            String url = String.format(Locale.US,
                    "https://router.project-osrm.org/route/v1/driving/%.5f,%.5f;%.5f,%.5f?overview=full&geometries=geojson",
                    lon1, lat1, lon2, lat2);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(3000))
                    .header("User-Agent", "CuuNan247-DisasterRescue/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                String code = root.path("code").asText("");

                if ("Ok".equalsIgnoreCase(code) && root.has("routes") && root.get("routes").size() > 0) {
                    JsonNode bestRoute = root.get("routes").get(0);
                    double distMeters = bestRoute.path("distance").asDouble(0);
                    double durationSeconds = bestRoute.path("duration").asDouble(0);
                    JsonNode geometry = bestRoute.path("geometry");

                    double routeKm = Math.round((distMeters / 1000.0) * 100.0) / 100.0;
                    int durationMinutes = Math.max(1, (int) Math.round(durationSeconds / 60.0));

                    dto.setRouteDistanceKm(routeKm);
                    dto.setRouteDurationMinutes(durationMinutes);
                    dto.setRouteGeometryGeoJson(geometry.toString());
                    return true;
                }
            }
        } catch (Exception ex) {
            log.warn("Không thể kết nối OSRM API (sẽ fallback Haversine): {}", ex.getMessage());
        }

        // Fallback: Haversine straight line
        dto.setRouteDistanceKm(straightDistanceKm);
        int estMinutes = Math.max(2, (int) Math.round(straightDistanceKm * 2.5)); // Tốc độ trung bình 24 km/h
        dto.setRouteDurationMinutes(estMinutes);
        dto.setRouteGeometryGeoJson(String.format(Locale.US,
                "{\"type\":\"LineString\",\"coordinates\":[[%.5f,%.5f],[%.5f,%.5f]]}",
                lon1, lat1, lon2, lat2));
        return false;
    }

    private void determineTerrainType(RouteTerrainAnalysisDto dto, SosRequest sos, boolean hasRoadRoute) {
        String text = ((sos.getDescription() != null ? sos.getDescription() : "") + " "
                + (sos.getAddressDetail() != null ? sos.getAddressDetail() : "")).toLowerCase(Locale.ROOT);

        boolean isFloodKeyword = text.contains("ngập") || text.contains("lũ") || text.contains("nước sâu")
                || text.contains("mái nhà") || text.contains("chìm") || text.contains("sông")
                || text.contains("cù lao") || text.contains("cửa biển") || text.contains("cô lập")
                || text.contains("triều cường") || text.contains("thuyền") || text.contains("xuồng");

        boolean isMountainKeyword = text.contains("sạt lở") || text.contains("đất đá") || text.contains("đèo")
                || text.contains("núi") || text.contains("đứt đường") || text.contains("vùi lấp")
                || text.contains("dốc") || text.contains("đồi");

        boolean isUrbanKeyword = text.contains("phố") || text.contains("hẻm") || text.contains("ngõ")
                || text.contains("đường ngập") || text.contains("chết máy");

        if (isMountainKeyword) {
            dto.setTerrainType("LANDSLIDE_MOUNTAIN");
            dto.setTerrainTitle("ĐỊA HÌNH SẠT LỞ & ĐỒI NÚI HIỂM TRỞ");
            dto.setTerrainDescription("Tuyến đường có nguy cơ đứt gãy sạt lở bùn đất, cây gãy đổ, đèo dốc hiểm trở cần xe gầm cao 2 cầu và thiết bị leo núi.");
        } else if (!hasRoadRoute || isFloodKeyword) {
            dto.setTerrainType("FLOOD_DEEP");
            dto.setTerrainTitle("VÙNG TRŨNG NGẬP LỤT SÂU / CHIA CẮT ĐƯỜNG THỦY");
            dto.setTerrainDescription("Mực nước dâng cao chia cắt đường bộ hoặc ven sông hồ, bắt buộc sử dụng xuồng máy chuyên dụng cùng đầy đủ áo phao cứu sinh.");
        } else if (isUrbanKeyword) {
            dto.setTerrainType("URBAN_INUNDATION");
            dto.setTerrainTitle("NGẬP ÚNG ĐÔ THỊ / HẺM SÂU CHẬT HẸP");
            dto.setTerrainDescription("Đường phố và các ngõ ngách đô thị bị ngập cục bộ, xe to khó tiếp cận, ưu tiên xuồng cao su cơ động hoặc xe tải lội nước nhẹ.");
        } else {
            dto.setTerrainType("NORMAL_ROAD");
            dto.setTerrainTitle("ĐỊA HÌNH ĐƯỜNG BỘ THÔNG THƯỜNG");
            dto.setTerrainDescription("Tuyến đường bộ tương đối thông suốt, có thể di chuyển bằng ô tô cứu nạn chuyên dụng kết hợp túi y tế khẩn cấp.");
        }
    }

    private void generateRecommendedVehicles(RouteTerrainAnalysisDto dto) {
        List<VehicleOptionDto> vehicles = new ArrayList<>();
        String type = dto.getTerrainType();

        if ("FLOOD_DEEP".equals(type)) {
            vehicles.add(new VehicleOptionDto("BOAT_MOTOR", "Xuồng máy cứu hộ (Ca-nô cao tốc)", "fa-solid fa-ship", "Chuyên dụng vượt nước xiết, ngập sâu > 1.5m và vượt dòng sông lũ", true));
            vehicles.add(new VehicleOptionDto("BOAT_RUBBER", "Xuồng cao su chèo tay / thuyền nhôm", "fa-solid fa-sailboat", "Hỗ trợ len lỏi vào nhà dân có mái che thấp", false));
            vehicles.add(new VehicleOptionDto("TRUCK_4X4", "Xe bán tải 2 cầu (4x4) gầm cao", "fa-solid fa-truck-pickup", "Chở xuồng máy đến mép nước trước khi hạ thủy", false));
        } else if ("LANDSLIDE_MOUNTAIN".equals(type)) {
            vehicles.add(new VehicleOptionDto("TRUCK_4X4", "Xe bán tải 2 cầu (4x4 gầm cao lội nước)", "fa-solid fa-truck-pickup", "Vượt đường đèo dốc, bùn lầy sạt lở và chở trang bị nặng", true));
            vehicles.add(new VehicleOptionDto("AMBULANCE_SPECIAL", "Xe cứu thương dã chiến chuyên dụng", "fa-solid fa-truck-medical", "Hỗ trợ cáng cứu thương và trang thiết bị hồi sức tích cực", false));
            vehicles.add(new VehicleOptionDto("BOAT_MOTOR", "Xuồng máy cứu hộ", "fa-solid fa-ship", "Dự phòng khi gặp đoạn đường bị nước cuốn trôi cầu", false));
        } else if ("URBAN_INUNDATION".equals(type)) {
            vehicles.add(new VehicleOptionDto("BOAT_RUBBER", "Xuồng cao su cơ động / Thuyền nhôm đẩy tay", "fa-solid fa-sailboat", "Tối ưu len lỏi vào các ngõ hẻm đô thị bị ngập sâu", true));
            vehicles.add(new VehicleOptionDto("TRUCK_4X4", "Xe tải cứu nạn lội nước gầm cao", "fa-solid fa-truck-pickup", "Di chuyển trên các đại lộ chính ngập sâu 0.5 - 0.8m", false));
            vehicles.add(new VehicleOptionDto("BOAT_MOTOR", "Xuồng máy cứu hộ", "fa-solid fa-ship", "Dùng cho đoạn kênh rạch thoát nước lớn", false));
        } else {
            vehicles.add(new VehicleOptionDto("TRUCK_4X4", "Xe bán tải / Xe tải cứu nạn chuyên dụng", "fa-solid fa-truck-pickup", "Phương tiện cơ động tiêu chuẩn di chuyển nhanh đường bộ", true));
            vehicles.add(new VehicleOptionDto("AMBULANCE_SPECIAL", "Xe cứu thương dã chiến chuyên dụng", "fa-solid fa-truck-medical", "Ưu tiên vận chuyển người bị thương / người già", false));
            vehicles.add(new VehicleOptionDto("BOAT_MOTOR", "Xuồng máy cứu hộ", "fa-solid fa-ship", "Dự phòng tình huống phát sinh ngập cục bộ", false));
        }

        dto.setRecommendedVehicles(vehicles);
    }

    private void generateRecommendedEquipment(RouteTerrainAnalysisDto dto, SosRequest sos) {
        List<EquipmentItemDto> list = new ArrayList<>();
        String type = dto.getTerrainType();
        int people = sos.getPeopleCount() != null && sos.getPeopleCount() > 0 ? sos.getPeopleCount() : 1;
        boolean hasInjured = Boolean.TRUE.equals(sos.getHasInjured());
        boolean hasElderly = Boolean.TRUE.equals(sos.getHasElderly());
        boolean hasChildren = Boolean.TRUE.equals(sos.getHasChildren());

        boolean isFlood = "FLOOD_DEEP".equals(type) || "URBAN_INUNDATION".equals(type);
        boolean isMountain = "LANDSLIDE_MOUNTAIN".equals(type);

        // 1. NHÓM AN TOÀN SÔNG NƯỚC (SAFETY)
        list.add(new EquipmentItemDto("LIFE_JACKET", "Áo phao cứu sinh tiêu chuẩn (" + (people + 2) + " chiếc)",
                "SAFETY", "An Toàn Sông Nước", "fa-solid fa-life-ring", isFlood, isFlood || isMountain));
        list.add(new EquipmentItemDto("LIFE_BUOY", "Phao tròn cứu sinh có dây ném",
                "SAFETY", "An Toàn Sông Nước", "fa-solid fa-circle-notch", false, isFlood));
        list.add(new EquipmentItemDto("THROW_ROPE", "Dây ném cứu sinh chống chìm (30m)",
                "SAFETY", "An Toàn Sông Nước", "fa-solid fa-anchor", isFlood, isFlood));
        list.add(new EquipmentItemDto("WATERPROOF_LIGHT", "Đèn pin rọi cứu nạn chống nước siêu sáng",
                "SAFETY", "An Toàn Sông Nước", "fa-solid fa-flashlight", true, true));

        // 2. NHÓM Y TẾ & SƠ CỨU (MEDICAL)
        list.add(new EquipmentItemDto("FIRST_AID_KIT", "Túi sơ cứu y tế khẩn cấp (băng ép, garo, sát trùng)",
                "MEDICAL", "Y Tế & Sơ Cứu", "fa-solid fa-kit-medical", true, true));
        list.add(new EquipmentItemDto("STRETCHER", "Cáng cứu thương gấp gọn đa năng",
                "MEDICAL", "Y Tế & Sơ Cứu", "fa-solid fa-bed-pulse", hasInjured, hasInjured || hasElderly));
        list.add(new EquipmentItemDto("OXYGEN_TANK", "Bình oxy y tế mini cầm tay",
                "MEDICAL", "Y Tế & Sơ Cứu", "fa-solid fa-lungs", false, hasElderly || hasInjured));
        list.add(new EquipmentItemDto("THERMAL_BLANKET", "Chăn giữ nhiệt khẩn cấp chống sốc lạnh",
                "MEDICAL", "Y Tế & Sơ Cứu", "fa-solid fa-temperature-arrow-up", false, hasChildren || hasElderly || isFlood));

        // 3. NHÓM CÔNG BINH & CỨU NẠN CƠ ĐỘNG (GEAR)
        list.add(new EquipmentItemDto("CLIMBING_ROPE", "Dây thừng leo núi chịu lực cứu nạn (50m)",
                "GEAR", "Công Binh & Cơ Động", "fa-solid fa-braid", isMountain, isMountain));
        list.add(new EquipmentItemDto("CHAINSAW", "Máy cưa xích / Cưa tay cắt tỉa cây cối đỗ ngã",
                "GEAR", "Công Binh & Cơ Động", "fa-solid fa-screwdriver-wrench", false, isMountain));
        list.add(new EquipmentItemDto("MILITARY_SHOVEL", "Xẻng công binh & Rìu phá dỡ cứu hộ",
                "GEAR", "Công Binh & Cơ Động", "fa-solid fa-hammer", false, isMountain));
        list.add(new EquipmentItemDto("MEGAPHONE", "Loa pin phát thanh gọi nạn nhân từ xa",
                "GEAR", "Công Binh & Cơ Động", "fa-solid fa-bullhorn", false, true));

        // 4. NHÓM NHU YẾU PHẨM & DỰ PHÒNG (SUPPLY)
        list.add(new EquipmentItemDto("WATER_FOOD", "Nước uống đóng chai & Lương khô quân đội",
                "SUPPLY", "Nhu Yếu Phẩm Khẩn Cấp", "fa-solid fa-bottle-water", false, true));
        list.add(new EquipmentItemDto("DRY_BAG", "Túi bọc chống nước bảo vệ thuốc men / giấy tờ nạn nhân",
                "SUPPLY", "Nhu Yếu Phẩm Khẩn Cấp", "fa-solid fa-bag-shopping", false, isFlood));
        if (hasChildren) {
            list.add(new EquipmentItemDto("CHILD_VEST", "Áo phao chuyên dụng cỡ nhỏ cho trẻ em",
                    "SUPPLY", "Nhu Yếu Phẩm Khẩn Cấp", "fa-solid fa-child", true, true));
        }

        dto.setRecommendedEquipment(list);
    }
}
