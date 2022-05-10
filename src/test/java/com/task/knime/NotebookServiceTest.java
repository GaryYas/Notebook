package com.task.knime;


import com.task.knime.cache.CacheManager;
import com.task.knime.dto.NoteDto;
import com.task.knime.dto.NotebookDto;
import com.task.knime.exception.ResourceNotFoundException;
import com.task.knime.model.NoteEntity;
import com.task.knime.model.NotebookEntity;
import com.task.knime.model.TagEntity;
import com.task.knime.repository.NotebookRepository;
import com.task.knime.service.NoteBookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class NotebookServiceTest {

    @InjectMocks
    NoteBookService noteBookService;

    @Mock
    NotebookRepository notebookRepository;

    @Mock
    CacheManager cacheManager;

    @Test
    public void createNotebookTest() {
        String notebookName = "My notebook";
        when(notebookRepository.save(any(NotebookEntity.class))).thenReturn(new NotebookEntity(notebookName));
        NotebookDto notebookDto = noteBookService.createNotebook(notebookName);
        assertTrue(notebookDto.getName().equals(notebookName) && notebookDto.getNotes() == null);

    }

    @Test
    public void getNotebook() {
        int id = 1;
        String notebookName = "My notebook";
        NotebookEntity notebookEntity = new NotebookEntity(notebookName);
        when(cacheManager.getNoteBookFromCache(eq(id))).thenReturn(null);
        when(notebookRepository.findById(eq(id))).thenReturn(Optional.of(notebookEntity));
        NotebookDto notebookDto = noteBookService.getNotebook(id);
        assertEquals(notebookDto.getName(), notebookEntity.getName());
        assertEquals(notebookDto.getNotes(), new ArrayList<>());

    }

    @Test
    public void getNoteBookWithTag() {
        String notebookName = "My notebook";
        String firstTag = "tag1";
        String secondTag = "tag2";
        NotebookEntity notebookEntity = new NotebookEntity(notebookName);
        int noteBookId = notebookEntity.getId();

        NoteEntity note1 = new NoteEntity("First Note", "some random text", notebookEntity, new Date(), new Date());
        NoteEntity note2 = new NoteEntity("Second Note", "some random text", notebookEntity, new Date(), new Date());

        TagEntity tag1 = new TagEntity(firstTag, note1);
        TagEntity tag2 = new TagEntity(secondTag, note2);

        note1.addTag(tag1);
        note2.addTag(tag2);

        notebookEntity.addNote(note1);
        notebookEntity.addNote(note2);


        when(cacheManager.getNoteBookFromCache(eq(noteBookId))).thenReturn(null);
        when(notebookRepository.findById(eq(noteBookId))).thenReturn(Optional.of(notebookEntity));
        Mockito.doNothing().when(cacheManager).addNoteBookDto(any());
        NotebookDto notebooksByTag = noteBookService.getNotebooksByTag(noteBookId, firstTag);

        assertEquals(notebooksByTag.getNotes().size(), 1);
        assertEquals(notebooksByTag.getName(), notebookEntity.getName());
        NoteDto noteDto = notebooksByTag.getNotes().get(0);
        assertEquals(noteDto.getBody(), note1.getBody());
        assertEquals(noteDto.getTitle(), note1.getTitle());


    }

    @Test
    public void getNoteBooksByIds() {
        String notebookName = "My notebook";
        String secondNoteName = "the Notebook";
        NotebookEntity notebookEntity = new NotebookEntity(notebookName);
        NotebookEntity notebookEntity2 = new NotebookEntity(secondNoteName);
        NoteEntity note1 = new NoteEntity("First Note", "some random text", notebookEntity, new Date(), new Date());
        NoteEntity note2 = new NoteEntity("Second Note", "some random text", notebookEntity, new Date(), new Date());

        note1.setNotebook(notebookEntity);
        note2.setNotebook(notebookEntity2);

        List<NotebookEntity> notebookEntities = Arrays.asList(notebookEntity, notebookEntity2);
        when(notebookRepository.findAllById(any())).thenReturn(notebookEntities);
        List<NotebookDto> notebooks = noteBookService.getNotebooks(Arrays.asList(notebookEntity.getId(), notebookEntity2.getId()));

        assertEquals(notebooks.size(), 2);
        notebookEntities.sort(Comparator.comparing(NotebookEntity::getId));
        notebooks.sort((Comparator.comparing(NotebookDto::getId)));

        for (int i = 0; i < notebookEntities.size(); i++) {
            assertEquals(notebookEntities.get(i).getName(), notebooks.get(i).getName());
            assertEquals(notebookEntities.get(i).getId(), notebooks.get(i).getId());
        }
    }

    @Test
    public void deleteNotebook() {
        int notebookId = 1;
        when(notebookRepository.existsById(eq(notebookId))).thenReturn(true);

        Mockito.doNothing().when(notebookRepository).deleteById(notebookId);
        int deletedId = noteBookService.deleteNoteBookById(notebookId);
        assertEquals(notebookId, deletedId);
    }

    @Test()
    public void deleteNotebookWithNotExistedId() {
        int notebookId = 1;
        when(notebookRepository.existsById(eq(notebookId))).thenReturn(false);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> noteBookService.deleteNoteBookById(notebookId));
        String message = exception.getMessage();
        assertTrue(message.contains("notebook with id " + notebookId + " does not exist"));

    }


}
