package com.task.knime.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
/**
 * DataBase Entity for persisting Notebook
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter
@Entity
@Table(name = "NOTEBOOK")
public class NotebookEntity {

    @Id()
    @GeneratedValue
    private int id;

    @Column
    String name;

    @OneToMany(
            mappedBy = "notebook",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    Set<NoteEntity> notes = new HashSet<>();

    public NotebookEntity() {
    }

    public NotebookEntity(String name) {
        this.name = name;
    }

    public void addNote(NoteEntity noteEntity) {
        notes.add(noteEntity);

        if(noteEntity.getNotebook() != this)
            noteEntity.setNotebook(this);
    }

    public void removeNote(NoteEntity noteEntity) {
        this.getNotes().removeIf(note -> note.getId() == noteEntity.getId());
    }
}
