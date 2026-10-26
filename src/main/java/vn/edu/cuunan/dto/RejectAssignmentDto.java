package vn.edu.cuunan.dto;

import jakarta.validation.constraints.NotBlank;

public class RejectAssignmentDto {

    @NotBlank(message = "Lý do từ chối không được để trống")
    private String reason;

    public RejectAssignmentDto() {
    }

    public RejectAssignmentDto(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
