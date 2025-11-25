package com.garmin.runner.service.impl;

import com.garmin.runner.model.Activity;
import com.garmin.runner.repository.ActivityRepository;
import com.garmin.runner.service.ActivityService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 运动活动服务实现类
 */
@Service
public class ActivityServiceImpl implements ActivityService {

    private static final Logger logger = Logger.getLogger(ActivityServiceImpl.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public Map<String, Object> importActivitiesFromCSV(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failureCount = 0;
        List<String> errors = new ArrayList<>();

        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .build())) {

            for (CSVRecord record : csvParser) {
                try {
                    Activity activity = parseActivityFromRecord(record);
                    if (activity != null) {
                        activityRepository.save(activity);
                        successCount++;
                    } else {
                        failureCount++;
                        errors.add("无效的记录: " + record.toString());
                    }
                } catch (Exception e) {
                    failureCount++;
                    errors.add("记录处理失败: " + e.getMessage());
                    logger.log(Level.WARNING, "Error processing record: " + record.toString(), e);
                }
            }

            result.put("success", true);
            result.put("message", "文件导入完成");
            result.put("successCount", successCount);
            result.put("failureCount", failureCount);
            if (!errors.isEmpty()) {
                result.put("errors", errors);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "CSV导入失败", e);
            result.put("success", false);
            result.put("message", "文件处理过程中发生错误: " + e.getMessage());
        }

        return result;
    }

    private Activity parseActivityFromRecord(CSVRecord record) {
        Activity activity = new Activity();

        try {
            // 设置活动类型
            String activityType = record.get("Activity Type");
            if (activityType == null || activityType.trim().isEmpty()) {
                return null;
            }
            activity.setActivityType(activityType.trim());

            // 设置日期
            String dateStr = record.get("Date");
            if (dateStr == null || dateStr.trim().isEmpty()) {
                return null;
            }
            try {
                LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
                activity.setDate(date);
            } catch (DateTimeParseException e) {
                // 尝试其他日期格式
                try {
                    activity.setDate(LocalDate.parse(dateStr));
                } catch (DateTimeParseException ex) {
                    throw new IllegalArgumentException("无效的日期格式: " + dateStr);
                }
            }

            // 设置时长（分钟）
            String durationStr = record.get("Duration");
            if (durationStr != null && !durationStr.trim().isEmpty()) {
                try {
                    activity.setDuration(Double.parseDouble(durationStr));
                } catch (NumberFormatException e) {
                    activity.setDuration(0.0);
                }
            }

            // 设置距离（公里）
            String distanceStr = record.get("Distance");
            if (distanceStr != null && !distanceStr.trim().isEmpty()) {
                try {
                    activity.setDistance(Double.parseDouble(distanceStr));
                } catch (NumberFormatException e) {
                    activity.setDistance(0.0);
                }
            }

            // 设置卡路里
            String caloriesStr = record.get("Calories");
            if (caloriesStr != null && !caloriesStr.trim().isEmpty()) {
                try {
                    activity.setCalories(Integer.parseInt(caloriesStr));
                } catch (NumberFormatException e) {
                    activity.setCalories(0);
                }
            }

            // 设置平均心率
            String avgHeartRateStr = record.get("Avg Heart Rate");
            if (avgHeartRateStr != null && !avgHeartRateStr.trim().isEmpty()) {
                try {
                    activity.setAvgHeartRate(Integer.parseInt(avgHeartRateStr));
                } catch (NumberFormatException e) {
                    // 可选字段，忽略错误
                }
            }

            // 设置最大心率
            String maxHeartRateStr = record.get("Max Heart Rate");
            if (maxHeartRateStr != null && !maxHeartRateStr.trim().isEmpty()) {
                try {
                    activity.setMaxHeartRate(Integer.parseInt(maxHeartRateStr));
                } catch (NumberFormatException e) {
                    // 可选字段，忽略错误
                }
            }

            return activity;

        } catch (Exception e) {
            logger.log(Level.WARNING, "解析记录失败", e);
            throw new IllegalArgumentException("记录解析失败: " + e.getMessage());
        }
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    @Override
    public Optional<Activity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }

    @Override
    public Activity saveActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    @Override
    public List<Activity> findActivitiesByFilters(Map<String, Object> filters) {
        // 实现简单的过滤逻辑
        List<Activity> allActivities = activityRepository.findAll();
        List<Activity> filteredActivities = new ArrayList<>();

        for (Activity activity : allActivities) {
            boolean match = true;

            if (filters.containsKey("activityType") && !activity.getActivityType().equals(filters.get("activityType"))) {
                match = false;
            }

            if (filters.containsKey("startDate") && activity.getDate().isBefore((LocalDate) filters.get("startDate"))) {
                match = false;
            }

            if (filters.containsKey("endDate") && activity.getDate().isAfter((LocalDate) filters.get("endDate"))) {
                match = false;
            }

            if (match) {
                filteredActivities.add(activity);
            }
        }

        return filteredActivities;
    }

    @Override
    public long getActivityCount() {
        return activityRepository.count();
    }
}