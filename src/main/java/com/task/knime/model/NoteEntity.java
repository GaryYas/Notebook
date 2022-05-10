package com.task.knime.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * DataBase Entity for persisting NOTES
 */
@Getter
@Setter
@Entity
@Table(name = "NOTES")
public class NoteEntity {

    @Id
    @GeneratedValue
    public int id;

    @Column(name = "title")
    public String title;

    @Column(name = "body")
    public String body;

    @ManyToOne
    @JoinColumn(name = "Notebook_id")
    public NotebookEntity notebook;

    @CreatedDate
    @Column(name = "created_at")
    Date createdAt;

    @Column(name = "modified_at")
    Date lastModified;

    @OneToMany(
            mappedBy = "note",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    Set<TagEntity> tags = new HashSet<>();

    public NoteEntity() {
    }

    public NoteEntity(String title, String body, NotebookEntity notebook, Date createdAt, Date lastModified) {
        this.title = title;
        this.body = body;
        this.notebook = notebook;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
    }

    public void setNotebook(NotebookEntity notebook) {
        this.notebook = notebook;

        if (!notebook.getNotes().contains(this)) {
            notebook.addNote(this);
        }
    }

    public void addTag(TagEntity tag) {
        tags.add(tag);

        if (tag.getNote() != this)
            tag.setNote(this);

    }

    public void deleteEntity() {
        this.getNotebook().removeNote(this);
        this.notebook = null;
    }

    public void deleteAllTags() {
        Iterator<TagEntity> iterator = tags.iterator();
        while (iterator.hasNext()) {
            TagEntity tagEntity = iterator.next();
            iterator.remove();
            tagEntity.note = null;
        }

        this.getTags().clear();
    }
}
