package com.task.knime.service;

import com.task.knime.cache.CacheManager;
import com.task.knime.dto.NoteDto;
import com.task.knime.dto.TagsNoteDto;
import com.task.knime.exception.IllegalFieldException;
import com.task.knime.exception.IllegalTagModificationException;
import com.task.knime.exception.ResourceNotFoundException;
import com.task.knime.model.NoteEntity;
import com.task.knime.model.NotebookEntity;
import com.task.knime.model.TagEntity;
import com.task.knime.repository.NoteRepository;
import com.task.knime.repository.NotebookRepository;
import com.task.knime.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.task.knime.service.ServiceUtils.transformFromNoteEntityToDto;
import static com.task.knime.service.ServiceUtils.transformNotebookEntityToDto;
import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;

@Service
public class NoteService {

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    NotebookRepository notebookRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    CacheManager cacheManager;

    /**
     * create new Note
     *
     * @param noteDto with notebook id and all data required for note creation
     * @return create noteDto
     * @throws ResourceNotFoundException if there is no such note
     */
    public NoteDto createNote(NoteDto noteDto) {

        NoteAndNoteBookEntity notebookAndNoteEntities = getNotebookAndNoteEntitiesAfterPersistance(noteDto);
        NotebookEntity noteBook = notebookAndNoteEntities.getNotebookEntity();
        NoteEntity note = notebookAndNoteEntities.getNoteEntity();

        NoteDto updatedNoteDto = transformFromNoteEntityToDto(note);
        cacheManager.addNoteDto(updatedNoteDto);
        cacheManager.addNoteBookDto(transformNotebookEntityToDto(noteBook));
        return updatedNoteDto;
    }

    /**
     * deletes the note from database and updates caches
     *
     * @param id of the note to be deleted
     * @return id of the deleted note
     */
    @Transactional
    public int deleteNote(int id) {
        if (noteRepository.existsById(id)) {
            NoteEntity noteEntity = noteRepository.getById(id);
            cacheManager.removeNoteBookFromCache(noteEntity.getNotebook().getId());
            noteEntity.deleteEntity();
            noteRepository.deleteById(id);
            return id;

        }
        throw new ResourceNotFoundException("note with id " + id + " does not exist");
    }

    /**
     * retrieves note from cahe of exist in cache otherwise from database and updates the cache
     *
     * @param id of the note
     * @return retrieved note dto
     * @throws ResourceNotFoundException if there is no such note
     */
    public NoteDto getNote(int id) {

        NoteDto noteFromCache = cacheManager.getNoteFromCache(id);
        if (noteFromCache == null) {
            NoteEntity note = noteRepository.findById(id).orElseThrow(() ->
                    new ResourceNotFoundException("note with id " + id + " does not exist"));
            NoteDto noteDto = transformFromNoteEntityToDto(note);
            cacheManager.addNoteDto(noteDto);
            cacheManager.addNoteBookDto(transformNotebookEntityToDto(note.getNotebook()));
            return noteDto;
        }
        return noteFromCache;
    }

    /**
     * updates all the values of the note , updates caches as well
     *
     * @param id      note id
     * @param noteDto
     * @return updated note
     * @throws ResourceNotFoundException if there is no such note
     */
    public NoteDto updateNote(int id, NoteDto noteDto) {
        NoteEntity note = getUpdatedNoteEntity(id, noteDto);

        NoteDto updatedNoteDto = transformFromNoteEntityToDto(note);
        cacheManager.addNoteDto(updatedNoteDto);
        cacheManager.addNoteBookDto(transformNotebookEntityToDto(note.getNotebook()));

        return updatedNoteDto;
    }


    /**
     * partial update of the note, tag update is not allowed here. tags are updated via tags api
     *
     * @param id             of the not
     * @param valuesByFields map  values to be updates by fields name
     * @return new updated noteDto
     * @throws ResourceNotFoundException       if there is no such note
     * @throws IllegalTagModificationException if trying to update tags
     * @throws IllegalFieldException           if no such field exist
     */

    public NoteDto updateNotePartial(int id, Map<String, String> valuesByFields) {
        NoteEntity note = getNoteAfterPartialUpdateEntity(id, valuesByFields);
        NoteDto noteDto = transformFromNoteEntityToDto(note);
        cacheManager.addNoteDto(noteDto);
        cacheManager.addNoteBookDto(transformNotebookEntityToDto(note.getNotebook()));
        return noteDto;
    }


    /**
     * adds new tags to the note
     *
     * @param tagsNoteDto
     * @return
     * @throws ResourceNotFoundException if there is no such note
     */

    public NoteDto addTagsToNote(TagsNoteDto tagsNoteDto) {
        NoteEntity note = getNoteEntityWithAddedTags(tagsNoteDto);
        NoteDto noteDto = transformFromNoteEntityToDto(note);
        cacheManager.addNoteDto(noteDto);
        cacheManager.addNoteBookDto(transformNotebookEntityToDto(note.getNotebook()));
        return noteDto;
    }

    /**
     * deletes tags from note
     *
     * @param tagsNoteDto note id with tags to be deleted
     * @return updated noteDto with removed tags
     * @throws ResourceNotFoundException if there is no such note
     */
    @Transactional
    public NoteDto deleteTagsFromNote(TagsNoteDto tagsNoteDto) {
        NoteEntity note = getNoteEntityAfterTagsDeletion(tagsNoteDto);
        NoteDto noteDto = transformFromNoteEntityToDto(note);
        cacheManager.addNoteDto(noteDto);
        cacheManager.addNoteBookDto(transformNotebookEntityToDto(note.getNotebook()));

        return noteDto;
    }

    private NoteEntity getNoteEntityAfterTagsDeletion(TagsNoteDto tagsNoteDto) {
        NoteEntity note = noteRepository.findById(tagsNoteDto.getNoteId()).orElseThrow(() ->
                new ResourceNotFoundException("note with id " + tagsNoteDto.getNoteId() + " does not exist"));
        List<Integer> tagsToBeRemoved = note.getTags().stream()
                .filter(tag -> tagsNoteDto.getTags().contains(tag.getValue()))
                .map(TagEntity::getId)
                .collect(Collectors.toList());

        List<TagEntity> tagEntitiesToRemove = tagRepository.findAllById(tagsToBeRemoved);
        tagEntitiesToRemove.forEach(TagEntity::deleteEntity);
        tagRepository.deleteTagsWithIds(tagsToBeRemoved);

        note.setLastModified(new Date());
        noteRepository.save(note);
        return note;
    }

    @Transactional
    private NoteEntity getNoteEntityWithAddedTags(TagsNoteDto tagsNoteDto) {
        NoteEntity note = noteRepository.findById(tagsNoteDto.getNoteId()).orElseThrow(() ->
                new ResourceNotFoundException("note with id " + tagsNoteDto.getNoteId() + " does not exist"));

        saveAndUpdateTags(note, tagsNoteDto.getTags());
        note.setLastModified(new Date());
        noteRepository.save(note);
        return note;
    }

    @Transactional
    private NoteAndNoteBookEntity getNotebookAndNoteEntitiesAfterPersistance(NoteDto noteDto) {
        NotebookEntity noteBook = notebookRepository.findById(noteDto.getNotebookId()).orElseThrow(() ->
                new ResourceNotFoundException("note with id " + noteDto.getNotebookId() + " does not exist"));
        NoteEntity note = noteRepository.save(new NoteEntity(noteDto.getTitle(), noteDto.getBody(), noteBook
                , new Date(), new Date()));
        note.setNotebook(noteBook);

        saveAndUpdateTags(note, noteDto.getTags());
        return new NoteAndNoteBookEntity(note, noteBook);
    }

    @Transactional
    private void saveAndUpdateTags(NoteEntity note, Collection<String> tagsValues) {
        Set<TagEntity> tags = tagsValues.stream()
                .map(tag -> new TagEntity(tag, note)).collect(Collectors.toSet());
        tagRepository.saveAll(tags);
        tags.forEach(tag -> tag.setNote(note));
    }

    @Transactional(isolation = REPEATABLE_READ)
    private NoteEntity getNoteAfterPartialUpdateEntity(int id, Map<String, String> valuesByFields) {
        NoteEntity note = noteRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("note with id " + id + " does not exist"));
        valuesByFields.forEach((key, value) -> {

            if (key.equals("tags"))
                throw new IllegalTagModificationException("Tags should be modified by tags api");

            Field field = ReflectionUtils.findField(NoteEntity.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, note, value);
            } else throw new IllegalFieldException("no such field exist in Note");
        });
        note.setLastModified(new Date());
        return noteRepository.save(note);
    }

    @Transactional(isolation = REPEATABLE_READ)
    public NoteEntity getUpdatedNoteEntity(int id, NoteDto noteDto) {
        NoteEntity note = noteRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("note with id " + id + " does not exist"));
        note.setBody(noteDto.getBody());
        note.setTitle(noteDto.getTitle());

        note.deleteAllTags();
        List<Integer> tagsIds = note.getTags().stream().map(TagEntity::getId).collect(Collectors.toList());

        tagRepository.deleteTagsWithIds(tagsIds);
        Set<TagEntity> tags = noteDto.getTags().stream()
                .map(tag -> new TagEntity(tag, note)).collect(Collectors.toSet());

        tagRepository.saveAll(tags);
        tags.forEach(tag -> tag.setNote(note));
        note.setLastModified(new Date());
        noteRepository.save(note);
        return note;
    }

    private class NoteAndNoteBookEntity {
        NoteEntity noteEntity;
        NotebookEntity notebookEntity;

        public NoteAndNoteBookEntity(NoteEntity noteEntity, NotebookEntity notebookEntity) {
            this.noteEntity = noteEntity;
            this.notebookEntity = notebookEntity;
        }

        public NoteEntity getNoteEntity() {
            return noteEntity;
        }

        public NotebookEntity getNotebookEntity() {
            return notebookEntity;
        }
    }
}
