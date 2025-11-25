package com.garmin.runner.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "import_records")
public class ImportRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "import_time")
    private LocalDateTime importTime;

    @Column(name = "status")
    private String status; // 成功、失败、部分成功

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "activity_count")
    private Integer activityCount; // 导入的活动数量

    @OneToMany(mappedBy = "importRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Activity> activities;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public LocalDateTime getImportTime() { return importTime; }
    public void setImportTime(LocalDateTime importTime) { this.importTime = importTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public Integer getActivityCount() { return activityCount; }
    public void setActivityCount(Integer activityCount) { this.activityCount = activityCount; }
    
    public List<Activity> getActivities() { return activities; }
    public void setActivities(List<Activity> activities) { this.activities = activities; }
}
