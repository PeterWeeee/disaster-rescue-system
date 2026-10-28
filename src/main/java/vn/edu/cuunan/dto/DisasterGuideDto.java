package vn.edu.cuunan.dto;

import java.util.ArrayList;
import java.util.List;

public class DisasterGuideDto {
    private String id;
    private String stepCode;
    private String category;
    private String title;
    private String subtitle;
    private String summary;
    private String principle;
    private String iconClass;
    private String colorClass;
    private List<String> overviewPoints = new ArrayList<>();
    private List<DisasterStepDto> steps = new ArrayList<>();
    private List<String> checklist = new ArrayList<>();
    private String emergencyNote;

    public DisasterGuideDto() {
    }

    public DisasterGuideDto(String id, String stepCode, String category, String title, String subtitle,
                            String summary, String principle, String iconClass, String colorClass,
                            List<String> overviewPoints, List<DisasterStepDto> steps,
                            List<String> checklist, String emergencyNote) {
        this.id = id;
        this.stepCode = stepCode;
        this.category = category;
        this.title = title;
        this.subtitle = subtitle;
        this.summary = summary;
        this.principle = principle;
        this.iconClass = iconClass;
        this.colorClass = colorClass;
        if (overviewPoints != null) this.overviewPoints = overviewPoints;
        if (steps != null) this.steps = steps;
        if (checklist != null) this.checklist = checklist;
        this.emergencyNote = emergencyNote;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStepCode() {
        return stepCode;
    }

    public void setStepCode(String stepCode) {
        this.stepCode = stepCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPrinciple() {
        return principle;
    }

    public void setPrinciple(String principle) {
        this.principle = principle;
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public String getColorClass() {
        return colorClass;
    }

    public void setColorClass(String colorClass) {
        this.colorClass = colorClass;
    }

    public List<String> getOverviewPoints() {
        return overviewPoints;
    }

    public void setOverviewPoints(List<String> overviewPoints) {
        this.overviewPoints = overviewPoints;
    }

    public List<DisasterStepDto> getSteps() {
        return steps;
    }

    public void setSteps(List<DisasterStepDto> steps) {
        this.steps = steps;
    }

    public List<String> getChecklist() {
        return checklist;
    }

    public void setChecklist(List<String> checklist) {
        this.checklist = checklist;
    }

    public String getEmergencyNote() {
        return emergencyNote;
    }

    public void setEmergencyNote(String emergencyNote) {
        this.emergencyNote = emergencyNote;
    }
}
