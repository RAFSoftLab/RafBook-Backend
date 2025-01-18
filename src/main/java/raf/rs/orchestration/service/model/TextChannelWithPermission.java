package raf.rs.orchestration.service.model;

import lombok.Data;

@Data
public class TextChannelWithPermission {
    private Long textChannelId;
    private String textChannelName;
    private String textChannelDescription;
    private Long studiesId;
    private String studiesName;
    private String studiesDescription;
    private Long studyProgramId;
    private String studyProgramName;
    private String studyProgramDescription;
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    private Boolean hasWritePermission;
}
