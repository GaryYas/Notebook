package com.task.knime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Note Dto with all necessary data for creation of the note like tags, body, title.
 */
@Data
@Schema
@Builder
public class NoteDto {

    @Schema(required = false, hidden = true)
    int id;
    int NotebookId;
    @NotNull(message = "please provide title")
    String title;
    List<String> tags;
    @Schema(required = false, hidden = true)
    Date createdTime;
    @Schema(required = false, hidden = true)
    Date lastModified;
    @NotNull(message = "please provide body")
    String body;

}
