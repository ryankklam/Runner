package com.garmin.runner.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "import_records")
@Data
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
}
