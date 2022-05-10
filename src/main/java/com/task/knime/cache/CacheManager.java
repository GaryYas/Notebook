package com.task.knime.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.task.knime.dto.NoteDto;
import com.task.knime.dto.NotebookDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * cache layer manager,using Caffeine thread safe cache for library for caching notes and notebooks.
 * expire eviction and maximum size configured via properties file.
 * uses uses the Window TinyLfu eviction policy
 */
@Component
public class CacheManager {

    @Value("${cache.notebook.duration}")
    int noteBookExpirationDuration;

    @Value("${cache.note.duration}")
    int noteExpirationDuration;

    @Value("${cache.notebook.size}")
    int noteBookSizeCache;

    @Value("${cache.note.size}")
    int noteSizeCache;


    Cache<Integer, NotebookDto> noteBookCache;

    Cache<Integer, NoteDto> noteCache;

    @PostConstruct
    private void configureCaches() {
        noteBookCache = Caffeine.newBuilder()
                .expireAfterWrite(noteBookExpirationDuration, TimeUnit.MINUTES)
                .maximumSize(noteBookSizeCache)
                .build();
        noteCache = Caffeine.newBuilder()
                .expireAfterWrite(noteExpirationDuration, TimeUnit.MINUTES)
                .maximumSize(noteSizeCache)
                .build();
    }

    public NotebookDto getNoteBookFromCache(int id) {
        return noteBookCache.getIfPresent(id);
    }

    public NoteDto getNoteFromCache(int id) {
        return noteCache.getIfPresent(id);
    }

    public void addNoteBookDto(NotebookDto notebookDto) {
        noteBookCache.put(notebookDto.getId(), notebookDto);
    }

    public void addNoteDto(NoteDto noteDto) {
        noteCache.put(noteDto.getId(), noteDto);
    }

    public void removeNoteBookFromCache(int id) {
        if (noteBookCache.getIfPresent(id) != null) {
            Objects.requireNonNull(noteBookCache.getIfPresent(id)).getNotes().forEach(note -> {
                        if (noteCache.getIfPresent(note.getId()) != null)
                            noteCache.invalidate(note.getId());
                    }
            );
            noteBookCache.invalidate(id);
        }

    }

    public void removeNoteFromCache(int id) {
        if (noteCache.getIfPresent(id) != null)
            noteCache.invalidate(id);
    }


}
