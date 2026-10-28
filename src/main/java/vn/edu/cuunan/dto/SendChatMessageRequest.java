package vn.edu.cuunan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SendChatMessageRequest {

    @NotBlank(message = "Nội dung tin nhắn không được để trống")
    @Size(max = 1000, message = "Nội dung tin nhắn không được vượt quá 1000 ký tự")
    private String content;

    private String senderType; // CITIZEN, RESCUER, COORDINATOR, SYSTEM
    private String senderName;
    private String senderPhone;
    private String messageType; // TEXT, QUICK_ALERT, LOCATION_PING, SYSTEM_INFO
    private Long assignmentId;

    public SendChatMessageRequest() {
    }

    public SendChatMessageRequest(String content) {
        this.content = content;
        this.messageType = "TEXT";
    }

    public SendChatMessageRequest(String content, String senderType, String senderName, String messageType) {
        this.content = content;
        this.senderType = senderType;
        this.senderName = senderName;
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }
}
