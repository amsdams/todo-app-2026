package com.example.todo.infrastructure.batch;

import com.example.todo.domain.model.Todo;
import com.example.todo.domain.port.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DeleteCompletedTodosBatchConfig {

    private final TodoRepository todoRepository;

    @Bean
    public Job deleteCompletedTodosJob(JobRepository jobRepository, Step deleteCompletedTodosStep) {
        return new JobBuilder("deleteCompletedTodosJob", jobRepository)
                .start(deleteCompletedTodosStep)
                .build();
    }

    @Bean
    public Step deleteCompletedTodosStep(JobRepository jobRepository, 
                                          PlatformTransactionManager transactionManager,
                                          Tasklet deleteCompletedTodosTasklet) {
        return new StepBuilder("deleteCompletedTodosStep", jobRepository)
                .tasklet(deleteCompletedTodosTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet deleteCompletedTodosTasklet() {
        return (contribution, chunkContext) -> {
            log.info("Starting batch job to delete completed todos");
            
            List<Todo> completedTodos = todoRepository.findCompletedTodos();
            
            if (completedTodos.isEmpty()) {
                log.info("No completed todos found to delete");
            } else {
                log.info("Found {} completed todos to delete", completedTodos.size());
                todoRepository.deleteAll(completedTodos);
                log.info("Successfully deleted {} completed todos", completedTodos.size());
            }
            
            return RepeatStatus.FINISHED;
        };
    }
}
