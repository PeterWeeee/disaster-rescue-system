package vn.edu.cuunan.dto;

import jakarta.validation.constraints.NotNull;

public class AssignTaskRequestDto {

    @NotNull(message = "Mã ca cứu hộ SOS không được để trống")
    private Long sosRequestId;

    @NotNull(message = "Mã đội cứu hộ không được để trống")
    private Long teamId;

    private String notes;

    public AssignTaskRequestDto() {
    }

    public AssignTaskRequestDto(Long sosRequestId, Long teamId, String notes) {
        this.sosRequestId = sosRequestId;
        this.teamId = teamId;
        this.notes = notes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long sosRequestId;
        private Long teamId;
        private String notes;

        public Builder sosRequestId(Long sosRequestId) {
            this.sosRequestId = sosRequestId;
            return this;
        }

        public Builder teamId(Long teamId) {
            this.teamId = teamId;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public AssignTaskRequestDto build() {
            return new AssignTaskRequestDto(sosRequestId, teamId, notes);
        }
    }

    public Long getSosRequestId() {
        return sosRequestId;
    }

    public void setSosRequestId(Long sosRequestId) {
        this.sosRequestId = sosRequestId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
