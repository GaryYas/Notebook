package com.task.knime.api;

import com.task.knime.dto.NoteDto;
import com.task.knime.dto.NotebookDto;
import com.task.knime.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@Tag(name = "Note api", description = "The note API for adding, retrieving and deleting and notes")
public class NoteController {

    @Autowired
    NoteService noteService;

    @Operation(summary = "creates new notebook", tags = {"Note api"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "create new notebook",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotebookDto.class))}),
            @ApiResponse(responseCode = "500", description = "internal server error",
                    content = @Content)})
    @PostMapping(value = "/NoteBook/Note", produces = "application/json")
    public ResponseEntity<NoteDto> createNote(@Valid @RequestBody NoteDto noteDto) {
        return new ResponseEntity<>(noteService.createNote(noteDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete note by id", tags = {"Note api"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "deleted note by id",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Integer.class))}),
            @ApiResponse(responseCode = "500", description = "internal server error",
                    content = @Content)})
    @DeleteMapping(value = "/notebook/note", produces = "application/json")
    public ResponseEntity<Integer> deleteNoteById(@RequestParam String noteId) {
        return ResponseEntity.ok(noteService.deleteNote(Integer.parseInt(noteId)));
    }

    @Operation(summary = "Get note by notebook id with all related notes", tags = {"Note api"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retrieved note by id",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotebookDto.class))}),
            @ApiResponse(responseCode = "500", description = "internal server error",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "note for not found",
                    content = @Content)})
    @GetMapping(value = "/notebook/note", produces = "application/json")
    public ResponseEntity<NoteDto> getNoteBook(@RequestParam String id) {
        return ResponseEntity.ok(noteService.getNote(Integer.parseInt(id)));
    }

    @Operation(summary = "updates all fields of the note", tags = {"Note api"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "create new notebook",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotebookDto.class))}),
            @ApiResponse(responseCode = "500", description = "internal server error",
                    content = @Content)})
    @PutMapping(value = "/noteBook/Note", produces = "application/json")
    public ResponseEntity<NoteDto> updateNote(@RequestParam int id,@Valid @RequestBody NoteDto noteDto) {
        return ResponseEntity.ok(noteService.updateNote(id, noteDto));
    }

    @Operation(summary = "partial update of the note,upate of the tags are not supported in this api. look for tag api", tags = {"Note api"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "create new notebook",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotebookDto.class))}),
            @ApiResponse(responseCode = "500", description = "internal server error",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "for not existed field",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "for trying update tags",
                    content = @Content)})
    @PatchMapping(value = "/noteBook/Note", produces = "application/json")
    public ResponseEntity<NoteDto> updateNotePartial(@RequestParam int id, @RequestBody Map<String, String> noteFields) {
        return ResponseEntity.ok(noteService.updateNotePartial(id, noteFields));
    }

}
