package com.task.knime.repository;

import com.task.knime.model.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<NoteEntity, Integer> {
}
