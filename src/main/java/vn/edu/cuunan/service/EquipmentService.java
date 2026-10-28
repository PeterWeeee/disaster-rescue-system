package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.dto.EquipmentDispatchDto;
import vn.edu.cuunan.dto.EquipmentReturnDto;
import vn.edu.cuunan.dto.StartMissionRequestDto;
import vn.edu.cuunan.dto.RouteTerrainAnalysisDto;
import vn.edu.cuunan.dto.TeamResourcesDto;
import vn.edu.cuunan.entity.*;
import vn.edu.cuunan.repository.*;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.*;

@Service
public class EquipmentService {

    private static final Logger log = LoggerFactory.getLogger(EquipmentService.class);

    private final RescueEquipmentRepository equipmentRepository;
    private final EquipmentAssignmentRepository eqAssignmentRepository;
    private final RescueAssignmentRepository rescueAssignmentRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;

    public EquipmentService(RescueEquipmentRepository equipmentRepository,
                            EquipmentAssignmentRepository eqAssignmentRepository,
                            RescueAssignmentRepository rescueAssignmentRepository,
                            TeamMemberRepository teamMemberRepository,
                            ActivityLogService activityLogService,
                            NotificationService notificationService) {
        this.equipmentRepository = equipmentRepository;
        this.eqAssignmentRepository = eqAssignmentRepository;
        this.rescueAssignmentRepository = rescueAssignmentRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.activityLogService = activityLogService;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public TeamResourcesDto getTeamResources(RescueTeam team) {
        if (team == null) return new TeamResourcesDto();

        TeamResourcesDto dto = new TeamResourcesDto();
        dto.setTeamId(team.getId());
        dto.setTeamName(team.getTeamName());
        dto.setContactPhone(team.getContactPhone());
        dto.setTeamStatus(team.getStatus());

        // 1. Quản lý quân số
        List<TeamMember> members = teamMemberRepository.findByTeamId(team.getId());
        List<TeamResourcesDto.MemberItemDto> memberDtos = new ArrayList<>();

        // Check missions in progress for this team
        List<RescueAssignment> assignments = rescueAssignmentRepository.findByTeamId(team.getId());
        boolean hasInProgressMission = assignments.stream().anyMatch(a -> "IN_PROGRESS".equalsIgnoreCase(a.getStatus()));
        String activeMissionDesc = hasInProgressMission ? "Đang tham gia cứu hộ ca SOS khẩn cấp" : null;

        // Leader
        if (team.getLeader() != null) {
            String leaderStatus = hasInProgressMission ? "ON_MISSION" : "READY";
            memberDtos.add(new TeamResourcesDto.MemberItemDto(
                    team.getLeader().getId(),
                    team.getLeader().getFullName(),
                    team.getLeader().getPhone(),
                    "Đội trưởng",
                    leaderStatus,
                    activeMissionDesc
            ));
        }

        // Other members
        for (TeamMember m : members) {
            if (m.getUser() != null && (team.getLeader() == null || !m.getUser().getId().equals(team.getLeader().getId()))) {
                String memberStatus = hasInProgressMission ? "ON_MISSION" : "READY";
                memberDtos.add(new TeamResourcesDto.MemberItemDto(
                        m.getUser().getId(),
                        m.getUser().getFullName(),
                        m.getUser().getPhone(),
                        m.getRoleInTeam() != null ? m.getRoleInTeam() : "Đội viên",
                        memberStatus,
                        activeMissionDesc
                ));
            }
        }

        dto.setMembers(memberDtos);
        dto.setTotalMembers(memberDtos.size());
        int onMissionCount = (int) memberDtos.stream().filter(m -> "ON_MISSION".equals(m.getReadinessStatus())).count();
        dto.setOnMissionMembers(onMissionCount);
        dto.setReadyMembers(memberDtos.size() - onMissionCount);
        dto.setOffDutyMembers(0);

        // 2. Kho trang thiết bị
        List<RescueEquipment> eqList = equipmentRepository.findByTeamOrderByCategoryAscEquipmentNameAsc(team);
        dto.setEquipmentList(eqList);

        int totalUnits = eqList.stream().mapToInt(RescueEquipment::getTotalQuantity).sum();
        int availUnits = eqList.stream().mapToInt(RescueEquipment::getAvailableQuantity).sum();
        int dispUnits = eqList.stream().mapToInt(RescueEquipment::getDispatchedQuantity).sum();
        int damUnits = eqList.stream().mapToInt(RescueEquipment::getDamagedQuantity).sum();

        dto.setTotalEquipmentUnits(totalUnits);
        dto.setAvailableEquipmentUnits(availUnits);
        dto.setDispatchedEquipmentUnits(dispUnits);
        dto.setDamagedEquipmentUnits(damUnits);

        return dto;
    }

    @Transactional(readOnly = true)
    public void attachInventoryToRecommendations(RouteTerrainAnalysisDto analysis, RescueTeam team) {
        if (analysis == null || team == null) return;
        List<RescueEquipment> stock = equipmentRepository.findByTeamOrderByCategoryAscEquipmentNameAsc(team);
        Map<String, RescueEquipment> byCode = new HashMap<>();
        stock.forEach(item -> byCode.put(item.getEquipmentCode(), item));
        Map<String, String> vehicleCodes = Map.of(
                "TRUCK_4X4", "VEHICLE_TRUCK_4X4",
                "AMBULANCE_SPECIAL", "VEHICLE_AMBULANCE",
                "BOAT_MOTOR", "VEHICLE_BOAT_MOTOR",
                "BOAT_RUBBER", "VEHICLE_BOAT_RUBBER");
        List<RouteTerrainAnalysisDto.VehicleOptionDto> vehicles = analysis.getRecommendedVehicles();
        if (vehicles != null) {
            vehicles.removeIf(vehicle -> {
                RescueEquipment item = byCode.get(vehicleCodes.get(vehicle.getId()));
                if (item == null) return true;
                vehicle.setEquipmentId(item.getId());
                vehicle.setAvailableQuantity(item.getAvailableQuantity());
                vehicle.setUnit(item.getUnit());
                return false;
            });
        }

        Map<String, String> itemCodes = Map.ofEntries(
                Map.entry("LIFE_JACKET", "AO_PHAO"), Map.entry("LIFE_BUOY", "PHAO_TRON"),
                Map.entry("THROW_ROPE", "DAY_CUU_HO"), Map.entry("WATERPROOF_LIGHT", "DEN_PIN"),
                Map.entry("FIRST_AID_KIT", "TUI_SO_CUU"), Map.entry("CLIMBING_ROPE", "DAY_CUU_HO"),
                Map.entry("WATER_FOOD", "LUONG_KHO"), Map.entry("CHILD_VEST", "AO_PHAO"));
        List<RouteTerrainAnalysisDto.EquipmentItemDto> items = analysis.getRecommendedEquipment();
        if (items != null) {
            Set<Long> included = new HashSet<>();
            items.removeIf(recommended -> {
                RescueEquipment item = byCode.get(itemCodes.get(recommended.getId()));
                if (item == null || !included.add(item.getId())) return true;
                int suggested = 1;
                java.util.regex.Matcher count = java.util.regex.Pattern.compile("\\((\\d+)\\s+chiếc\\)", java.util.regex.Pattern.CASE_INSENSITIVE).matcher(recommended.getName());
                if (count.find()) suggested = Integer.parseInt(count.group(1));
                recommended.setEquipmentId(item.getId());
                recommended.setName(item.getEquipmentName());
                recommended.setCategory(item.getCategory());
                recommended.setCategoryName(categoryLabel(item.getCategory()));
                recommended.setUnit(item.getUnit());
                recommended.setAvailableQuantity(item.getAvailableQuantity());
                recommended.setRecommendedQuantity(suggested);
                return false;
            });
        }
    }

    private String categoryLabel(String category) {
        return switch (category == null ? "" : category.toUpperCase()) {
            case "SAFETY" -> "An toàn cứu sinh";
            case "MEDICAL" -> "Y tế & sơ cứu";
            case "SUPPLY" -> "Nhu yếu phẩm";
            default -> "Công binh & cơ động";
        };
    }

    @Transactional
    public List<EquipmentAssignment> dispatchMissionSelection(Long assignmentId, StartMissionRequestDto selection, User rescuer) {
        RescueAssignment assignment = rescueAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhiệm vụ #" + assignmentId));
        Map<Long, Integer> requested = new LinkedHashMap<>();
        if (selection.getVehicleType() != null && !selection.getVehicleType().isBlank()
                && selection.getVehicleEquipmentId() == null) {
            throw new IllegalArgumentException("Phương tiện chưa được liên kết với kho của đội.");
        }
        if (selection.getVehicleEquipmentId() != null) {
            String expectedCode = switch (selection.getVehicleType() == null ? "" : selection.getVehicleType()) {
                case "TRUCK_4X4" -> "VEHICLE_TRUCK_4X4";
                case "AMBULANCE_SPECIAL" -> "VEHICLE_AMBULANCE";
                case "BOAT_MOTOR" -> "VEHICLE_BOAT_MOTOR";
                case "BOAT_RUBBER" -> "VEHICLE_BOAT_RUBBER";
                default -> throw new IllegalArgumentException("Phương tiện đã chọn không hợp lệ.");
            };
            RescueEquipment vehicle = equipmentRepository.findById(selection.getVehicleEquipmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phương tiện đã chọn trong kho."));
            if (!expectedCode.equals(vehicle.getEquipmentCode())) {
                throw new IllegalArgumentException("Phương tiện không khớp với loại phương tiện đã chọn.");
            }
            requested.merge(selection.getVehicleEquipmentId(), 1, Integer::sum);
        }
        if (selection.getEquipmentItems() != null) {
            for (StartMissionRequestDto.SelectedEquipmentItem item : selection.getEquipmentItems()) {
                if (item == null || item.getEquipmentId() == null || item.getQuantity() < 1) {
                    throw new IllegalArgumentException("Số lượng trang bị đã chọn không hợp lệ.");
                }
                requested.merge(item.getEquipmentId(), item.getQuantity(), Integer::sum);
            }
        }
        if (requested.isEmpty()) return List.of();

        List<EquipmentAssignment> current = eqAssignmentRepository.findByRescueAssignmentIdAndStatus(assignmentId, "DISPATCHED");
        if (!current.isEmpty()) {
            Map<Long, Integer> existing = new LinkedHashMap<>();
            current.forEach(ea -> existing.merge(ea.getEquipment().getId(), ea.getQuantityDispatched(), Integer::sum));
            if (existing.equals(requested)) return current;
            throw new IllegalStateException("Trang bị của nhiệm vụ này đã xuất kho và không thể thay đổi sau khi xuất kích.");
        }

        EquipmentDispatchDto dispatch = new EquipmentDispatchDto();
        List<EquipmentDispatchDto.DispatchItem> dispatchItems = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : requested.entrySet()) {
            RescueEquipment item = equipmentRepository.findById(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trang bị đã chọn trong kho."));
            if (item.getTeam() == null || !Objects.equals(item.getTeam().getId(), assignment.getTeam().getId())) {
                throw new IllegalArgumentException("Trang bị đã chọn không thuộc kho của đội bạn.");
            }
            if (entry.getValue() > item.getAvailableQuantity()) {
                throw new IllegalStateException("Kho chỉ còn " + item.getAvailableQuantity() + " " + item.getUnit() + " " + item.getEquipmentName() + ". Hãy điều chỉnh số lượng.");
            }
            dispatchItems.add(new EquipmentDispatchDto.DispatchItem(entry.getKey(), entry.getValue()));
        }
        dispatch.setItems(dispatchItems);
        dispatch.setNotes("Tự động xuất kho theo phương tiện và trang bị xác nhận khi xuất kích.");
        return dispatchEquipment(assignmentId, dispatch, rescuer);
    }

    @Transactional
    public void returnAllIntact(Long assignmentId, User rescuer) {
        List<EquipmentAssignment> dispatched = eqAssignmentRepository.findByRescueAssignmentIdAndStatus(assignmentId, "DISPATCHED");
        EquipmentReturnDto dto = new EquipmentReturnDto();
        List<EquipmentReturnDto.ReturnItem> items = new ArrayList<>();
        for (EquipmentAssignment ea : dispatched) {
            items.add(new EquipmentReturnDto.ReturnItem(ea.getId(), ea.getQuantityDispatched(), 0, 0, 0));
        }
        dto.setItems(items);
        dto.setReportNotes("Tự động hoàn kho toàn bộ phương tiện và trang bị nguyên vẹn khi nhiệm vụ hoàn thành.");
        returnEquipment(assignmentId, dto, rescuer);
    }

    @Transactional
    public RescueEquipment updateUnitPrice(Long equipmentId, BigDecimal unitPrice, User administrator) {
        if (administrator == null || administrator.getRole() == null
                || !"ROLE_ADMIN".equalsIgnoreCase(administrator.getRole().getName())) {
            throw new IllegalArgumentException("Chỉ quản trị viên được cập nhật đơn giá.");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Đơn giá phải lớn hơn 0 VNĐ.");
        }
        if (unitPrice.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Đơn giá VNĐ chỉ nhận số nguyên.");
        }
        RescueEquipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vật dụng trong kho."));
        equipment.setUnitPrice(unitPrice);
        return equipmentRepository.save(equipment);
    }

    @Transactional
    public List<EquipmentAssignment> dispatchEquipment(Long assignmentId, EquipmentDispatchDto dto, User rescuer) {
        RescueAssignment assignment = rescueAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhiệm vụ #" + assignmentId));

        RescueTeam team = assignment.getTeam();
        if (team == null) {
            throw new IllegalStateException("Nhiệm vụ không gắn với đội cứu hộ hợp lệ");
        }

        List<EquipmentAssignment> savedAssignments = new ArrayList<>();

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (EquipmentDispatchDto.DispatchItem item : dto.getItems()) {
                if (item.getQuantity() <= 0) continue;

                RescueEquipment eq = equipmentRepository.findById(item.getEquipmentId())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trang bị #" + item.getEquipmentId()));

                if (!eq.getTeam().getId().equals(team.getId())) {
                    throw new SecurityException("Trang bị không thuộc kho của đội này");
                }

                if (eq.getAvailableQuantity() < item.getQuantity()) {
                    throw new IllegalStateException("Số lượng trang bị '" + eq.getEquipmentName()
                            + "' không đủ xuất kho (Hiện có: " + eq.getAvailableQuantity() + ", Yêu cầu: " + item.getQuantity() + ")");
                }

                // Cập nhật tồn kho an toàn trong transaction
                eq.setAvailableQuantity(eq.getAvailableQuantity() - item.getQuantity());
                eq.setDispatchedQuantity(eq.getDispatchedQuantity() + item.getQuantity());
                equipmentRepository.save(eq);

                EquipmentAssignment ea = new EquipmentAssignment(assignment, eq, item.getQuantity());
                ea.setNotes(dto.getNotes());
                savedAssignments.add(eqAssignmentRepository.save(ea));
            }

            // Ghi nhật ký kiểm toán
            activityLogService.log(rescuer, "DISPATCH_EQUIPMENT", "Nhiệm vụ #" + assignmentId,
                    "Cấp phát " + savedAssignments.size() + " loại trang bị cho ca cứu nạn", null);
        }

        return savedAssignments;
    }

    @Transactional
    public void returnEquipment(Long assignmentId, EquipmentReturnDto dto, User rescuer) {
        RescueAssignment assignment = rescueAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhiệm vụ #" + assignmentId));

        List<EquipmentAssignment> existingDispatches = eqAssignmentRepository.findByRescueAssignmentIdAndStatus(assignmentId, "DISPATCHED");
        if (existingDispatches.isEmpty()) {
            log.info("Nhiệm vụ #{} không có trang bị đang cấp phát cần thu hồi hoặc đã được thu hồi trước đó.", assignmentId);
            return;
        }

        Map<Long, EquipmentAssignment> map = new HashMap<>();
        for (EquipmentAssignment ea : existingDispatches) {
            map.put(ea.getId(), ea);
        }

        LocalDateTime now = LocalDateTime.now();

        if (dto != null && dto.getItems() != null) {
            for (EquipmentReturnDto.ReturnItem rItem : dto.getItems()) {
                EquipmentAssignment ea = map.get(rItem.getAssignmentEquipmentId());
                if (ea == null || !"DISPATCHED".equals(ea.getStatus())) {
                    throw new IllegalArgumentException("Phiếu đối soát có thiết bị không hợp lệ hoặc bị lặp.");
                }
                map.remove(rItem.getAssignmentEquipmentId());

                RescueEquipment eq = ea.getEquipment();
                if (rItem.getIntactQuantity() < 0 || rItem.getDamagedQuantity() < 0
                        || rItem.getLostQuantity() < 0 || rItem.getConsumedQuantity() < 0) {
                    throw new IllegalArgumentException("Số lượng kiểm kê không được âm.");
                }
                int totalReturnedThisItem = rItem.getIntactQuantity() + rItem.getDamagedQuantity()
                        + rItem.getLostQuantity() + rItem.getConsumedQuantity();

                // Mỗi phiếu xuất phải được đối soát đủ trước khi đóng.
                if (totalReturnedThisItem != ea.getQuantityDispatched()) {
                    throw new IllegalArgumentException("Tổng số nguyên vẹn, hư hỏng, mất và tiêu hao phải bằng số đã cấp phát (" + ea.getQuantityDispatched() + ").");
                }

                // Cập nhật kho
                eq.setDispatchedQuantity(Math.max(0, eq.getDispatchedQuantity() - totalReturnedThisItem));
                eq.setAvailableQuantity(eq.getAvailableQuantity() + rItem.getIntactQuantity());
                eq.setDamagedQuantity(eq.getDamagedQuantity() + rItem.getDamagedQuantity());

                int permanentlyRemoved = rItem.getLostQuantity() + rItem.getConsumedQuantity();
                if (permanentlyRemoved > 0) {
                    eq.setTotalQuantity(Math.max(0, eq.getTotalQuantity() - permanentlyRemoved));
                }
                equipmentRepository.save(eq);

                // Cập nhật phiếu cấp phát
                ea.setQuantityReturned(rItem.getIntactQuantity());
                ea.setQuantityDamaged(rItem.getDamagedQuantity());
                ea.setQuantityLost(rItem.getLostQuantity());
                ea.setQuantityConsumed(rItem.getConsumedQuantity());
                ea.setStatus("RETURNED");
                ea.setReturnedAt(now);
                ea.setNotes(dto.getReportNotes());
                eqAssignmentRepository.save(ea);

                // Cảnh báo nếu có trang bị hư hỏng hoặc mất
                if (rItem.getDamagedQuantity() > 0 || rItem.getLostQuantity() > 0) {
                    notificationService.sendToTeam(eq.getTeam(),
                            "Cảnh báo trang bị sau nhiệm vụ #" + assignmentId,
                            "Trang bị '" + eq.getEquipmentName() + "' ghi nhận hư hỏng: "
                                    + rItem.getDamagedQuantity() + ", mất: " + rItem.getLostQuantity() + ". Cần bảo dưỡng hoặc bổ sung.",
                            "EQUIPMENT_ALERT",
                            "/rescuer/resources");
                }
            }
        }
        if (!map.isEmpty()) {
            throw new IllegalArgumentException("Cần kiểm kê đầy đủ toàn bộ phương tiện và trang bị đã cấp phát.");
        }

        // Ghi nhật ký kiểm toán
        activityLogService.log(rescuer, "RETURN_EQUIPMENT", "Nhiệm vụ #" + assignmentId,
                "Thu hồi và kiểm kê trang thiết bị sau khi hoàn thành nhiệm vụ", null);
    }

    @Transactional(readOnly = true)
    public List<EquipmentAssignment> getDispatchedEquipmentForMission(Long assignmentId) {
        return eqAssignmentRepository.findByRescueAssignmentId(assignmentId);
    }
}
