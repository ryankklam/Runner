package com.garmin.runner.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Component
public class CsvParserUtil {

    /**
     * 解析佳明活动摘要CSV文件
     */
    public List<Map<String, String>> parseActivitySummary(InputStream inputStream) throws IOException {
        List<Map<String, String>> activities = new ArrayList<>();
        
        try (CSVParser parser = new CSVParser(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8),
                CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setIgnoreHeaderCase(true)
                        .setTrim(true)
                        .build())) {
            
            for (CSVRecord record : parser) {
                Map<String, String> activityMap = new HashMap<>();
                
                // 将CSV记录转换为Map
                for (String header : parser.getHeaderMap().keySet()) {
                    activityMap.put(header, record.get(header));
                }
                
                activities.add(activityMap);
            }
        }
        
        return activities;
    }

    /**
     * 解析佳明活动详细数据CSV文件
     */
    public List<Map<String, String>> parseActivityDetails(InputStream inputStream) throws IOException {
        List<Map<String, String>> details = new ArrayList<>();
        
        try (CSVParser parser = new CSVParser(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8),
                CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setIgnoreHeaderCase(true)
                        .setTrim(true)
                        .build())) {
            
            for (CSVRecord record : parser) {
                Map<String, String> detailMap = new HashMap<>();
                
                // 将CSV记录转换为Map
                for (String header : parser.getHeaderMap().keySet()) {
                    detailMap.put(header, record.get(header));
                }
                
                details.add(detailMap);
            }
        }
        
        return details;
    }

    /**
     * 解析时间字符串为LocalDateTime
     */
    public LocalDateTime parseDateTime(String dateTimeStr) {
        // 处理不同格式的时间字符串
        // 这里是一个简化版本，实际应用中可能需要更复杂的解析逻辑
        try {
            // 假设格式为：yyyy-MM-dd HH:mm:ss
            return LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            // 如果解析失败，尝试转换时间戳
            try {
                long timestamp = Long.parseLong(dateTimeStr);
                return LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.UTC);
            } catch (Exception ex) {
                // 如果都失败，返回当前时间
                return LocalDateTime.now();
            }
        }
    }

    /**
     * 安全地解析Double值
     */
    public Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 安全地解析Integer值
     */
    public Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 安全地解析Long值
     */
    public Long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
