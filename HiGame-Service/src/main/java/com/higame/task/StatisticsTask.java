package com.higame.task;

import com.higame.entity.UserType;
import com.higame.service.UserStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticsTask {
    private final UserStatisticsService userStatisticsService;

    @Scheduled(cron = "0 0 * * * *") // 每小时执行一次
    public void updateHourlyStatistics() {
        log.info("开始更新用户统计数据...");
        try {
            userStatisticsService.updateStatistics(UserType.SDK);
            userStatisticsService.updateStatistics(UserType.APP);
            log.info("用户统计数据更新完成");
        } catch (Exception e) {
            log.error("更新用户统计数据失败", e);
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // 每天凌晨执行一次
    public void resetDailyStatistics() {
        log.info("开始重置每日统计数据...");
        try {
            userStatisticsService.updateStatistics(UserType.SDK);
            userStatisticsService.updateStatistics(UserType.APP);
            log.info("每日统计数据重置完成");
        } catch (Exception e) {
            log.error("重置每日统计数据失败", e);
        }
    }
}
