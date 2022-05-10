package com.task.knime.integrationTests;

import com.task.knime.model.NoteEntity;
import com.task.knime.model.NotebookEntity;
import com.task.knime.repository.NoteRepository;
import com.task.knime.repository.NotebookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class NoteBookRepositoryTest {

    @Autowired
    NotebookRepository notebookRepository;

    @Autowired
    NoteRepository noteRepository;

    @AfterEach
    public void after() {
        notebookRepository.deleteAll();
    }

    @Test
    public void noteBookCreationAndDeletionTest() {

        NotebookEntity theNoteBook = notebookRepository.save(new NotebookEntity("The noteBook"));
        NotebookEntity bestNoteBook = notebookRepository.save(new NotebookEntity("Best noteBook"));

        assertTrue(notebookRepository.findById(theNoteBook.getId()).isPresent());
        assertTrue(notebookRepository.findById(bestNoteBook.getId()).isPresent());

        notebookRepository.deleteById(theNoteBook.getId());
        assertTrue(notebookRepository.findById(theNoteBook.getId()).isEmpty());
        notebookRepository.deleteById(bestNoteBook.getId());
        assertTrue(notebookRepository.findById(bestNoteBook.getId()).isEmpty());

        assertEquals(0, notebookRepository.findAll().size());
    }

    @Test
    public void verifyNotesRemovedAfterNotebookDeletionTest() {

        NotebookEntity notebook1 = notebookRepository.save(new NotebookEntity("The notebook"));

        NoteEntity note1 = noteRepository.save(new NoteEntity("First Note", "some random text", notebook1, new Date(), new Date()));
        NoteEntity note2 = noteRepository.save(new NoteEntity("Second Note", "some random text", notebook1, new Date(), new Date()));

         note1.setNotebook(notebook1);
         note2.setNotebook(notebook1);

        assertTrue(notebookRepository.findById(notebook1.getId()).isPresent());
        assertEquals(2, notebookRepository.findById(notebook1.getId()).get().getNotes().size());
        notebookRepository.deleteById(notebook1.getId());
        assertTrue(noteRepository.findById(notebook1.getId()).isEmpty());
    }

}
