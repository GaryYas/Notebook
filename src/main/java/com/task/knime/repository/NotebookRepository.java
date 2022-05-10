package com.task.knime.repository;

import com.task.knime.model.NotebookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotebookRepository extends JpaRepository<NotebookEntity, Integer> {
}
