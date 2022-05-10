package com.task.knime.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Notebook dto with its name and list of notes that are related to it.
 */
@Data
@Schema
@Builder
public class NotebookDto {

    String name;
    @Schema(required = false, hidden = true)
    int id;
    @Schema(required = false, hidden = true)
    List<NoteDto> notes = new ArrayList<>();
}
