package com.garmin.runner.init;

import com.garmin.runner.service.ImportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;


import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class FakeDataInitializer implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(FakeDataInitializer.class.getName());
    private final ImportService importService;

    public FakeDataInitializer(ImportService importService) {
        this.importService = importService;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // 尝试加载假数据CSV文件
            ClassPathResource resource = new ClassPathResource("fake_garmin_data.csv");
            
            if (!resource.exists()) {
                logger.warning("假数据文件不存在，跳过自动导入");
                return;
            }
            
            logger.info("开始导入假数据...");
            
            // 创建模拟的MultipartFile
            try (InputStream inputStream = resource.getInputStream()) {
                MultipartFile mockFile = new MockMultipartFile(
                        "fake_garmin_data.csv",
                        "fake_garmin_data.csv",
                        "text/csv",
                        inputStream.readAllBytes()
                );
                
                // 验证文件
                if (importService.validateGarminFile(mockFile)) {
                    // 导入数据
                    Map<String, Object> result = importService.importGarminData(mockFile);
                    
                    if ((boolean) result.getOrDefault("success", false)) {
                        logger.info("假数据导入成功，导入了 " + result.get("activityCount") + " 条活动记录");
                    } else {
                        logger.warning("假数据导入失败: " + result.getOrDefault("message", "未知错误"));
                    }
                } else {
                    logger.warning("假数据文件格式无效，无法导入");
                }
            }
            
        } catch (Exception e) {
            logger.warning("导入假数据时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
