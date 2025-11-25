package com.garmin.runner.service.impl;

import com.garmin.runner.model.Activity;
import com.garmin.runner.repository.ActivityRepository;
import com.garmin.runner.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public Map<String, Object> getOverallStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            // 获取所有活动
            List<Activity> allActivities = activityRepository.findAll();
            
            // 计算总距离（米转换为公里）
            double totalDistance = allActivities.stream()
                    .mapToDouble(Activity::getDistance)
                    .filter(Objects::nonNull)
                    .sum() / 1000;
            
            // 计算总时长（秒转换为小时）
            double totalDuration = allActivities.stream()
                    .mapToLong(Activity::getDuration)
                    .filter(Objects::nonNull)
                    .sum() / 3600.0;
            
            // 计算总卡路里
            int totalCalories = allActivities.stream()
                    .mapToInt(Activity::getCalories)
                    .filter(Objects::nonNull)
                    .sum();
            
            // 计算平均心率
            OptionalDouble avgHeartRate = allActivities.stream()
                    .mapToInt(Activity::getAverageHeartRate)
                    .filter(Objects::nonNull)
                    .average();
            
            // 计算平均配速
            OptionalDouble avgPace = allActivities.stream()
                    .mapToDouble(Activity::getAveragePace)
                    .filter(Objects::nonNull)
                    .average();
            
            // 填充统计数据
            statistics.put("totalDistance", Math.round(totalDistance * 100) / 100.0);
            statistics.put("totalActivities", allActivities.size());
            statistics.put("totalDuration", Math.round(totalDuration * 100) / 100.0);
            statistics.put("totalCalories", totalCalories);
            statistics.put("averageHeartRate", avgHeartRate.isPresent() ? Math.round(avgHeartRate.getAsDouble()) : 0);
            statistics.put("averagePace", avgPace.isPresent() ? Math.round(avgPace.getAsDouble() * 100) / 100.0 : 0);
            
            // 获取最早和最新的活动日期
            Optional<Activity> firstActivity = allActivities.stream()
                    .min(Comparator.comparing(Activity::getStartTime));
            Optional<Activity> lastActivity = allActivities.stream()
                    .max(Comparator.comparing(Activity::getStartTime));
            
            statistics.put("firstActivityDate", firstActivity.map(Activity::getStartTime).orElse(null));
            statistics.put("lastActivityDate", lastActivity.map(Activity::getStartTime).orElse(null));
            
            statistics.put("success", true);
            
        } catch (Exception e) {
            statistics.put("success", false);
            statistics.put("message", "计算统计数据失败: " + e.getMessage());
        }
        
        return statistics;
    }

    @Override
    public Map<String, Object> getStatisticsByDateRange(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            // 转换日期为LocalDateTime
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusSeconds(1);
            
            // 获取时间范围内的活动
            List<Activity> activities = activityRepository.findByStartTimeBetween(startDateTime, endDateTime);
            
            // 计算统计数据
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
            
            // 填充统计数据
            statistics.put("startDate", startDate);
            statistics.put("endDate", endDate);
            statistics.put("totalDistance", Math.round(totalDistance * 100) / 100.0);
            statistics.put("totalActivities", activities.size());
            statistics.put("totalDuration", Math.round(totalDuration * 100) / 100.0);
            statistics.put("totalCalories", totalCalories);
            statistics.put("activities", activities);
            
            statistics.put("success", true);
            
        } catch (Exception e) {
            statistics.put("success", false);
            statistics.put("message", "计算时间范围统计数据失败: " + e.getMessage());
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
        
        // 初始化心率区间
        zoneCount.put("恢复区 (50-60%)", 0);
        zoneCount.put("有氧区 (60-70%)", 0);
        zoneCount.put("阈值区 (70-80%)", 0);
        zoneCount.put("无氧区 (80-90%)", 0);
        zoneCount.put("极限区 (90-100%)", 0);
        
        try {
            // 获取所有活动
            List<Activity> allActivities = activityRepository.findAll();
            
            // 简化版本：基于平均心率进行区间统计
            // 实际应用中应该基于最大心率百分比进行计算
            for (Activity activity : allActivities) {
                if (activity.getAverageHeartRate() != null) {
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
            
            statistics.put("zoneDistribution", zoneCount);
            statistics.put("totalActivities", allActivities.size());
            statistics.put("success", true);
            
        } catch (Exception e) {
            statistics.put("success", false);
            statistics.put("message", "计算心率区间统计失败: " + e.getMessage());
        }
        
        return statistics;
    }

    @Override
    public Map<String, Object> getPaceZoneStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        Map<String, Integer> zoneCount = new HashMap<>();
        
        // 初始化配速区间（单位：分钟/公里）
        zoneCount.put("轻松跑 (>6'30\")", 0);
        zoneCount.put("有氧跑 (5'30\"-6'30\")", 0);
        zoneCount.put("马拉松配速 (4'30\"-5'30\")", 0);
        zoneCount.put("阈值跑 (3'30\"-4'30\")", 0);
        zoneCount.put("间歇跑 (<3'30\")", 0);
        
        try {
            // 获取所有跑步活动
            List<Activity> runningActivities = activityRepository.findAll().stream()
                    .filter(a -> a.getActivityType() != null && 
                            (a.getActivityType().contains("Run") || 
                             a.getActivityType().contains("跑步") ||
                             a.getActivityType().equals("Running")))
                    .filter(a -> a.getAveragePace() != null)
                    .collect(Collectors.toList());
            
            // 统计配速区间
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
            
            statistics.put("zoneDistribution", zoneCount);
            statistics.put("totalRunningActivities", runningActivities.size());
            statistics.put("success", true);
            
        } catch (Exception e) {
            statistics.put("success", false);
            statistics.put("message", "计算配速区间统计失败: " + e.getMessage());
        }
        
        return statistics;
    }
}
