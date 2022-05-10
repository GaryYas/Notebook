package com.task.knime.service;

import com.task.knime.dto.NoteDto;
import com.task.knime.dto.NotebookDto;
import com.task.knime.model.NoteEntity;
import com.task.knime.model.NotebookEntity;
import com.task.knime.model.TagEntity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * utils class with static methods for transforming entities to dto and vise versa
 */
public class ServiceUtils {

    public static NotebookDto transformNotebookEntityToDto(NotebookEntity notebookEntity){
        return NotebookDto.builder().id(notebookEntity.getId()).name(notebookEntity.getName())
                .notes(transformFromNotesEntityToDto(notebookEntity.getNotes())).build();

    }

    public static List<NoteDto> transformFromNotesEntityToDto(Collection<NoteEntity> notes) {
        return notes.stream().map(ServiceUtils::transformFromNoteEntityToDto).collect(Collectors.toList());
    }

    public static NoteDto transformFromNoteEntityToDto(NoteEntity noteEntity) {
        return NoteDto.builder().id(noteEntity.getId()).createdTime(noteEntity.getCreatedAt())
                .body(noteEntity.getBody())
                .NotebookId(noteEntity.getNotebook().getId())
                .lastModified(noteEntity.getLastModified()).title(noteEntity.getTitle())
                .tags(noteEntity.getTags().stream().map(TagEntity::getValue).collect(Collectors.toList())).build();
    }


}
