package vn.edu.cuunan.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminUserUpdateDto {

    @NotBlank
    private String role;

    @NotBlank
    private String status;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}