package com.garmin.runner.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "activities")
@Data
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
}
