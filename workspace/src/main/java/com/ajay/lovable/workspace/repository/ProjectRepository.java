package com.ajay.lovable.workspace.repository;



import com.ajay.lovable.commonlib.enums.ProjectRole;
import com.ajay.lovable.workspace.entitiy.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Query("""
            SELECT p as project, pm.projectRole as role
            FROM Project p
            JOIN ProjectMember pm ON pm.project.id = p.id
            WHERE pm.id.userId = :userId
              AND p.deletedAt IS NULL
            ORDER BY p.updatedAt DESC
            """)
    List<ProjectWithRole> getAllAccessibleByUser(@Param("userId") Long userId);


    @Query("""
            Select p from Project p
            where p.id = :projectId
            AND p.deletedAt is NULL
            AND EXISTS (
                   select 1 from ProjectMember pm where pm.id.userId = :userId
                   and pm.id.projectId = p.id
            )
            """)
    Optional<Project> findAccessibleProjectById(@Param("projectId") Long projectId, @Param("userId") Long userId);


    @Query("""
            SELECT p as project, pm.projectRole as role
            FROM Project p
            JOIN ProjectMember pm ON pm.project.id = p.id
            WHERE p.id = :projectId
              AND pm.id.userId = :userId
              AND p.deletedAt IS NULL
            """)
    Optional<ProjectWithRole> findAccessibleProjectByIdWithRole(@Param("projectId") Long projectId,
                                                                @Param("userId") Long userId);



    interface ProjectWithRole {
        Project getProject();
        ProjectRole getRole();
    }


}
