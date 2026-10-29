package vn.edu.cuunan.dto;

import vn.edu.cuunan.entity.RescueChatMessage;
import vn.edu.cuunan.entity.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatMessageDto {

    private Long id;
    private Long sosId;
    private Long assignmentId;
    private String senderType;
    private String senderName;
    private String senderPhone;
    private String content;
    private String messageType;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private String formattedTime;
    private Boolean isMine;

    public ChatMessageDto() {
    }

    public static ChatMessageDto fromEntity(RescueChatMessage entity, User currentUser, String citizenPhone) {
        if (entity == null) return null;
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(entity.getId());
        dto.setSosId(entity.getSosRequest() != null ? entity.getSosRequest().getId() : null);
        dto.setAssignmentId(entity.getAssignment() != null ? entity.getAssignment().getId() : null);
        dto.setSenderType(entity.getSenderType());
        dto.setSenderName(entity.getSenderName());
        dto.setSenderPhone(entity.getSenderPhone());
        dto.setContent(entity.getContent());
        dto.setMessageType(entity.getMessageType() != null ? entity.getMessageType() : "TEXT");
        dto.setIsRead(entity.getIsRead() != null ? entity.getIsRead() : false);
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getCreatedAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            dto.setFormattedTime(entity.getCreatedAt().format(formatter));
        } else {
            dto.setFormattedTime("");
        }

        // Determine isMine
        boolean mine = false;
        if (currentUser != null) {
            String role = (currentUser.getRole() != null) ? currentUser.getRole().getName() : "";
            if ("ROLE_RESCUER".equalsIgnoreCase(role)) {
                mine = "RESCUER".equalsIgnoreCase(entity.getSenderType());
            } else if ("ROLE_COORDINATOR".equalsIgnoreCase(role) || "ROLE_ADMIN".equalsIgnoreCase(role)) {
                mine = "COORDINATOR".equalsIgnoreCase(entity.getSenderType()) || "ADMIN".equalsIgnoreCase(entity.getSenderType());
            } else if ("ROLE_CITIZEN".equalsIgnoreCase(role)) {
                mine = "CITIZEN".equalsIgnoreCase(entity.getSenderType());
            } else {
                mine = (currentUser.getPhone() != null && currentUser.getPhone().equals(entity.getSenderPhone()));
            }
        } else if (citizenPhone != null && !citizenPhone.isBlank()) {
            mine = "CITIZEN".equalsIgnoreCase(entity.getSenderType()) && citizenPhone.trim().equals(entity.getSenderPhone());
        }
        dto.setIsMine(mine);

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSosId() {
        return sosId;
    }

    public void setSosId(Long sosId) {
        this.sosId = sosId;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public Boolean getIsMine() {
        return isMine;
    }

    public void setIsMine(Boolean mine) {
        isMine = mine;
    }
}
