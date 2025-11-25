package com.garmin.runner.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "activity_details")
@Data
public class ActivityDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp")
    private Long timestamp; // 相对于活动开始的时间戳，单位：秒

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "elevation")
    private Double elevation; // 海拔高度，单位：米

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "pace")
    private Double pace; // 当前配速，单位：分钟/公里

    @Column(name = "distance_from_start")
    private Double distanceFromStart; // 距离起点的距离，单位：米

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;
}
