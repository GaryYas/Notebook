package com.task.knime.api;

import com.task.knime.dto.NoteDto;
import com.task.knime.dto.NotebookDto;
import com.task.knime.dto.TagsNoteDto;
import com.task.knime.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Tag(name = "Tag api for note", description = "api for adding and deleting tags of the note")
public class TagController {

    @Autowired
    NoteService noteService;

    @Operation(summary = "adds tags to existing note", tags = {"Note tags api"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "create new notebook",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotebookDto.class))}),
            @ApiResponse(responseCode = "500", description = "internal server error",
                    content = @Content)})
    @PostMapping(value = "/NoteBook/Note/tag", produces = "application/json")
    public ResponseEntity<NoteDto> addTagsToNote(@Valid @RequestBody TagsNoteDto tagsNoteDto) {
        return ResponseEntity.ok(noteService.addTagsToNote(tagsNoteDto));
    }

    @Operation(summary = "delete tags from existing note", tags = {"Note tags api"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "create new notebook",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotebookDto.class))}),
            @ApiResponse(responseCode = "500", description = "internal server error",
                    content = @Content)})
    @DeleteMapping(value = "/NoteBook/Note/tag", produces = "application/json")
    public ResponseEntity<NoteDto> deleteTagsFromNote(@Valid @RequestBody TagsNoteDto tagsNoteDto) {
        return ResponseEntity.ok(noteService.deleteTagsFromNote(tagsNoteDto));
    }
}
