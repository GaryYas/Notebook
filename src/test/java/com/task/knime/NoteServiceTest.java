package com.task.knime;


import com.task.knime.cache.CacheManager;
import com.task.knime.dto.NoteDto;
import com.task.knime.dto.TagsNoteDto;
import com.task.knime.exception.ResourceNotFoundException;
import com.task.knime.model.NoteEntity;
import com.task.knime.model.NotebookEntity;
import com.task.knime.model.TagEntity;
import com.task.knime.repository.NoteRepository;
import com.task.knime.repository.NotebookRepository;
import com.task.knime.repository.TagRepository;
import com.task.knime.service.NoteService;
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
public class NoteServiceTest {

    @InjectMocks
    NoteService noteService;

    @Mock
    NoteRepository noteRepository;

    @Mock
    NotebookRepository notebookRepository;

    @Mock
    TagRepository tagRepository;

    @Mock
    CacheManager cacheManager;


    @Test
    public void createNoteTest() {
        int notebookId = 1;
        List<String> tags = Arrays.asList("tag1");
        String noteTitle = "The note";
        String notebookName = "The notebook";

        NoteDto noteDto = NoteDto.builder().body("some text").NotebookId(notebookId).tags(tags).title(noteTitle).build();
        NotebookEntity notebookEntity = new NotebookEntity(notebookName);
        NoteEntity noteEntity = new NoteEntity(noteTitle, "some text", notebookEntity, new Date(), new Date());

        when(notebookRepository.findById(eq(noteDto.getNotebookId()))).thenReturn(Optional.of(notebookEntity));
        when(noteRepository.save(any(NoteEntity.class))).thenReturn(noteEntity);

        Mockito.doNothing().when(cacheManager).addNoteBookDto(any());
        Mockito.doNothing().when(cacheManager).addNoteDto(any());
        NoteDto fetchedNote = noteService.createNote(noteDto);

        assertEquals(fetchedNote.getBody(), noteDto.getBody());
        assertEquals(fetchedNote.getTitle(), noteDto.getTitle());
        assertEquals(fetchedNote.getTags().get(0), noteDto.getTags().get(0));
    }

    @Test
    public void deleteNotebook() {
        int noteId = 1;
        NotebookEntity notebookEntity = new NotebookEntity("notebookName");
        NoteEntity noteEntity = new NoteEntity("noteTitle", "some text", notebookEntity, new Date(), new Date());

        when(noteRepository.existsById(eq(noteId))).thenReturn(true);
        when(noteRepository.getById(eq(noteId))).thenReturn(noteEntity);
        Mockito.doNothing().when(cacheManager).removeNoteBookFromCache(notebookEntity.getId());

        Mockito.doNothing().when(noteRepository).deleteById(noteId);
        int deletedId = noteService.deleteNote(noteId);
        assertEquals(noteId, deletedId);
    }

    @Test()
    public void deleteNotebookWithNotExistedId() {
        int noteId = 1;
        when(noteRepository.existsById(eq(noteId))).thenReturn(false);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> noteService.deleteNote(noteId));
        String message = exception.getMessage();
        assertTrue(message.contains("note with id " + noteId + " does not exist"));
    }

    @Test
    public void getNoteTest() {
        int noteId = 1;
        NotebookEntity notebookEntity = new NotebookEntity("notebookName");
        NoteEntity noteEntity = new NoteEntity("noteTitle", "some text", notebookEntity, new Date(), new Date());


        when(cacheManager.getNoteFromCache(eq(noteId))).thenReturn(null);
        when(noteRepository.findById(eq(noteId))).thenReturn(Optional.of(noteEntity));
        Mockito.doNothing().when(cacheManager).addNoteBookDto(any());
        Mockito.doNothing().when(cacheManager).addNoteDto(any());

        NoteDto noteDto = noteService.getNote(noteId);

        assertEquals(noteEntity.getBody(), noteDto.getBody());
        assertEquals(noteEntity.getTitle(), noteDto.getTitle());
    }

    @Test
    public void updateNoteTest() {
        int noteId = 1;
        NoteDto noteDto = NoteDto.builder().body("new body").tags(Arrays.asList("new tag"))
                .id(noteId).title("new title").build();
        NotebookEntity notebookEntity = new NotebookEntity("notebookName");
        NoteEntity noteEntity = new NoteEntity("noteTitle", "some text", notebookEntity, new Date(), new Date());
        TagEntity tagEntity = new TagEntity("new tag", noteEntity);


        when(noteRepository.findById(eq(noteId))).thenReturn(Optional.of(noteEntity));
        Mockito.doNothing().when(tagRepository).deleteTagsWithIds(any());
        when(tagRepository.saveAll(any())).thenReturn(Arrays.asList(tagEntity));
        Mockito.doNothing().when(cacheManager).addNoteBookDto(any());
        Mockito.doNothing().when(cacheManager).addNoteDto(any());
        NoteEntity updatedNoteEntity1 = noteService.getUpdatedNoteEntity(noteId, noteDto);
        when(noteRepository.save(any(NoteEntity.class))).thenReturn(updatedNoteEntity1);
        NoteDto updatedNote = noteService.updateNote(noteId, noteDto);

        assertEquals(updatedNote.getBody(), noteDto.getBody());
        assertEquals(updatedNote.getTitle(), noteDto.getTitle());
        assertEquals(updatedNote.getTags().get(0), noteDto.getTags().get(0));

    }

    @Test
    public void partialNoteUpdateTest() {
        int noteId = 1;
        Map<String, String> valuesByFieldsMap = new HashMap<>();
        String updatedTitle = "new title";
        String updatedBody = "new body";
        valuesByFieldsMap.put("title", updatedTitle);
        valuesByFieldsMap.put("body", updatedBody);
        NotebookEntity notebookEntity = new NotebookEntity("notebookName");
        NoteEntity noteEntity = new NoteEntity("noteTitle", "some text", notebookEntity, new Date(), new Date());

        when(noteRepository.findById(eq(noteId))).thenReturn(Optional.of(noteEntity));
        when(noteRepository.save(any(NoteEntity.class))).thenReturn(noteEntity);
        Mockito.doNothing().when(cacheManager).addNoteBookDto(any());
        Mockito.doNothing().when(cacheManager).addNoteDto(any());

        NoteDto updatedNote = noteService.updateNotePartial(noteId, valuesByFieldsMap);
        assertEquals(updatedNote.getBody(), updatedBody);
        assertEquals(updatedNote.getTitle(), updatedTitle);
    }

    @Test
    public void addTagsToNoteTest() {
        int noteId = 1;
        Set<String> newTags = Set.of("tag1", "tag2");
        TagsNoteDto tagsNoteDto = TagsNoteDto.builder().noteId(noteId).tags(newTags).build();
        NotebookEntity notebookEntity = new NotebookEntity("notebookName");
        NoteEntity noteEntity = new NoteEntity("noteTitle", "some text", notebookEntity, new Date(), new Date());

        when(noteRepository.findById(eq(noteId))).thenReturn(Optional.of(noteEntity));
        when(noteRepository.save(any(NoteEntity.class))).thenReturn(noteEntity);
        when(tagRepository.saveAll(any())).thenReturn(null);
        NoteDto noteDto = noteService.addTagsToNote(tagsNoteDto);

        assertEquals(noteDto.getTags().size(), 2);
        noteDto.getTags().forEach(tag -> assertTrue(newTags.contains(tag)));
    }

    @Test
    public void deleteTagsFromNoteTest() {
        int noteId = 1;
        Set<String> tagsToDelete = Set.of("tag1", "tag2");
        TagsNoteDto tagsNoteDto = TagsNoteDto.builder().noteId(noteId).tags(tagsToDelete).build();
        NotebookEntity notebookEntity = new NotebookEntity("notebookName");
        NoteEntity noteEntity = new NoteEntity("noteTitle", "some text", notebookEntity, new Date(), new Date());
        TagEntity tagEntity = new TagEntity("tag1", noteEntity);
        TagEntity tagEntity2 = new TagEntity("tag2", noteEntity);
        TagEntity tagEntity3 = new TagEntity("tag3", noteEntity);

        List<TagEntity> tagEntities = List.of(tagEntity, tagEntity2, tagEntity3);
        tagEntities.forEach(tag -> tag.setNote(noteEntity));


        when(noteRepository.findById(eq(noteId))).thenReturn(Optional.of(noteEntity));
        when(tagRepository.findAllById(Arrays.asList(tagEntity.getId(),tagEntity2.getId()))).thenReturn(Arrays.asList(tagEntity, tagEntity2));
        Mockito.doNothing().when(cacheManager).addNoteBookDto(any());
        Mockito.doNothing().when(cacheManager).addNoteDto(any());
        Mockito.doNothing().when(tagRepository).deleteTagsWithIds(any());
        when(noteRepository.save(any(NoteEntity.class))).thenReturn(noteEntity);

        NoteDto noteDto = noteService.deleteTagsFromNote(tagsNoteDto);

        assertEquals(noteDto.getTags().size(), 1);
        assertEquals(noteDto.getTags().get(0), tagEntity3.getValue());

    }

}
