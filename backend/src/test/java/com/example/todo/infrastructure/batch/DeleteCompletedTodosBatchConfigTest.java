package com.example.todo.infrastructure.batch;

import com.example.todo.domain.model.Todo;
import com.example.todo.domain.port.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCompletedTodosBatchConfigTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private StepContribution stepContribution;

    @Mock
    private ChunkContext chunkContext;

    private DeleteCompletedTodosBatchConfig config;

    @BeforeEach
    void setUp() {
        config = new DeleteCompletedTodosBatchConfig(todoRepository);
    }

    @Test
    void shouldCreateDeleteCompletedTodosJob() {
        // Given
        Step mockStep = mock(Step.class);

        // When
        Job job = config.deleteCompletedTodosJob(jobRepository, mockStep);

        // Then
        assertThat(job).isNotNull();
        assertThat(job.getName()).isEqualTo("deleteCompletedTodosJob");
    }

    @Test
    void shouldCreateDeleteCompletedTodosStep() {
        // Given
        Tasklet mockTasklet = mock(Tasklet.class);

        // When
        Step step = config.deleteCompletedTodosStep(jobRepository, transactionManager, mockTasklet);

        // Then
        assertThat(step).isNotNull();
        assertThat(step.getName()).isEqualTo("deleteCompletedTodosStep");
    }

    @Test
    void shouldCreateDeleteCompletedTodosTasklet() {
        // When
        Tasklet tasklet = config.deleteCompletedTodosTasklet();

        // Then
        assertThat(tasklet).isNotNull();
    }

    @Test
    void taskletShouldDeleteCompletedTodos() throws Exception {
        // Given
        Tasklet tasklet = config.deleteCompletedTodosTasklet();

        Todo todo1 = mock(Todo.class);
        Todo todo2 = mock(Todo.class);
        Todo todo3 = mock(Todo.class);

        List<Todo> completedTodos = List.of(todo1, todo2, todo3);

        when(todoRepository.findCompletedTodos()).thenReturn(completedTodos);

        // When
        RepeatStatus status = tasklet.execute(stepContribution, chunkContext);

        // Then
        assertThat(status).isEqualTo(RepeatStatus.FINISHED);
        verify(todoRepository, times(1)).findCompletedTodos();
        verify(todoRepository, times(1)).deleteAll(completedTodos);
    }

    @Test
    void taskletShouldHandleEmptyCompletedTodosList() throws Exception {
        // Given
        Tasklet tasklet = config.deleteCompletedTodosTasklet();

        when(todoRepository.findCompletedTodos()).thenReturn(Collections.emptyList());

        // When
        RepeatStatus status = tasklet.execute(stepContribution, chunkContext);

        // Then
        assertThat(status).isEqualTo(RepeatStatus.FINISHED);
        verify(todoRepository, times(1)).findCompletedTodos();
        verify(todoRepository, never()).deleteAll(anyList());
    }

    @Test
    void taskletShouldDeleteSingleCompletedTodo() throws Exception {
        // Given
        Tasklet tasklet = config.deleteCompletedTodosTasklet();

        Todo todo = mock(Todo.class);
        List<Todo> completedTodos = List.of(todo);

        when(todoRepository.findCompletedTodos()).thenReturn(completedTodos);

        // When
        RepeatStatus status = tasklet.execute(stepContribution, chunkContext);

        // Then
        assertThat(status).isEqualTo(RepeatStatus.FINISHED);
        verify(todoRepository, times(1)).findCompletedTodos();
        verify(todoRepository, times(1)).deleteAll(completedTodos);
    }

    @Test
    void taskletShouldDeleteMultipleCompletedTodos() throws Exception {
        // Given
        Tasklet tasklet = config.deleteCompletedTodosTasklet();

        List<Todo> completedTodos = List.of(mock(Todo.class),
                mock(Todo.class),
                mock(Todo.class),
                mock(Todo.class),
                mock(Todo.class));

        when(todoRepository.findCompletedTodos()).thenReturn(completedTodos);

        // When
        RepeatStatus status = tasklet.execute(stepContribution, chunkContext);

        // Then
        assertThat(status).isEqualTo(RepeatStatus.FINISHED);
        verify(todoRepository, times(1)).findCompletedTodos();
        verify(todoRepository, times(1)).deleteAll(argThat(list -> list.size() == 5));
    }

    @Test
    void taskletShouldAlwaysReturnFinishedStatus() throws Exception {
        // Given
        Tasklet tasklet = config.deleteCompletedTodosTasklet();

        when(todoRepository.findCompletedTodos()).thenReturn(Collections.emptyList());

        // When
        RepeatStatus status1 = tasklet.execute(stepContribution, chunkContext);

        RepeatStatus status2 = tasklet.execute(stepContribution, chunkContext);

        // Then
        assertThat(status1).isEqualTo(RepeatStatus.FINISHED);
        assertThat(status2).isEqualTo(RepeatStatus.FINISHED);
    }

    @Test
    void taskletShouldCallRepositoryMethodsInCorrectOrder() throws Exception {
        // Given
        Tasklet tasklet = config.deleteCompletedTodosTasklet();
        List<Todo> completedTodos = List.of(mock(Todo.class));

        when(todoRepository.findCompletedTodos()).thenReturn(completedTodos);

        // When
        tasklet.execute(stepContribution, chunkContext);

        // Then
        var inOrder = inOrder(todoRepository);
        inOrder.verify(todoRepository).findCompletedTodos();
        inOrder.verify(todoRepository).deleteAll(completedTodos);
        inOrder.verifyNoMoreInteractions();
    }

    // Helper method to create Todo objects
    /*private Todo createTodo(Long id, String title, boolean completed) {
        Todo todo = mock(Todo.class);
        when(todo.getId()).thenReturn(id);
        when(todo.getTitle()).thenReturn(title);
        when(todo.isCompleted()).thenReturn(completed);
        return todo;
    }*/
}
