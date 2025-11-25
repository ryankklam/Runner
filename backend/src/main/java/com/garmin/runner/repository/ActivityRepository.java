package com.garmin.runner.repository;

import com.garmin.runner.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // 根据活动类型查询活动
    List<Activity> findByActivityType(String activityType);

    // 根据时间范围查询活动
    List<Activity> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // 查询指定时间段内的总距离
    @Query("SELECT SUM(a.distance) FROM Activity a WHERE a.startTime BETWEEN :start AND :end")
    Double calculateTotalDistance(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 查询指定时间段内的总活动次数
    @Query("SELECT COUNT(a) FROM Activity a WHERE a.startTime BETWEEN :start AND :end")
    Long countActivities(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 根据导入记录查询活动
    List<Activity> findByImportRecordId(Long importRecordId);
}
