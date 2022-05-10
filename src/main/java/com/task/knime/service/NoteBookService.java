package com.task.knime.service;

import com.task.knime.cache.CacheManager;
import com.task.knime.dto.NoteDto;
import com.task.knime.dto.NotebookDto;
import com.task.knime.exception.ResourceNotFoundException;
import com.task.knime.model.NotebookEntity;
import com.task.knime.repository.NotebookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.task.knime.service.ServiceUtils.transformNotebookEntityToDto;

@Service
@Slf4j
public class NoteBookService {

    @Autowired
    NotebookRepository notebookRepository;

    @Autowired
    CacheManager cacheManager;

    /**
     * creates notebook with no notes
     * @param name of the notebook
     * @return created notebook dto
     */
    public NotebookDto createNotebook(String name) {
        NotebookEntity noteBook = notebookRepository.save(new NotebookEntity(name));
        NotebookDto noteBookDTo = NotebookDto.builder().name(name).id(noteBook.getId()).build();
        cacheManager.addNoteBookDto(noteBookDTo);
        return noteBookDTo;

    }

    /**
     *  retrieves notebook from cache if exist in cache otherwise takes it from database
     * @param id of the notebook
     * @return retrieved notebook dto
     * @throws ResourceNotFoundException if there is no such notebook
     */
    //todo perhaps add here transaction isolation
    public NotebookDto getNotebook(int id) {

        NotebookDto noteBookFromCache = cacheManager.getNoteBookFromCache(id);
        if (noteBookFromCache == null) {
            NotebookEntity noteBook = notebookRepository.findById(id).orElseThrow(() ->
                    new ResourceNotFoundException("notebook with id " + id + " does not exist"));
            NotebookDto notebookDto = transformNotebookEntityToDto(noteBook);
            cacheManager.addNoteBookDto(notebookDto);
            return notebookDto;
        }
        return noteBookFromCache;

    }

    /**
     * get notebooks by ids
     * @param ids of the notebooks to be retrieved
     * @return list of notebooks
     */
    public List<NotebookDto> getNotebooks(List<Integer> ids) {
        return notebookRepository.findAllById(ids).stream().map(ServiceUtils::transformNotebookEntityToDto).collect(Collectors.toList());

    }

    /**
     * retrieves all notes that matches tag value of specific notebook
     * @param notebookId id of the notebook
     * @param tag to be filtered according to
     * @return notebook dto with relevant notes
     */
    public NotebookDto getNotebooksByTag(int notebookId, String tag) {

        NotebookDto noteBookFromCache = cacheManager.getNoteBookFromCache(notebookId);

        if (noteBookFromCache == null) {
            NotebookEntity noteBook = notebookRepository.findById(notebookId).orElseThrow(() ->
                    new ResourceNotFoundException("notebook with id " + notebookId + " does not exist"));
            noteBookFromCache = transformNotebookEntityToDto(noteBook);
            cacheManager.addNoteBookDto(transformNotebookEntityToDto(noteBook));
        }


        List<NoteDto> filteredNotes = noteBookFromCache.getNotes().stream()
                .filter(note -> note.getTags().contains(tag))
                .collect(Collectors.toList());
        return NotebookDto.builder().name(noteBookFromCache.getName()).notes(filteredNotes).id(noteBookFromCache.getId()).build();

    }

    /**
     * deletes notebook by id from database and from cache
     * @param id of tne notebook to be deleted
     * @return id of the deleted notebook
     * @throws ResourceNotFoundException if there is no such notebook
     */
    public int deleteNoteBookById(int id) {
        if (notebookRepository.existsById(id)) {
            cacheManager.removeNoteBookFromCache(id);
            notebookRepository.deleteById(id);
            return id;
        }

        throw new ResourceNotFoundException("notebook with id " + id + " does not exist");
    }

    /**
     * retrieves all notebooks
     * @return List all existed notebooks
     */
    public List<NotebookDto> getNotebooks() {
        return notebookRepository.findAll().stream().map(ServiceUtils::transformNotebookEntityToDto)
                .collect(Collectors.toList());

    }
}
