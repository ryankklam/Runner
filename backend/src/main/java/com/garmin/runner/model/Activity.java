package com.garmin.runner.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_name")
    private String activityName;

    @Column(name = "activity_type")
    private String activityType; // 如跑步、骑行、游泳等

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration")
    private Long duration; // 单位：秒

    @Column(name = "distance")
    private Double distance; // 单位：米

    @Column(name = "calories")
    private Integer calories;

    @Column(name = "average_heart_rate")
    private Integer averageHeartRate;

    @Column(name = "max_heart_rate")
    private Integer maxHeartRate;

    @Column(name = "average_pace")
    private Double averagePace; // 平均配速，单位：分钟/公里

    @Column(name = "garmin_activity_id")
    private String garminActivityId;

    @Column(name = "import_date")
    private LocalDateTime importDate;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityDetail> activityDetails;

    @ManyToOne
    @JoinColumn(name = "import_record_id")
    private ImportRecord importRecord;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }
    
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public Long getDuration() { return duration; }
    public void setDuration(Long duration) { this.duration = duration; }
    
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
    
    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }
    
    public Integer getAverageHeartRate() { return averageHeartRate; }
    public void setAverageHeartRate(Integer averageHeartRate) { this.averageHeartRate = averageHeartRate; }
    
    public Integer getMaxHeartRate() { return maxHeartRate; }
    public void setMaxHeartRate(Integer maxHeartRate) { this.maxHeartRate = maxHeartRate; }
    
    public Double getAveragePace() { return averagePace; }
    public void setAveragePace(Double averagePace) { this.averagePace = averagePace; }
    
    public String getGarminActivityId() { return garminActivityId; }
    public void setGarminActivityId(String garminActivityId) { this.garminActivityId = garminActivityId; }
    
    public LocalDateTime getImportDate() { return importDate; }
    public void setImportDate(LocalDateTime importDate) { this.importDate = importDate; }
    
    public List<ActivityDetail> getActivityDetails() { return activityDetails; }
    public void setActivityDetails(List<ActivityDetail> activityDetails) { this.activityDetails = activityDetails; }
    
    public ImportRecord getImportRecord() { return importRecord; }
    public void setImportRecord(ImportRecord importRecord) { this.importRecord = importRecord; }
}
