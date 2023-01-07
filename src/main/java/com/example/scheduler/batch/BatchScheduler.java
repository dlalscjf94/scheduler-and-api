package com.example.scheduler.batch;

import com.example.scheduler.dao.FileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class BatchScheduler {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private CsvJob csvJob;
    @Autowired
    private TxtJob txtJob;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    // 10초마다 실행
    @Scheduled(cron = "0/10 * * * * *", zone = "Asia/Seoul")
    public void batchScheduler() {

        // jobparam 설정
        Map<String, JobParameter> confMap = new HashMap<>();
        // TimeMillis 를 param으로 주고 job이 00시마다 반복될수 있도록
        confMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(confMap);

        // 특정 경로 파일 확인
        File file = new File("/file2.txt");
        // 파일명
        String fileName = file.getName();
        // 파일 확장자 추출 , 마지막 . 문자 확인해서 추출
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            String extension = fileName.substring(index + 1 );

            if (extension.equals("csv")) {
                logger.info("csv파일입니다.");
                // csvJob 실행
                try {
                    // TODO : fileinfo data insert
                    // TODO : filedata data insert
                    jobLauncher.run(csvJob.csvJob_batchBuild(), jobParameters);
                } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                         JobParametersInvalidException | JobRestartException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            } else if (extension.equals("txt")) {

                logger.info("txt파일입니다.");
                // txtJob 실행
                try {
                    // TODO : fileinfo data insert
                    // TODO : filedata data insert
                    jobLauncher.run(txtJob.textJob_batchBuild(), jobParameters);
                } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                         JobParametersInvalidException | JobRestartException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            } else {
                logger.error("새로운 형식의 파일입니다. 확장자명을 확인해주세요" + extension);
            }
        }

    }
}
