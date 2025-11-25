package com.garmin.runner.service.impl;

import com.garmin.runner.model.Activity;
import com.garmin.runner.repository.ActivityRepository;
import com.garmin.runner.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final Logger log = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public Map<String, Object> getOverallStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            log.debug("开始计算总体统计数据");
            
            // 获取所有活动
            log.debug("从数据库获取所有活动记录");
            List<Activity> allActivities = activityRepository.findAll();
            
            // 添加空值检查
            if (allActivities == null || allActivities.isEmpty()) {
                log.warn("未找到任何活动记录，返回默认统计数据");
                statistics.put("totalDistance", 0.0);
                statistics.put("totalActivities", 0);
                statistics.put("totalDuration", 0.0);
                statistics.put("totalCalories", 0);
                statistics.put("averageHeartRate", 0);
                statistics.put("averagePace", 0.0);
                statistics.put("firstActivityDate", null);
                statistics.put("lastActivityDate", null);
                statistics.put("success", true);
                return statistics;
            }
            
            log.debug("成功获取 {} 条活动记录", allActivities.size());
            
            // 计算总距离（米转换为公里）
            log.debug("开始计算总距离");
            double totalDistance = allActivities.stream()
                    .filter(a -> a != null && a.getDistance() != null)
                    .mapToDouble(Activity::getDistance)
                    .sum() / 1000;
            log.debug("计算总距离完成: {} 公里", Math.round(totalDistance * 100) / 100.0);
            
            // 计算总时长（秒转换为小时）
            log.debug("开始计算总时长");
            double totalDuration = allActivities.stream()
                    .filter(a -> a != null && a.getDuration() != null)
                    .mapToLong(Activity::getDuration)
                    .sum() / 3600.0;
            log.debug("计算总时长完成: {} 小时", Math.round(totalDuration * 100) / 100.0);
            
            // 计算总卡路里
            log.debug("开始计算总卡路里");
            int totalCalories = allActivities.stream()
                    .filter(a -> a != null && a.getCalories() != null)
                    .mapToInt(Activity::getCalories)
                    .sum();
            log.debug("计算总卡路里完成: {} 卡路里", totalCalories);
            
            // 计算平均心率
            log.debug("开始计算平均心率");
            OptionalDouble avgHeartRate = allActivities.stream()
                    .filter(a -> a != null && a.getAverageHeartRate() != null)
                    .mapToInt(Activity::getAverageHeartRate)
                    .average();
            log.debug("计算平均心率完成: {}", avgHeartRate.isPresent() ? Math.round(avgHeartRate.getAsDouble()) : 0);
            
            // 计算平均配速
            log.debug("开始计算平均配速");
            OptionalDouble avgPace = allActivities.stream()
                    .filter(a -> a != null && a.getAveragePace() != null)
                    .mapToDouble(Activity::getAveragePace)
                    .average();
            log.debug("计算平均配速完成: {}", avgPace.isPresent() ? Math.round(avgPace.getAsDouble() * 100) / 100.0 : 0);
            
            // 填充统计数据
            log.debug("开始填充统计数据");
            statistics.put("totalDistance", Math.round(totalDistance * 100) / 100.0);
            statistics.put("totalActivities", allActivities.size());
            statistics.put("totalDuration", Math.round(totalDuration * 100) / 100.0);
            statistics.put("totalCalories", totalCalories);
            statistics.put("averageHeartRate", avgHeartRate.isPresent() ? Math.round(avgHeartRate.getAsDouble()) : 0);
            statistics.put("averagePace", avgPace.isPresent() ? Math.round(avgPace.getAsDouble() * 100) / 100.0 : 0);
            
            // 获取最早和最新的活动日期 - 添加更安全的处理
            log.debug("开始获取最早和最新的活动日期");
            Optional<Activity> firstActivity = allActivities.stream()
                    .filter(a -> a != null && a.getStartTime() != null)
                    .min(Comparator.comparing(Activity::getStartTime));
            Optional<Activity> lastActivity = allActivities.stream()
                    .filter(a -> a != null && a.getStartTime() != null)
                    .max(Comparator.comparing(Activity::getStartTime));
            
            statistics.put("firstActivityDate", firstActivity.map(Activity::getStartTime).orElse(null));
            statistics.put("lastActivityDate", lastActivity.map(Activity::getStartTime).orElse(null));
            
            log.debug("最早活动日期: {}, 最新活动日期: {}", 
                    firstActivity.map(Activity::getStartTime).orElse(null), 
                    lastActivity.map(Activity::getStartTime).orElse(null));
            
            statistics.put("success", true);
            log.info("总体统计数据计算成功");
            
        } catch (NullPointerException e) {
            // 针对空指针异常的特定处理
            log.error("计算总体统计数据时发生空指针异常", e);
            statistics.put("success", false);
            statistics.put("message", "计算统计数据失败: 遇到空指针异常，请检查数据完整性");
            log.debug("空指针异常堆栈详情:", e);
        } catch (Exception e) {
            log.error("计算总体统计数据时发生异常", e);
            statistics.put("success", false);
            // 改进错误消息处理，确保不会出现'null'消息
            String errorMessage = e.getMessage() != null ? e.getMessage() : "未知错误";
            statistics.put("message", "计算统计数据失败: " + errorMessage);
            // 添加详细的错误堆栈信息到日志，便于调试
            log.debug("异常堆栈详情:", e);
        }
        
        return statistics;
    }

    @Override
    public Map<String, Object> getStatisticsByDateRange(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            log.debug("开始计算时间范围统计数据: {} 至 {}", startDate, endDate);
            
            // 转换日期为LocalDateTime
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusSeconds(1);
            log.debug("日期转换完成，查询时间范围: {} 至 {}", startDateTime, endDateTime);
            
            // 获取时间范围内的活动
            log.debug("从数据库获取指定时间范围内的活动记录");
            List<Activity> activities = activityRepository.findByStartTimeBetween(startDateTime, endDateTime);
            log.debug("成功获取 {} 条符合条件的活动记录", activities.size());
            
            // 计算统计数据
            log.debug("开始计算时间范围内的统计数据");
            double totalDistance = activities.stream()
                    .mapToDouble(Activity::getDistance)
                    .filter(Objects::nonNull)
                    .sum() / 1000;
            
            double totalDuration = activities.stream()
                    .mapToLong(Activity::getDuration)
                    .filter(Objects::nonNull)
                    .sum() / 3600.0;
            
            int totalCalories = activities.stream()
                    .mapToInt(Activity::getCalories)
                    .filter(Objects::nonNull)
                    .sum();
            
            log.debug("时间范围内统计结果: 距离={}公里, 时长={}小时, 卡路里={}卡, 活动数={}个",
                    Math.round(totalDistance * 100) / 100.0,
                    Math.round(totalDuration * 100) / 100.0,
                    totalCalories,
                    activities.size());
            
            // 填充统计数据
            statistics.put("startDate", startDate);
            statistics.put("endDate", endDate);
            statistics.put("totalDistance", Math.round(totalDistance * 100) / 100.0);
            statistics.put("totalActivities", activities.size());
            statistics.put("totalDuration", Math.round(totalDuration * 100) / 100.0);
            statistics.put("totalCalories", totalCalories);
            statistics.put("activities", activities);
            
            statistics.put("success", true);
            log.info("时间范围统计数据计算成功: {} 至 {}", startDate, endDate);
            
        } catch (Exception e) {
            log.error("计算时间范围统计数据时发生异常: 开始日期={}, 结束日期={}", startDate, endDate, e);
            statistics.put("success", false);
            statistics.put("message", "计算时间范围统计数据失败: " + e.getMessage());
            log.debug("异常堆栈详情:", e);
        }
        
        return statistics;
    }

    @Override
    public List<Map<String, Object>> getStatisticsByActivityType() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        try {
            // 获取所有活动
            List<Activity> allActivities = activityRepository.findAll();
            
            // 按活动类型分组
            Map<String, List<Activity>> activitiesByType = allActivities.stream()
                    .filter(a -> a.getActivityType() != null)
                    .collect(Collectors.groupingBy(Activity::getActivityType));
            
            // 计算每种活动类型的统计数据
            for (Map.Entry<String, List<Activity>> entry : activitiesByType.entrySet()) {
                String activityType = entry.getKey();
                List<Activity> activities = entry.getValue();
                
                double totalDistance = activities.stream()
                        .mapToDouble(Activity::getDistance)
                        .filter(Objects::nonNull)
                        .sum() / 1000;
                
                int totalActivities = activities.size();
                
                Map<String, Object> typeStats = new HashMap<>();
                typeStats.put("activityType", activityType);
                typeStats.put("totalDistance", Math.round(totalDistance * 100) / 100.0);
                typeStats.put("totalActivities", totalActivities);
                typeStats.put("percentage", allActivities.isEmpty() ? 0 : Math.round((double) totalActivities / allActivities.size() * 100));
                
                result.add(typeStats);
            }
            
            // 按活动数量降序排序
            result.sort((a, b) -> Integer.compare((int) b.get("totalActivities"), (int) a.get("totalActivities")));
            
        } catch (Exception e) {
            // 如果发生错误，返回空列表
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getRecentActivities(int limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        try {
            // 获取所有活动并按开始时间降序排序
            List<Activity> recentActivities = activityRepository.findAll().stream()
                    .sorted(Comparator.comparing(Activity::getStartTime).reversed())
                    .limit(limit)
                    .collect(Collectors.toList());
            
            // 转换为前端需要的格式
            for (Activity activity : recentActivities) {
                Map<String, Object> activityMap = new HashMap<>();
                activityMap.put("id", activity.getId());
                activityMap.put("activityName", activity.getActivityName());
                activityMap.put("activityType", activity.getActivityType());
                activityMap.put("startTime", activity.getStartTime());
                activityMap.put("distance", activity.getDistance() != null ? activity.getDistance() / 1000 : 0);
                activityMap.put("duration", activity.getDuration());
                activityMap.put("calories", activity.getCalories());
                activityMap.put("averageHeartRate", activity.getAverageHeartRate());
                activityMap.put("averagePace", activity.getAveragePace());
                
                result.add(activityMap);
            }
            
        } catch (Exception e) {
            // 如果发生错误，返回空列表
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getActivityTrendByMonth(int months) {
        List<Map<String, Object>> trendData = new ArrayList<>();
        
        try {
            // 获取当前时间
            LocalDate now = LocalDate.now();
            
            // 生成最近N个月的数据
            for (int i = months - 1; i >= 0; i--) {
                YearMonth yearMonth = YearMonth.from(now.minusMonths(i));
                LocalDate monthStart = yearMonth.atDay(1);
                LocalDate monthEnd = yearMonth.atEndOfMonth();
                
                // 获取该月的活动
                LocalDateTime startDateTime = monthStart.atStartOfDay();
                LocalDateTime endDateTime = monthEnd.plusDays(1).atStartOfDay().minusSeconds(1);
                List<Activity> monthActivities = activityRepository.findByStartTimeBetween(startDateTime, endDateTime);
                
                // 计算统计数据
                double totalDistance = monthActivities.stream()
                        .mapToDouble(Activity::getDistance)
                        .filter(Objects::nonNull)
                        .sum() / 1000;
                
                int totalActivities = monthActivities.size();
                
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", yearMonth.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")));
                monthData.put("totalDistance", Math.round(totalDistance * 100) / 100.0);
                monthData.put("totalActivities", totalActivities);
                
                trendData.add(monthData);
            }
            
        } catch (Exception e) {
            // 如果发生错误，返回空列表
        }
        
        return trendData;
    }

    @Override
    public Map<String, Object> getHeartRateZoneStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        Map<String, Integer> zoneCount = new HashMap<>();
        
        log.debug("开始计算心率区间统计数据");
        
        // 初始化心率区间
        zoneCount.put("恢复区 (50-60%)", 0);
        zoneCount.put("有氧区 (60-70%)", 0);
        zoneCount.put("阈值区 (70-80%)", 0);
        zoneCount.put("无氧区 (80-90%)", 0);
        zoneCount.put("极限区 (90-100%)", 0);
        
        try {
            // 获取所有活动
            log.debug("从数据库获取所有活动记录用于心率区间统计");
            List<Activity> allActivities = activityRepository.findAll();
            log.debug("成功获取 {} 条活动记录", allActivities.size());
            
            // 简化版本：基于平均心率进行区间统计
            // 实际应用中应该基于最大心率百分比进行计算
            log.debug("开始按心率区间分类统计活动");
            int activitiesWithHeartRate = 0;
            
            for (Activity activity : allActivities) {
                if (activity.getAverageHeartRate() != null) {
                    activitiesWithHeartRate++;
                    int avgHR = activity.getAverageHeartRate();
                    
                    // 这里使用简化的心率区间判断
                    // 实际应该基于个人最大心率计算百分比
                    if (avgHR < 120) {
                        zoneCount.put("恢复区 (50-60%)", zoneCount.get("恢复区 (50-60%)") + 1);
                    } else if (avgHR < 140) {
                        zoneCount.put("有氧区 (60-70%)", zoneCount.get("有氧区 (60-70%)") + 1);
                    } else if (avgHR < 160) {
                        zoneCount.put("阈值区 (70-80%)", zoneCount.get("阈值区 (70-80%)") + 1);
                    } else if (avgHR < 180) {
                        zoneCount.put("无氧区 (80-90%)", zoneCount.get("无氧区 (80-90%)") + 1);
                    } else {
                        zoneCount.put("极限区 (90-100%)", zoneCount.get("极限区 (90-100%)") + 1);
                    }
                }
            }
            
            log.debug("心率区间统计完成, 有心率数据的活动数量: {}", activitiesWithHeartRate);
            log.debug("心率区间分布: {}", zoneCount);
            
            statistics.put("zoneDistribution", zoneCount);
            statistics.put("totalActivities", allActivities.size());
            statistics.put("activitiesWithHeartRate", activitiesWithHeartRate);
            statistics.put("success", true);
            log.info("心率区间统计计算成功");
            
        } catch (Exception e) {
            log.error("计算心率区间统计时发生异常", e);
            statistics.put("success", false);
            statistics.put("message", "计算心率区间统计失败: " + e.getMessage());
            log.debug("异常堆栈详情:", e);
        }
        
        return statistics;
    }

    @Override
    public Map<String, Object> getPaceZoneStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        Map<String, Integer> zoneCount = new HashMap<>();
        
        log.debug("开始计算配速区间统计数据");
        
        // 初始化配速区间（单位：分钟/公里）
        zoneCount.put("轻松跑 (>6'30\")", 0);
        zoneCount.put("有氧跑 (5'30\"-6'30\")", 0);
        zoneCount.put("马拉松配速 (4'30\"-5'30\")", 0);
        zoneCount.put("阈值跑 (3'30\"-4'30\")", 0);
        zoneCount.put("间歇跑 (<3'30\")", 0);
        
        try {
            // 获取所有跑步活动
            log.debug("从数据库获取所有跑步活动记录用于配速区间统计");
            List<Activity> allActivities = activityRepository.findAll();
            log.debug("获取到 {} 条活动记录，开始筛选跑步活动", allActivities.size());
            
            List<Activity> runningActivities = allActivities.stream()
                    .filter(a -> a.getActivityType() != null && 
                            (a.getActivityType().contains("Run") || 
                             a.getActivityType().contains("跑步") ||
                             a.getActivityType().equals("Running")))
                    .filter(a -> a.getAveragePace() != null)
                    .collect(Collectors.toList());
            
            log.debug("成功筛选出 {} 条跑步活动记录（有配速数据）", runningActivities.size());
            
            // 统计配速区间
            log.debug("开始按配速区间分类统计跑步活动");
            for (Activity activity : runningActivities) {
                double pace = activity.getAveragePace();
                
                if (pace >= 6.5) {
                    zoneCount.put("轻松跑 (>6'30\")", zoneCount.get("轻松跑 (>6'30\")") + 1);
                } else if (pace >= 5.5) {
                    zoneCount.put("有氧跑 (5'30\"-6'30\")", zoneCount.get("有氧跑 (5'30\"-6'30\")") + 1);
                } else if (pace >= 4.5) {
                    zoneCount.put("马拉松配速 (4'30\"-5'30\")", zoneCount.get("马拉松配速 (4'30\"-5'30\")") + 1);
                } else if (pace >= 3.5) {
                    zoneCount.put("阈值跑 (3'30\"-4'30\")", zoneCount.get("阈值跑 (3'30\"-4'30\")") + 1);
                } else {
                    zoneCount.put("间歇跑 (<3'30\")", zoneCount.get("间歇跑 (<3'30\")") + 1);
                }
            }
            
            log.debug("配速区间统计完成");
            log.debug("配速区间分布: {}", zoneCount);
            
            statistics.put("zoneDistribution", zoneCount);
            statistics.put("totalRunningActivities", runningActivities.size());
            statistics.put("success", true);
            log.info("配速区间统计计算成功");
            
        } catch (Exception e) {
            log.error("计算配速区间统计时发生异常", e);
            statistics.put("success", false);
            statistics.put("message", "计算配速区间统计失败: " + e.getMessage());
            log.debug("异常堆栈详情:", e);
        }
        
        return statistics;
    }
}
