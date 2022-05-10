package com.task.knime.integrationTests;

import com.task.knime.model.NoteEntity;
import com.task.knime.model.NotebookEntity;
import com.task.knime.repository.NoteRepository;
import com.task.knime.repository.NotebookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class NoteRepositoryTest {

    @Autowired
    NotebookRepository notebookRepository;

    @Autowired
    NoteRepository noteRepository;

    NotebookEntity noteBook1;
    NotebookEntity noteBook2;

    @BeforeEach
    public void before() {
        noteBook1 = notebookRepository.save(new NotebookEntity("The noteBook"));
        noteBook2 = notebookRepository.save(new NotebookEntity("Best noteBook"));
    }

    @Test
    public void CreationAndDeletionTest() {

        NoteEntity note1 = noteRepository.save(new NoteEntity("First Note", "some random text", noteBook1, new Date(), new Date()));
        NoteEntity note2 = noteRepository.save(new NoteEntity("Second Note", "some random text", noteBook2, new Date(), new Date()));

        note1.setNotebook(noteBook1);
        note2.setNotebook(noteBook2);

        assertTrue(notebookRepository.findById(noteBook1.getId()).isPresent());
        assertTrue(noteRepository.findById(note1.getId()).isPresent());
        assertEquals(2, noteRepository.findAll().size());
        assertEquals(noteRepository.findById(note1.getId()).get().getNotebook(), noteBook1);

        assertTrue(notebookRepository.findById(noteBook1.getId()).get().getNotes().contains(note1));


        //check orphan removal
        notebookRepository.deleteById(noteBook1.getId());
        assertTrue(noteRepository.findById(note1.getId()).isEmpty());

    }

    @Test
    public void  deleteNotes(){
        NoteEntity note1 = noteRepository.save(new NoteEntity("First Note", "some random text", noteBook1, new Date(), new Date()));
        NoteEntity note2 = noteRepository.save(new NoteEntity("Second Note", "some random text", noteBook2, new Date(), new Date()));
        NoteEntity note3 = noteRepository.save(new NoteEntity("third Note", "some random text", noteBook1, new Date(), new Date()));
        NoteEntity note4 = noteRepository.save(new NoteEntity("fourth Note", "some random text", noteBook2, new Date(), new Date()));

        note1.setNotebook(noteBook1);
        note2.setNotebook(noteBook2);
        note3.setNotebook(noteBook1);
        note4.setNotebook(noteBook2);

        noteRepository.getById(note3.getId()).deleteEntity();
        noteRepository.deleteById(note3.getId());
        assertEquals(3, noteRepository.findAll().size());


        List<NoteEntity> allNotes = noteRepository.findAll();
        allNotes.forEach(NoteEntity::deleteEntity);
        noteRepository.deleteAll();
        assertEquals(0, noteRepository.findAll().size());

    }
}
