package com.task.knime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Dto object for storing tags and note id, for adding and deleting notes
 */
@Schema
@Data
@Builder
public class TagsNoteDto {


    int noteId;
    @NotNull(message = "please provide tags")
    @NotEmpty(message = "please provide tags")
    Set<String> tags;

}
