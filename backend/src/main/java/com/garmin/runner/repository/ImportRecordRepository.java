package com.garmin.runner.repository;

import com.garmin.runner.model.ImportRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ImportRecordRepository extends JpaRepository<ImportRecord, Long> {

    // 查询最近的导入记录
    List<ImportRecord> findTop10ByOrderByImportTimeDesc();

    // 根据导入时间范围查询记录
    List<ImportRecord> findByImportTimeBetween(LocalDateTime start, LocalDateTime end);

    // 根据状态查询导入记录
    List<ImportRecord> findByStatus(String status);
}
