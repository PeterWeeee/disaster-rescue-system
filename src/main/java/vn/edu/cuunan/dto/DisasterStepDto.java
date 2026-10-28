package vn.edu.cuunan.dto;

import java.util.ArrayList;
import java.util.List;

public class DisasterStepDto {
    private int stepNumber;
    private String stepTitle;
    private String headline;
    private String description;
    private String imageUrl;
    private String fallbackUrl;
    private String imageCaption;
    private String aiBadge = "Ảnh AI mô tả trực quan";
    private List<String> actions = new ArrayList<>();
    private List<String> dos = new ArrayList<>();
    private List<String> donts = new ArrayList<>();
    private String safetyTip;

    public DisasterStepDto() {
    }

    public DisasterStepDto(int stepNumber, String stepTitle, String headline, String description,
                           String imageUrl, String imageCaption,
                           List<String> actions, List<String> dos, List<String> donts, String safetyTip) {
        this(stepNumber, stepTitle, headline, description, imageUrl, null, imageCaption, actions, dos, donts, safetyTip);
    }

    public DisasterStepDto(int stepNumber, String stepTitle, String headline, String description,
                           String imageUrl, String fallbackUrl, String imageCaption,
                           List<String> actions, List<String> dos, List<String> donts, String safetyTip) {
        this.stepNumber = stepNumber;
        this.stepTitle = stepTitle;
        this.headline = headline;
        this.description = description;
        this.imageUrl = imageUrl;
        this.fallbackUrl = fallbackUrl;
        this.imageCaption = imageCaption;
        if (actions != null) this.actions = actions;
        if (dos != null) this.dos = dos;
        if (donts != null) this.donts = donts;
        this.safetyTip = safetyTip;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getStepTitle() {
        return stepTitle;
    }

    public void setStepTitle(String stepTitle) {
        this.stepTitle = stepTitle;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFallbackUrl() {
        return fallbackUrl;
    }

    public void setFallbackUrl(String fallbackUrl) {
        this.fallbackUrl = fallbackUrl;
    }

    public String getImageCaption() {
        return imageCaption;
    }

    public void setImageCaption(String imageCaption) {
        this.imageCaption = imageCaption;
    }

    public String getAiBadge() {
        return aiBadge;
    }

    public void setAiBadge(String aiBadge) {
        this.aiBadge = aiBadge;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public List<String> getDos() {
        return dos;
    }

    public void setDos(List<String> dos) {
        this.dos = dos;
    }

    public List<String> getDonts() {
        return donts;
    }

    public void setDonts(List<String> donts) {
        this.donts = donts;
    }

    public String getSafetyTip() {
        return safetyTip;
    }

    public void setSafetyTip(String safetyTip) {
        this.safetyTip = safetyTip;
    }
}
