package com.example.scheduler.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class BatchScheduler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    // 10초마다 실행
    @Scheduled(cron = "0/10 * * * * *", zone = "Asia/Seoul")
    public void batchScheduler() {
        logger.info("this is test schedule {}", LocalDateTime.now());

    }
}
