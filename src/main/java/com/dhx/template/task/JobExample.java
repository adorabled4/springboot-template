package com.dhx.template.task;

import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author <a href="https://blog.dhx.icu/"> adorabled4 </a>
 * @className JobExample spring Scheduled定时任务
 * @date : 2023/05/04/ 17:47
 **/
public class JobExample {

    @Scheduled(cron = "0 0 0 * * ?") // 设置每天零点执行
    private void refreshTask(){

    }
}
