package com.garmin.runner.controller;

import com.garmin.runner.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private static final Logger log = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取总体统计数据
     */
    @GetMapping("/overall")
    public ResponseEntity<Map<String, Object>> getOverallStatistics() {
        log.info("接收API请求: GET /statistics/overall");
        try {
            Map<String, Object> statistics = statisticsService.getOverallStatistics();
            boolean success = (boolean) statistics.getOrDefault("success", false);
            
            if (success) {
                log.info("API请求成功: GET /statistics/overall, 返回统计数据");
                return ResponseEntity.ok(statistics);
            } else {
                String message = (String) statistics.getOrDefault("message", "未知错误");
                log.warn("API请求失败: GET /statistics/overall, 返回400错误, 错误信息: {}", message);
                return ResponseEntity.badRequest().body(statistics);
            }
        } catch (Exception e) {
            log.error("API请求异常: GET /statistics/overall", e);
            Map<String, Object> error = Map.of(
                    "success", false,
                    "message", "处理请求时发生异常: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 按时间范围获取统计数据
     */
    @GetMapping("/date-range")
    public ResponseEntity<Map<String, Object>> getStatisticsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("接收API请求: GET /statistics/date-range, 开始日期: {}, 结束日期: {}", startDate, endDate);
        
        if (startDate.isAfter(endDate)) {
            Map<String, Object> error = Map.of(
                    "success", false,
                    "message", "开始日期不能晚于结束日期"
            );
            log.warn("日期参数验证失败: 开始日期({})晚于结束日期({})");
            return ResponseEntity.badRequest().body(error);
        }
        
        try {
            Map<String, Object> statistics = statisticsService.getStatisticsByDateRange(startDate, endDate);
            if ((boolean) statistics.getOrDefault("success", false)) {
                log.info("日期范围统计请求成功: {} 至 {}", startDate, endDate);
                return ResponseEntity.ok(statistics);
            } else {
                String message = (String) statistics.getOrDefault("message", "未知错误");
                log.warn("日期范围统计请求失败: {}, 错误信息: {}", message);
                return ResponseEntity.badRequest().body(statistics);
            }
        } catch (Exception e) {
            log.error("日期范围统计请求异常: 开始日期={}, 结束日期={}", startDate, endDate, e);
            Map<String, Object> error = Map.of(
                    "success", false,
                    "message", "处理日期范围请求时发生异常: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 按活动类型分组统计
     */
    @GetMapping("/by-type")
    public ResponseEntity<List<Map<String, Object>>> getStatisticsByActivityType() {
        log.info("接收API请求: GET /statistics/by-type");
        try {
            List<Map<String, Object>> statistics = statisticsService.getStatisticsByActivityType();
            log.info("按活动类型统计请求成功, 返回 {} 条数据", statistics.size());
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("按活动类型统计请求异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    /**
     * 获取最近的活动列表
     */
    @GetMapping("/recent-activities")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivities(
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("接收API请求: GET /statistics/recent-activities, limit={}", limit);
        
        if (limit <= 0 || limit > 100) {
            log.warn("参数limit超出范围, 使用默认值: 10");
            limit = 10; // 设置默认值和上限
        }
        
        try {
            List<Map<String, Object>> activities = statisticsService.getRecentActivities(limit);
            log.info("最近活动列表请求成功, 返回 {} 条数据", activities.size());
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            log.error("最近活动列表请求异常, limit={}", limit, e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    /**
     * 获取月度活动趋势数据
     */
    @GetMapping("/trend/monthly")
    public ResponseEntity<List<Map<String, Object>>> getActivityTrendByMonth(
            @RequestParam(defaultValue = "6") int months) {
        
        log.info("接收API请求: GET /statistics/trend/monthly, months={}", months);
        
        if (months <= 0 || months > 24) {
            log.warn("参数months超出范围, 使用默认值: 6");
            months = 6; // 设置默认值和上限
        }
        
        try {
            List<Map<String, Object>> trendData = statisticsService.getActivityTrendByMonth(months);
            log.info("月度趋势数据请求成功, 返回 {} 个月的数据", trendData.size());
            return ResponseEntity.ok(trendData);
        } catch (Exception e) {
            log.error("月度趋势数据请求异常, months={}", months, e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    /**
     * 获取心率区间统计
     */
    @GetMapping("/heart-rate-zones")
    public ResponseEntity<Map<String, Object>> getHeartRateZoneStatistics() {
        log.info("接收API请求: GET /statistics/heart-rate-zones");
        try {
            Map<String, Object> statistics = statisticsService.getHeartRateZoneStatistics();
            boolean success = (boolean) statistics.getOrDefault("success", false);
            
            if (success) {
                log.info("心率区间统计请求成功");
                return ResponseEntity.ok(statistics);
            } else {
                String message = (String) statistics.getOrDefault("message", "未知错误");
                log.warn("心率区间统计请求失败, 错误信息: {}", message);
                return ResponseEntity.badRequest().body(statistics);
            }
        } catch (Exception e) {
            log.error("心率区间统计请求异常", e);
            Map<String, Object> error = Map.of(
                    "success", false,
                    "message", "处理心率区间统计请求时发生异常: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 获取配速区间统计
     */
    @GetMapping("/pace-zones")
    public ResponseEntity<Map<String, Object>> getPaceZoneStatistics() {
        log.info("接收API请求: GET /statistics/pace-zones");
        try {
            Map<String, Object> statistics = statisticsService.getPaceZoneStatistics();
            boolean success = (boolean) statistics.getOrDefault("success", false);
            
            if (success) {
                log.info("配速区间统计请求成功");
                return ResponseEntity.ok(statistics);
            } else {
                String message = (String) statistics.getOrDefault("message", "未知错误");
                log.warn("配速区间统计请求失败, 错误信息: {}", message);
                return ResponseEntity.badRequest().body(statistics);
            }
        } catch (Exception e) {
            log.error("配速区间统计请求异常", e);
            Map<String, Object> error = Map.of(
                    "success", false,
                    "message", "处理配速区间统计请求时发生异常: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        log.debug("接收API请求: GET /statistics/health");
        try {
            log.debug("健康检查请求成功");
            return ResponseEntity.ok(Map.of("status", "healthy", "message", "统计分析服务正常运行"));
        } catch (Exception e) {
            log.error("健康检查请求异常", e);
            return ResponseEntity.status(500).body(Map.of("status", "unhealthy", "message", "服务异常: " + e.getMessage()));
        }
    }
}
