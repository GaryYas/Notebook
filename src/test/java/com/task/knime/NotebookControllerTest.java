package com.task.knime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.knime.api.NoteBookController;
import com.task.knime.dto.NotebookDto;
import com.task.knime.service.NoteBookService;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(NoteBookController.class)
public class NotebookControllerTest {

    @MockBean
    NoteBookService noteBookService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    @Test
    public void saveUserTest() {
        int notebookId = 1;
        NotebookDto notebookDto = NotebookDto.builder().name("some note").id(notebookId).build();
        when(noteBookService.createNotebook(any())).thenReturn(notebookDto);
        mockMvc.perform(post("/NoteBook")
                .param("name", "some note"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(notebookDto.getName()))
                .andExpect(jsonPath("$.id", Matchers.is(notebookDto.getId())));


    }

    @SneakyThrows
    @Test
    public void deleteUserTest() {
        int noteBookIdToDelete = 1;
        when(noteBookService.deleteNoteBookById(anyInt())).thenReturn(noteBookIdToDelete);

        mockMvc.perform(delete("/noteBook")
                .param("noteBookId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

}
