package com.garmin.runner.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface ImportService {

    /**
     * 导入佳明活动数据CSV文件
     * @param file 上传的CSV文件
     * @return 导入结果信息
     */
    Map<String, Object> importGarminData(MultipartFile file);

    /**
     * 验证文件是否为有效的佳明数据文件
     * @param file 上传的文件
     * @return 是否有效
     */
    boolean validateGarminFile(MultipartFile file);

    /**
     * 获取导入记录列表
     * @return 导入记录信息列表
     */
    Map<String, Object> getImportRecords();
}
