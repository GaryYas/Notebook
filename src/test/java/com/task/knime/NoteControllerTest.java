package com.task.knime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.knime.api.NoteController;
import com.task.knime.dto.NoteDto;
import com.task.knime.service.NoteService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(NoteController.class)
public class NoteControllerTest {

    @MockBean
    NoteService noteService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    @Test
    public void createNoteTest() {
        List<String> tags = Arrays.asList("tag1");
        String noteTitle = "The note";
        int notebookId = 1;

        NoteDto noteDto = NoteDto.builder().body("some text").NotebookId(notebookId).tags(tags).title(noteTitle).build();
        when(noteService.createNote(any(NoteDto.class))).thenReturn(noteDto);

        mockMvc.perform(post("/NoteBook/Note")
                .content(mapper.writeValueAsString(noteDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.body").value(noteDto.getBody()))
                .andExpect(jsonPath("$.title").value(noteDto.getTitle()))
                .andExpect(jsonPath("$.id").value(noteDto.getId()));

    }

    @SneakyThrows
    @Test
    public void deleteNoteTest() {
        int noteIdToDelete = 1;
        when(noteService.deleteNote(anyInt())).thenReturn(noteIdToDelete);

        mockMvc.perform(delete("/notebook/note")
                .param("noteId","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }
}
