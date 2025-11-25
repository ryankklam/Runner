package com.garmin.runner.controller;

import com.garmin.runner.model.Activity;
import com.garmin.runner.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 运动活动控制器，处理文件上传和数据查询请求
 */
@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    /**
     * 上传CSV文件并导入运动数据
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadCSV(@RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")) {
                return ResponseEntity.badRequest().body("请上传有效的CSV文件");
            }
            
            // 处理文件上传
            Map<String, Object> result = activityService.importActivitiesFromCSV(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("文件导入失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有活动数据
     */
    @GetMapping
    public ResponseEntity<List<Activity>> getAllActivities() {
        try {
            List<Activity> activities = activityService.getAllActivities();
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据ID获取单个活动详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        return activityService.getActivityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 删除活动
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable Long id) {
        try {
            activityService.deleteActivity(id);
            return ResponseEntity.ok("活动删除成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}