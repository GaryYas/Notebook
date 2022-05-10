package com.task.knime.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * DataBase Entity for persisting tags
 */
@Getter
@Setter
@Entity
@Table(name = "TAG")
public class TagEntity {

    @Id
    @GeneratedValue
    public int id;

    @Column(name = "value")
    public String value;

    @ManyToOne
    @JoinColumn(name = "note_id")
    public NoteEntity note;

    public TagEntity() {
    }

    public TagEntity(String value, NoteEntity note) {
        this.value = value;
        this.note = note;
    }

    public void setNote(NoteEntity note) {
        this.note = note;

        if(!note.getTags().contains(this))
            note.addTag(this);

    }

    public void deleteEntity(){
        this.getNote().getTags().remove(this);
        this.note = null;
    }
}
