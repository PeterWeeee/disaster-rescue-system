package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.dto.EquipmentDispatchDto;
import vn.edu.cuunan.dto.EquipmentReturnDto;
import vn.edu.cuunan.dto.TeamResourcesDto;
import vn.edu.cuunan.entity.*;
import vn.edu.cuunan.repository.*;

import java.time.LocalDateTime;
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
                    continue; // Anti duplicate return guard
                }

                RescueEquipment eq = ea.getEquipment();
                int totalReturnedThisItem = rItem.getIntactQuantity() + rItem.getDamagedQuantity()
                        + rItem.getLostQuantity() + rItem.getConsumedQuantity();

                // Đảm bảo không thu hồi vượt quá số đã cấp phát
                if (totalReturnedThisItem > ea.getQuantityDispatched()) {
                    throw new IllegalArgumentException("Số lượng thu hồi vượt quá số lượng đã cấp phát ban đầu (" + ea.getQuantityDispatched() + ")");
                }

                // Cập nhật kho
                eq.setDispatchedQuantity(Math.max(0, eq.getDispatchedQuantity() - totalReturnedThisItem));
                eq.setAvailableQuantity(eq.getAvailableQuantity() + rItem.getIntactQuantity());
                eq.setDamagedQuantity(eq.getDamagedQuantity() + rItem.getDamagedQuantity());

                if (rItem.getLostQuantity() > 0) {
                    eq.setTotalQuantity(Math.max(0, eq.getTotalQuantity() - rItem.getLostQuantity()));
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

        // Ghi nhật ký kiểm toán
        activityLogService.log(rescuer, "RETURN_EQUIPMENT", "Nhiệm vụ #" + assignmentId,
                "Thu hồi và kiểm kê trang thiết bị sau khi hoàn thành nhiệm vụ", null);
    }

    @Transactional(readOnly = true)
    public List<EquipmentAssignment> getDispatchedEquipmentForMission(Long assignmentId) {
        return eqAssignmentRepository.findByRescueAssignmentId(assignmentId);
    }
}
