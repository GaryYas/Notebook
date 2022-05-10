package com.task.knime.repository;

import com.task.knime.model.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TagRepository extends JpaRepository<TagEntity, Integer> {

    /**
     * Delete all tags with ids specified in {@code ids} parameter
     *
     * @param ids List of tags ids
     */
    @Modifying
    @Query(value = "delete from TAG s where s.id in ?1", nativeQuery = true)
    @Transactional
    void deleteTagsWithIds(List<Integer> ids);

}
