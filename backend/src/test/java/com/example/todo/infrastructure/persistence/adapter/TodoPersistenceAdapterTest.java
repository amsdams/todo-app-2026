package com.example.todo.infrastructure.persistence.adapter;

import com.example.todo.domain.model.Todo;
import com.example.todo.infrastructure.persistence.entity.TodoEntity;
import com.example.todo.infrastructure.persistence.mapper.TodoMapper;
import com.example.todo.infrastructure.persistence.repository.JpaTodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoPersistenceAdapterTest {

    @Mock
    private JpaTodoRepository jpaTodoRepository;

    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoPersistenceAdapter todoPersistenceAdapter;

    @Captor
    private ArgumentCaptor<Iterable<TodoEntity>> entityIterableCaptor;

    @Test
    void shouldSaveTodo() {
        // Given
        Todo todoToSave = mock(Todo.class);
        TodoEntity entityToSave = mock(TodoEntity.class);
        TodoEntity savedEntity = mock(TodoEntity.class);
        Todo expectedTodo = mock(Todo.class);
        when(expectedTodo.getId()).thenReturn(1L);
        when(expectedTodo.getTitle()).thenReturn("New Todo");


        when(todoMapper.toEntity(todoToSave)).thenReturn(entityToSave);
        when(jpaTodoRepository.save(entityToSave)).thenReturn(savedEntity);
        when(todoMapper.toDomain(savedEntity)).thenReturn(expectedTodo);

        // When
        Todo result = todoPersistenceAdapter.save(todoToSave);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("New Todo");

        verify(todoMapper, times(1)).toEntity(todoToSave);
        verify(jpaTodoRepository, times(1)).save(entityToSave);
        verify(todoMapper, times(1)).toDomain(savedEntity);
    }

    @Test
    void shouldUpdateExistingTodo() {
        // Given
        Todo todoToUpdate = mock(Todo.class);
        when(todoToUpdate.getId()).thenReturn(1L);
        when(todoToUpdate.getTitle()).thenReturn("Updated Todo");
        when(todoToUpdate.isCompleted()).thenReturn(true);

        TodoEntity entityToUpdate = mock(TodoEntity.class);
        TodoEntity updatedEntity = mock(TodoEntity.class);

        when(todoMapper.toEntity(todoToUpdate)).thenReturn(entityToUpdate);
        when(jpaTodoRepository.save(entityToUpdate)).thenReturn(updatedEntity);
        when(todoMapper.toDomain(updatedEntity)).thenReturn(todoToUpdate);

        // When
        Todo result = todoPersistenceAdapter.save(todoToUpdate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Updated Todo");
        assertThat(result.isCompleted()).isTrue();

        verify(todoMapper, times(1)).toEntity(todoToUpdate);
        verify(jpaTodoRepository, times(1)).save(entityToUpdate);
        verify(todoMapper, times(1)).toDomain(updatedEntity);
    }

    @Test
    void shouldFindTodoById() {
        // Given
        Long id = 1L;
        TodoEntity testEntity = mock(TodoEntity.class);

        Todo testTodo = mock(Todo.class);
        when(testTodo.getId()).thenReturn(1L);
        when(testTodo.getTitle()).thenReturn("Test Todo");
        when(jpaTodoRepository.findById(id)).thenReturn(Optional.of(testEntity));
        when(todoMapper.toDomain(testEntity)).thenReturn(testTodo);


        // When
        Optional<Todo> result = todoPersistenceAdapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getTitle()).isEqualTo("Test Todo");

        verify(jpaTodoRepository, times(1)).findById(id);
        verify(todoMapper, times(1)).toDomain(testEntity);
    }

    @Test
    void shouldReturnEmptyWhenTodoNotFound() {
        // Given
        Long id = 999L;
        when(jpaTodoRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Todo> result = todoPersistenceAdapter.findById(id);

        // Then
        assertThat(result).isEmpty();

        verify(jpaTodoRepository, times(1)).findById(id);
        verify(todoMapper, never()).toDomain(any());
    }

    @Test
    void shouldFindAllTodos() {
        // Given
        TodoEntity entity1 = mock(TodoEntity.class);
        TodoEntity entity2 = mock(TodoEntity.class);
        TodoEntity entity3 = mock(TodoEntity.class);

        Todo todo1 = mock(Todo.class);
        when(todo1.getId()).thenReturn(1L);
        when(todo1.getTitle()).thenReturn("Todo 1");

        Todo todo2 = mock(Todo.class);
        when(todo2.getId()).thenReturn(2L);
        when(todo2.getTitle()).thenReturn("Todo 2");

        Todo todo3 = mock(Todo.class);
        when(todo3.getId()).thenReturn(3L);
        when(todo3.getTitle()).thenReturn("Todo 3");

        List<TodoEntity> entities = Arrays.asList(entity1, entity2, entity3);

        when(jpaTodoRepository.findAll()).thenReturn(entities);
        when(todoMapper.toDomain(entity1)).thenReturn(todo1);
        when(todoMapper.toDomain(entity2)).thenReturn(todo2);
        when(todoMapper.toDomain(entity3)).thenReturn(todo3);

        // When
        List<Todo> result = todoPersistenceAdapter.findAll();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(Todo::getId).containsExactly(1L, 2L, 3L);
        assertThat(result).extracting(Todo::getTitle).containsExactly("Todo 1", "Todo 2", "Todo 3");

        verify(jpaTodoRepository, times(1)).findAll();
        verify(todoMapper, times(3)).toDomain(any(TodoEntity.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoTodosExist() {
        // Given
        when(jpaTodoRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Todo> result = todoPersistenceAdapter.findAll();

        // Then
        assertThat(result).isEmpty();

        verify(jpaTodoRepository, times(1)).findAll();
        verify(todoMapper, never()).toDomain(any(TodoEntity.class));
    }

    @Test
    void shouldDeleteTodoById() {
        // Given
        Long id = 1L;
        doNothing().when(jpaTodoRepository).deleteById(id);

        // When
        todoPersistenceAdapter.deleteById(id);

        // Then
        verify(jpaTodoRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldCheckIfTodoExistsById() {
        // Given
        Long id = 1L;
        when(jpaTodoRepository.existsById(id)).thenReturn(true);

        // When
        boolean result = todoPersistenceAdapter.existsById(id);

        // Then
        assertThat(result).isTrue();
        verify(jpaTodoRepository, times(1)).existsById(id);
    }

    @Test
    void shouldReturnFalseWhenTodoDoesNotExist() {
        // Given
        Long id = 999L;
        when(jpaTodoRepository.existsById(id)).thenReturn(false);

        // When
        boolean result = todoPersistenceAdapter.existsById(id);

        // Then
        assertThat(result).isFalse();
        verify(jpaTodoRepository, times(1)).existsById(id);
    }

    @Test
    void shouldFindCompletedTodos() {
        // Given
        TodoEntity completedEntity1 = mock(TodoEntity.class);
        TodoEntity completedEntity2 = mock(TodoEntity.class);

        Todo completedTodo1 = mock(Todo.class);
        when(completedTodo1.isCompleted()).thenReturn(true);
        when(completedTodo1.getTitle()).thenReturn("Completed 1");

        Todo completedTodo2 = mock(Todo.class);
        when(completedTodo2.isCompleted()).thenReturn(true);
        when(completedTodo2.getTitle()).thenReturn("Completed 2");


        List<TodoEntity> completedEntities = Arrays.asList(completedEntity1, completedEntity2);

        when(jpaTodoRepository.findCompletedTodos()).thenReturn(completedEntities);
        when(todoMapper.toDomain(completedEntity1)).thenReturn(completedTodo1);
        when(todoMapper.toDomain(completedEntity2)).thenReturn(completedTodo2);

        // When
        List<Todo> result = todoPersistenceAdapter.findCompletedTodos();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(Todo::isCompleted);
        assertThat(result).extracting(Todo::getTitle).containsExactly("Completed 1", "Completed 2");

        verify(jpaTodoRepository, times(1)).findCompletedTodos();
        verify(todoMapper, times(2)).toDomain(any(TodoEntity.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoCompletedTodosExist() {
        // Given
        when(jpaTodoRepository.findCompletedTodos()).thenReturn(Collections.emptyList());

        // When
        List<Todo> result = todoPersistenceAdapter.findCompletedTodos();

        // Then
        assertThat(result).isEmpty();

        verify(jpaTodoRepository, times(1)).findCompletedTodos();
        verify(todoMapper, never()).toDomain(any(TodoEntity.class));
    }

    @Test
    void shouldDeleteAllTodos() {
        // Given
        Todo todo1 = mock(Todo.class);
        Todo todo2 = mock(Todo.class);
        Todo todo3 = mock(Todo.class);

        TodoEntity entity1 = mock(TodoEntity.class);
        TodoEntity entity2 = mock(TodoEntity.class);
        TodoEntity entity3 = mock(TodoEntity.class);

        List<Todo> todosToDelete = Arrays.asList(todo1, todo2, todo3);

        when(todoMapper.toEntity(todo1)).thenReturn(entity1);
        when(todoMapper.toEntity(todo2)).thenReturn(entity2);
        when(todoMapper.toEntity(todo3)).thenReturn(entity3);
        doNothing().when(jpaTodoRepository).deleteAll(any());

        // When
        todoPersistenceAdapter.deleteAll(todosToDelete);

        // Then
        verify(todoMapper, times(3)).toEntity(any(Todo.class));
        verify(jpaTodoRepository, times(1)).deleteAll(entityIterableCaptor.capture());

        List<TodoEntity> capturedEntities = new java.util.ArrayList<>();
        entityIterableCaptor.getValue().forEach(capturedEntities::add);

        assertThat(capturedEntities).hasSize(3);
        assertThat(capturedEntities).contains(entity1, entity2, entity3);
    }

    @Test
    void shouldDeleteEmptyListOfTodos() {
        // Given
        List<Todo> emptyList = Collections.emptyList();
        doNothing().when(jpaTodoRepository).deleteAll(any());

        // When
        todoPersistenceAdapter.deleteAll(emptyList);

        // Then
        verify(todoMapper, never()).toEntity(any(Todo.class));
        verify(jpaTodoRepository, times(1)).deleteAll(entityIterableCaptor.capture());

        List<TodoEntity> capturedEntities = new java.util.ArrayList<>();
        entityIterableCaptor.getValue().forEach(capturedEntities::add);

        assertThat(capturedEntities).isEmpty();
    }

    @Test
    void shouldDeleteSingleTodo() {
        // Given
        Todo todo = mock(Todo.class);
        TodoEntity entity = mock(TodoEntity.class);

        when(todoMapper.toEntity(todo)).thenReturn(entity);
        doNothing().when(jpaTodoRepository).deleteAll(any());

        // When
        todoPersistenceAdapter.deleteAll(List.of(todo));

        // Then
        verify(todoMapper, times(1)).toEntity(todo);
        verify(jpaTodoRepository, times(1)).deleteAll(entityIterableCaptor.capture());

        List<TodoEntity> capturedEntities = new java.util.ArrayList<>();
        entityIterableCaptor.getValue().forEach(capturedEntities::add);

        assertThat(capturedEntities).hasSize(1);
        assertThat(capturedEntities).contains(entity);
    }

    @Test
    void shouldHandleMapperConversionCorrectly() {
        // Given
        Todo domainTodo = mock(Todo.class);
        TodoEntity entity = mock(TodoEntity.class);
        TodoEntity savedEntity = mock(TodoEntity.class);
        Todo savedTodo = mock(Todo.class);
        when(savedTodo.getId()).thenReturn(10L);

        when(todoMapper.toEntity(domainTodo)).thenReturn(entity);
        when(jpaTodoRepository.save(entity)).thenReturn(savedEntity);
        when(todoMapper.toDomain(savedEntity)).thenReturn(savedTodo);

        // When
        Todo result = todoPersistenceAdapter.save(domainTodo);

        // Then
        assertThat(result.getId()).isEqualTo(10L);

        var inOrder = inOrder(todoMapper, jpaTodoRepository);
        inOrder.verify(todoMapper).toEntity(domainTodo);
        inOrder.verify(jpaTodoRepository).save(entity);
        inOrder.verify(todoMapper).toDomain(savedEntity);
    }

    @Test
    void shouldVerifyMethodCallOrderForFindAll() {
        // Given

        TodoEntity testEntity = mock(TodoEntity.class);

        Todo testTodo = mock(Todo.class);

        List<TodoEntity> entities = List.of(testEntity);
        when(jpaTodoRepository.findAll()).thenReturn(entities);
        when(todoMapper.toDomain(testEntity)).thenReturn(testTodo);

        // When
        todoPersistenceAdapter.findAll();

        // Then
        var inOrder = inOrder(jpaTodoRepository, todoMapper);
        inOrder.verify(jpaTodoRepository).findAll();
        inOrder.verify(todoMapper).toDomain(testEntity);
    }

}