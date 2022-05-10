package com.task.knime.api;

import com.task.knime.dto.NotebookDto;
import com.task.knime.service.NoteBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Notebook api", description = "The notebook API for adding, retrieving and deleting and notes")
public class NoteBookController {

    @Autowired
    NoteBookService noteBookService;


    @Operation(summary = "creates new notebook", tags = {"Notebook api"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "create new notebook",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotebookDto.class))}),
            @ApiResponse(responseCode = "500", description = "internal server error",
                    content = @Content)})
    @PostMapping(value = "/NoteBook", produces = "application/json")
    public ResponseEntity<NotebookDto> createNotebook(@RequestParam String name) {
        return new ResponseEntity<>(noteBookService.createNotebook(name), HttpStatus.CREATED);
    }

    @Operation(summary = "Get notebook by notebook id with all related notes", tags = {"Notebook api"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retrieved notebook by id",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotebookDto.class))}),
            @ApiResponse(responseCode = "500", description = "internal server error",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "notebook for not found",
                    content = @Content)})
    @GetMapping(value = "/NoteBook", produces = "application/json")
    public ResponseEntity<NotebookDto> getNoteBook(@RequestParam String id) {
        return ResponseEntity.ok(noteBookService.getNotebook(Integer.parseInt(id)));
    }

    @Operation(summary = "Get notebook by notebook ids with all related notes", tags = {"Notebook api"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retrieved notebook by id",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotebookDto.class))}),
            @ApiResponse(responseCode = "500", description = "internal server error",
                    content = @Content)})
    @GetMapping(value = "/noteBooks", produces = "application/json")
    public ResponseEntity<List<NotebookDto>> getNoteBook(@RequestParam List<Integer> ids) {
        return ResponseEntity.ok(noteBookService.getNotebooks((ids)));
    }

    @Operation(summary = "Get notebook by notebook id with related notes filtered by tag", tags = {"Notebook api"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retrieved notebook by id",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotebookDto.class))}),
            @ApiResponse(responseCode = "500", description = "internal server error",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "notebook for not found",
                    content = @Content)})
    @GetMapping(value = "/noteBooks/tagged_notebook", produces = "application/json")
    public ResponseEntity<NotebookDto> getNoteBookByTag(@RequestParam int noteBookId, String tag) {
        return ResponseEntity.ok(noteBookService.getNotebooksByTag(noteBookId, tag));
    }


    @Operation(summary = "Delete Notebook by id", tags = {"Notebook api"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "delete notebook and return id",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Integer.class))}),
            @ApiResponse(responseCode = "500", description = "internal server error",
                    content = @Content)})
    @DeleteMapping(value = "/noteBook", produces = "application/json")
    public ResponseEntity<Integer> deleteNotebookById(@RequestParam String noteBookId) {
        return ResponseEntity.ok(noteBookService.deleteNoteBookById(Integer.parseInt(noteBookId)));
    }

    @Operation(summary = "Get all notebooks with all notes", tags = {"Notebook api"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retrieved notebooks",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotebookDto.class))}),
            @ApiResponse(responseCode = "500", description = "internal server error",
                    content = @Content)})
    @GetMapping(value = "/noteBooks/all", produces = "application/json")
    public ResponseEntity<List<NotebookDto>> getAllNoteBooks() {
        return ResponseEntity.ok(noteBookService.getNotebooks());

    }
}
