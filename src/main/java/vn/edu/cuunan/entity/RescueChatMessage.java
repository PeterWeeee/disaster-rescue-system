package vn.edu.cuunan.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rescue_chat_messages")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RescueChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sos_request_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SosRequest sosRequest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assignment_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RescueAssignment assignment;

    @Column(name = "sender_type", nullable = false, length = 20)
    private String senderType; // CITIZEN, RESCUER, COORDINATOR, SYSTEM

    @Column(name = "sender_name", nullable = false, length = 100)
    private String senderName;

    @Column(name = "sender_phone", length = 20)
    private String senderPhone;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "message_type", length = 30)
    private String messageType = "TEXT"; // TEXT, QUICK_ALERT, LOCATION_PING, SYSTEM_INFO

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public RescueChatMessage() {
    }

    public RescueChatMessage(SosRequest sosRequest, RescueAssignment assignment, String senderType,
                              String senderName, String senderPhone, String content, String messageType) {
        this.sosRequest = sosRequest;
        this.assignment = assignment;
        this.senderType = senderType;
        this.senderName = senderName;
        this.senderPhone = senderPhone;
        this.content = content;
        this.messageType = (messageType != null && !messageType.isBlank()) ? messageType : "TEXT";
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.isRead == null) {
            this.isRead = false;
        }
        if (this.messageType == null || this.messageType.isBlank()) {
            this.messageType = "TEXT";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SosRequest getSosRequest() {
        return sosRequest;
    }

    public void setSosRequest(SosRequest sosRequest) {
        this.sosRequest = sosRequest;
    }

    public RescueAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(RescueAssignment assignment) {
        this.assignment = assignment;
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

    public static RescueChatMessageBuilder builder() {
        return new RescueChatMessageBuilder();
    }

    public static class RescueChatMessageBuilder {
        private Long id;
        private SosRequest sosRequest;
        private RescueAssignment assignment;
        private String senderType;
        private String senderName;
        private String senderPhone;
        private String content;
        private String messageType = "TEXT";
        private Boolean isRead = false;
        private LocalDateTime createdAt;

        public RescueChatMessageBuilder id(Long id) { this.id = id; return this; }
        public RescueChatMessageBuilder sosRequest(SosRequest sosRequest) { this.sosRequest = sosRequest; return this; }
        public RescueChatMessageBuilder assignment(RescueAssignment assignment) { this.assignment = assignment; return this; }
        public RescueChatMessageBuilder senderType(String senderType) { this.senderType = senderType; return this; }
        public RescueChatMessageBuilder senderName(String senderName) { this.senderName = senderName; return this; }
        public RescueChatMessageBuilder senderPhone(String senderPhone) { this.senderPhone = senderPhone; return this; }
        public RescueChatMessageBuilder content(String content) { this.content = content; return this; }
        public RescueChatMessageBuilder messageType(String messageType) { this.messageType = messageType; return this; }
        public RescueChatMessageBuilder isRead(Boolean isRead) { this.isRead = isRead; return this; }
        public RescueChatMessageBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public RescueChatMessage build() {
            RescueChatMessage msg = new RescueChatMessage();
            msg.setId(this.id);
            msg.setSosRequest(this.sosRequest);
            msg.setAssignment(this.assignment);
            msg.setSenderType(this.senderType);
            msg.setSenderName(this.senderName);
            msg.setSenderPhone(this.senderPhone);
            msg.setContent(this.content);
            msg.setMessageType(this.messageType != null ? this.messageType : "TEXT");
            msg.setIsRead(this.isRead != null ? this.isRead : false);
            msg.setCreatedAt(this.createdAt != null ? this.createdAt : LocalDateTime.now());
            return msg;
        }
    }
}
