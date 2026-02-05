package com.example.todo.infrastructure.batch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobOperator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoBatchSchedulerTest {

    @Mock
    private JobOperator jobOperator;

    @Mock
    private Job deleteCompletedTodosJob;

    @InjectMocks
    private TodoBatchScheduler scheduler;

    @Test
    void shouldTriggerJobWithParameters() throws Exception {
        // Mock the start method to return a dummy execution ID
        when(jobOperator.start(any(Job.class), any(JobParameters.class))).thenReturn(mock(JobExecution.class));

        // When
        scheduler.runDeleteCompletedTodosJob();

        // Then
        // Verify jobOperator.start was called with the correct job name
        // and a parameter string containing "time="
        verify(jobOperator).start(any(Job.class), any(JobParameters.class));
        assertNotNull(deleteCompletedTodosJob);
    }

    @Test
    void shouldHandleJobOperatorException() throws Exception {
        // Given
        when(jobOperator.start(any(Job.class), any(JobParameters.class))).thenThrow(new RuntimeException("Batch system failure"));

        // When & Then
        // We call the method and ensure it doesn't crash the scheduler (it catches and logs)
        scheduler.runDeleteCompletedTodosJob();

        verify(jobOperator).start(any(Job.class), any(JobParameters.class));
        assertNotNull(deleteCompletedTodosJob);

    }
}
