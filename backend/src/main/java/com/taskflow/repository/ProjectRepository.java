package com.taskflow.repository;

import com.taskflow.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.members LEFT JOIN FETCH p.owner WHERE p.id = :id")
    java.util.Optional<Project> findByIdWithMembers(Long id);

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.members LEFT JOIN FETCH p.owner")
    List<Project> findAllWithMembers();

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.members m LEFT JOIN FETCH p.owner WHERE m.id = :userId OR p.owner.id = :userId")
    List<Project> findByMemberOrOwner(Long userId);
}
