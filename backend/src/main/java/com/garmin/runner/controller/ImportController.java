package com.garmin.runner.controller;

import com.garmin.runner.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    @Autowired
    private ImportService importService;

    /**
     * 上传并导入佳明数据文件
     */
    @PostMapping("/garmin-data")
    public ResponseEntity<Map<String, Object>> importGarminData(@RequestParam("file") MultipartFile file) {
        // 验证文件是否为空
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "请选择要上传的文件"));
        }

        // 验证文件格式
        if (!importService.validateGarminFile(file)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "无效的佳明数据文件，请上传CSV格式的活动数据"));
        }

        // 执行数据导入
        Map<String, Object> result = importService.importGarminData(file);
        
        if ((boolean) result.getOrDefault("success", false)) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 获取导入记录列表
     */
    @GetMapping("/records")
    public ResponseEntity<Map<String, Object>> getImportRecords() {
        Map<String, Object> result = importService.getImportRecords();
        
        if ((boolean) result.getOrDefault("success", false)) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "Garmin Data Import Service"));
    }
}
