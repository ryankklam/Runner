package com.garmin.runner.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StatisticsService {

    /**
     * 获取总体统计数据
     * @return 包含总距离、总活动次数、总卡路里等信息的Map
     */
    Map<String, Object> getOverallStatistics();

    /**
     * 获取指定时间范围的统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 时间范围内的统计数据
     */
    Map<String, Object> getStatisticsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 获取按活动类型分组的统计数据
     * @return 各活动类型的统计信息
     */
    List<Map<String, Object>> getStatisticsByActivityType();

    /**
     * 获取最近的活动列表
     * @param limit 限制返回数量
     * @return 最近的活动列表
     */
    List<Map<String, Object>> getRecentActivities(int limit);

    /**
     * 获取活动趋势数据（按月份）
     * @param months 统计的月数
     * @return 按月统计的活动趋势数据
     */
    List<Map<String, Object>> getActivityTrendByMonth(int months);

    /**
     * 获取心率区间分布统计
     * @return 心率区间分布数据
     */
    Map<String, Object> getHeartRateZoneStatistics();

    /**
     * 获取配速区间分布统计
     * @return 配速区间分布数据
     */
    Map<String, Object> getPaceZoneStatistics();
}
