package com.garmin.runner.util;

import com.garmin.runner.model.Activity;
import com.garmin.runner.model.ImportRecord;
import com.garmin.runner.repository.ActivityRepository;
import com.garmin.runner.repository.ImportRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 用于检查数据是否正确保存的工具类
 */
@Component
public class DataChecker implements CommandLineRunner {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ImportRecordRepository importRecordRepository;

    @Override
    public void run(String... args) throws Exception {
        // 仅当运行参数包含"check-data"时执行检查
        if (args.length > 0 && "check-data".equals(args[0])) {
            System.out.println("开始检查数据库数据...");
            
            // 检查导入记录
            long importRecordCount = importRecordRepository.count();
            System.out.println("导入记录数量: " + importRecordCount);
            
            // 检查活动数据
            long activityCount = activityRepository.count();
            System.out.println("活动记录数量: " + activityCount);
            
            // 打印部分活动数据
            System.out.println("最新的5条活动记录:");
            activityRepository.findAll().stream()
                    .limit(5)
                    .forEach(activity -> {
                        System.out.println("- " + activity.getActivityName() + " (" + activity.getActivityType() + ")");
                    });
            
            System.out.println("数据检查完成。");
        }
    }
}
