package vn.edu.cuunan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SosRequestDto {
    private String contactName;
    private String contactPhone;
    private String secondaryPhone;
    private Double latitude;
    private Double longitude;
    private String addressDetail;
    private String urgencyLevel;
    private Integer peopleCount;
    private Boolean hasElderly;
    private Boolean hasChildren;
    private Boolean hasInjured;
    private String description;
    private String imageUrl;
}
