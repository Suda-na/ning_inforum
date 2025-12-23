package com.niit.task;

import com.niit.service.UserBanHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component  // 让Spring扫描到这个类
public class AutoUnbanTask {

    @Autowired
    private UserBanHistoryService userBanHistoryService;

    /**
     * 每天0点执行：cron表达式“0 0 0 * * ?”表示每天0时0分0秒触发
     */
    @Scheduled(cron = "0 0 */1 * * ?")
    public void autoRecoverPermission() {
        userBanHistoryService.recoverExpiredBanPermission();
        // 可选：添加日志，方便排查执行情况
        System.out.println("每小时自动解封任务执行完成，当前时间：" + new java.util.Date());
    }
}