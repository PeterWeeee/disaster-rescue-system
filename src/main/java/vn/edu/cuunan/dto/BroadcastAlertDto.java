package vn.edu.cuunan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BroadcastAlertDto {

    @NotBlank
    @Size(max = 200)
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String severity;
    private String affectedRegion;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getAffectedRegion() { return affectedRegion; }
    public void setAffectedRegion(String affectedRegion) { this.affectedRegion = affectedRegion; }
}