package com.garmin.runner.controller;

import com.garmin.runner.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取总体统计数据
     */
    @GetMapping("/overall")
    public ResponseEntity<Map<String, Object>> getOverallStatistics() {
        Map<String, Object> statistics = statisticsService.getOverallStatistics();
        if ((boolean) statistics.getOrDefault("success", false)) {
            return ResponseEntity.ok(statistics);
        } else {
            return ResponseEntity.badRequest().body(statistics);
        }
    }

    /**
     * 按时间范围获取统计数据
     */
    @GetMapping("/date-range")
    public ResponseEntity<Map<String, Object>> getStatisticsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate.isAfter(endDate)) {
            Map<String, Object> error = Map.of(
                    "success", false,
                    "message", "开始日期不能晚于结束日期"
            );
            return ResponseEntity.badRequest().body(error);
        }
        
        Map<String, Object> statistics = statisticsService.getStatisticsByDateRange(startDate, endDate);
        if ((boolean) statistics.getOrDefault("success", false)) {
            return ResponseEntity.ok(statistics);
        } else {
            return ResponseEntity.badRequest().body(statistics);
        }
    }

    /**
     * 按活动类型分组统计
     */
    @GetMapping("/by-type")
    public ResponseEntity<List<Map<String, Object>>> getStatisticsByActivityType() {
        List<Map<String, Object>> statistics = statisticsService.getStatisticsByActivityType();
        return ResponseEntity.ok(statistics);
    }

    /**
     * 获取最近的活动列表
     */
    @GetMapping("/recent-activities")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivities(
            @RequestParam(defaultValue = "10") int limit) {
        
        if (limit <= 0 || limit > 100) {
            limit = 10; // 设置默认值和上限
        }
        
        List<Map<String, Object>> activities = statisticsService.getRecentActivities(limit);
        return ResponseEntity.ok(activities);
    }

    /**
     * 获取月度活动趋势数据
     */
    @GetMapping("/trend/monthly")
    public ResponseEntity<List<Map<String, Object>>> getActivityTrendByMonth(
            @RequestParam(defaultValue = "6") int months) {
        
        if (months <= 0 || months > 24) {
            months = 6; // 设置默认值和上限
        }
        
        List<Map<String, Object>> trendData = statisticsService.getActivityTrendByMonth(months);
        return ResponseEntity.ok(trendData);
    }

    /**
     * 获取心率区间统计
     */
    @GetMapping("/heart-rate-zones")
    public ResponseEntity<Map<String, Object>> getHeartRateZoneStatistics() {
        Map<String, Object> statistics = statisticsService.getHeartRateZoneStatistics();
        if ((boolean) statistics.getOrDefault("success", false)) {
            return ResponseEntity.ok(statistics);
        } else {
            return ResponseEntity.badRequest().body(statistics);
        }
    }

    /**
     * 获取配速区间统计
     */
    @GetMapping("/pace-zones")
    public ResponseEntity<Map<String, Object>> getPaceZoneStatistics() {
        Map<String, Object> statistics = statisticsService.getPaceZoneStatistics();
        if ((boolean) statistics.getOrDefault("success", false)) {
            return ResponseEntity.ok(statistics);
        } else {
            return ResponseEntity.badRequest().body(statistics);
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "healthy", "message", "统计分析服务正常运行"));
    }
}
