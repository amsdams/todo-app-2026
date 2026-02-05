package com.example.todo.infrastructure.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class TodoBatchScheduler {

    private final JobOperator jobLauncher;
    private final Job deleteCompletedTodosJob;

    // Run every day at 2 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void runDeleteCompletedTodosJob() {
        try {
            log.info("Triggering scheduled job to delete completed todos");
            
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.start(deleteCompletedTodosJob, jobParameters);
            
            log.info("Completed scheduled job to delete completed todos");
        } catch (Exception e) {
            log.error("Error running delete completed todos job", e);
        }
    }
}
