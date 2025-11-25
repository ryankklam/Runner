package com.garmin.runner.service.impl;

import com.garmin.runner.model.Activity;
import com.garmin.runner.model.ActivityDetail;
import com.garmin.runner.model.ImportRecord;
import com.garmin.runner.repository.ActivityRepository;
import com.garmin.runner.repository.ImportRecordRepository;
import com.garmin.runner.service.ImportService;
import com.garmin.runner.util.CsvParserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ImportServiceImpl implements ImportService {

    @Autowired
    private CsvParserUtil csvParserUtil;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ImportRecordRepository importRecordRepository;

    @Override
    public Map<String, Object> importGarminData(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        ImportRecord importRecord = new ImportRecord();
        
        try {
            // 初始化导入记录
            importRecord.setFileName(file.getOriginalFilename());
            importRecord.setFileSize(file.getSize());
            importRecord.setImportTime(LocalDateTime.now());
            importRecord.setStatus("成功");
            
            // 解析CSV文件
            List<Map<String, String>> csvData = csvParserUtil.parseActivitySummary(file.getInputStream());
            
            // 处理活动数据
            List<Activity> activities = processActivities(csvData, importRecord);
            
            // 保存导入记录和活动数据
            importRecord.setActivityCount(activities.size());
            importRecordRepository.save(importRecord);
            
            // 设置响应结果
            result.put("success", true);
            result.put("message", "数据导入成功");
            result.put("activityCount", activities.size());
            result.put("importRecordId", importRecord.getId());
            
        } catch (Exception e) {
            // 记录导入失败信息
            importRecord.setStatus("失败");
            importRecord.setErrorMessage(e.getMessage());
            importRecordRepository.save(importRecord);
            
            result.put("success", false);
            result.put("message", "数据导入失败: " + e.getMessage());
            result.put("importRecordId", importRecord.getId());
        }
        
        return result;
    }

    @Override
    public boolean validateGarminFile(MultipartFile file) {
        // 验证文件类型
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            return false;
        }
        
        // 简单验证文件内容（实际应用中可能需要更复杂的验证）
        try {
            // 读取第一行数据进行简单验证
            List<Map<String, String>> firstFewRecords = csvParserUtil.parseActivitySummary(file.getInputStream());
            if (firstFewRecords.isEmpty()) {
                return false;
            }
            
            // 检查是否包含佳明数据的关键字段
            Map<String, String> firstRecord = firstFewRecords.get(0);
            return firstRecord.containsKey("Activity Type") || 
                   firstRecord.containsKey("Date") || 
                   firstRecord.containsKey("Distance") ||
                   firstRecord.containsKey("活动类型") ||
                   firstRecord.containsKey("日期") ||
                   firstRecord.containsKey("距离");
                    
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Map<String, Object> getImportRecords() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ImportRecord> records = importRecordRepository.findTop10ByOrderByImportTimeDesc();
            result.put("success", true);
            result.put("data", records);
            result.put("count", records.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取导入记录失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 处理活动数据并转换为实体对象
     */
    private List<Activity> processActivities(List<Map<String, String>> csvData, ImportRecord importRecord) {
        List<Activity> activities = new ArrayList<>();
        
        for (Map<String, String> record : csvData) {
            Activity activity = new Activity();
            
            // 设置活动基本信息
            activity.setActivityName(getValue(record, "Activity Name", "活动名称"));
            activity.setActivityType(getValue(record, "Activity Type", "活动类型"));
            
            // 解析日期时间
            String dateStr = getValue(record, "Date", "日期");
            if (dateStr != null) {
                activity.setStartTime(csvParserUtil.parseDateTime(dateStr));
            }
            
            // 设置距离（转换为米）
            Double distance = csvParserUtil.parseDouble(getValue(record, "Distance", "距离"));
            if (distance != null) {
                // 假设距离单位为公里，转换为米
                activity.setDistance(distance * 1000);
            }
            
            // 设置持续时间（秒）
            String durationStr = getValue(record, "Duration", "持续时间");
            if (durationStr != null) {
                // 简化处理，实际应用中需要根据格式解析
                activity.setDuration(csvParserUtil.parseLong(durationStr));
            }
            
            // 设置卡路里
            activity.setCalories(csvParserUtil.parseInteger(getValue(record, "Calories", "卡路里")));
            
            // 设置心率信息
            activity.setAverageHeartRate(csvParserUtil.parseInteger(getValue(record, "Avg HR", "平均心率")));
            activity.setMaxHeartRate(csvParserUtil.parseInteger(getValue(record, "Max HR", "最大心率")));
            
            // 设置配速
            Double pace = csvParserUtil.parseDouble(getValue(record, "Avg Pace", "平均配速"));
            if (pace != null) {
                activity.setAveragePace(pace);
            }
            
            // 设置导入信息
            activity.setImportDate(LocalDateTime.now());
            activity.setImportRecord(importRecord);
            
            // 暂时不处理活动详情数据
            // activity.setActivityDetails(Collections.emptyList());
            
            activities.add(activity);
        }
        
        // 保存活动数据
        return activityRepository.saveAll(activities);
    }

    /**
     * 从Map中获取值，支持多个可能的键名
     */
    private String getValue(Map<String, String> record, String... keys) {
        for (String key : keys) {
            if (record.containsKey(key)) {
                return record.get(key);
            }
        }
        return null;
    }
}
