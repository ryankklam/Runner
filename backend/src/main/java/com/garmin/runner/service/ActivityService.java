package com.garmin.runner.service;

import com.garmin.runner.model.Activity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 运动活动服务接口
 */
public interface ActivityService {

    /**
     * 从CSV文件导入运动数据
     * @param file CSV文件
     * @return 导入结果，包含成功导入数量、失败信息等
     */
    Map<String, Object> importActivitiesFromCSV(MultipartFile file);

    /**
     * 获取所有运动活动
     * @return 运动活动列表
     */
    List<Activity> getAllActivities();

    /**
     * 根据ID获取运动活动
     * @param id 活动ID
     * @return 运动活动对象，如果不存在则返回Optional.empty()
     */
    Optional<Activity> getActivityById(Long id);

    /**
     * 保存运动活动
     * @param activity 运动活动对象
     * @return 保存后的运动活动对象
     */
    Activity saveActivity(Activity activity);

    /**
     * 删除运动活动
     * @param id 活动ID
     */
    void deleteActivity(Long id);

    /**
     * 根据条件查询运动活动
     * @param filters 查询条件
     * @return 符合条件的运动活动列表
     */
    List<Activity> findActivitiesByFilters(Map<String, Object> filters);

    /**
     * 获取运动活动总数
     * @return 运动活动总数
     */
    long getActivityCount();
}