package com.ajay.lovable.workspace.repository;


import com.ajay.lovable.commonlib.enums.ProjectRole;
import com.ajay.lovable.workspace.entitiy.ProjectMember;
import com.ajay.lovable.workspace.entitiy.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember,ProjectMemberId> {
    List<ProjectMember> findByIdProjectId(Long projectId);


    @Query("""
            SELECT pm.projectRole FROM ProjectMember pm
            WHERE pm.id.userId=:userId and pm.id.projectId=:projectId
            """)
    Optional<ProjectRole> findRoleByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);

    @Query("""
            SELECT COUNT(pm) FROM ProjectMember pm
            WHERE pm.id.userId = :userId AND pm.projectRole = 'OWNER'
            """)
    int countProjectOwnedByUser(@Param("userId") Long userId);


}
