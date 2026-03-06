package org.example.studyhub.reponsitory;

import org.example.studyhub.model.User;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    @Query("""
       SELECT DISTINCT u
       FROM User u
       JOIN u.userRoles ur
       JOIN ur.role r
       WHERE (:status IS NULL OR lower(u.status) = lower(:status))
       AND (:roleName IS NULL OR r.name = :roleName)
       AND r.type.id = 1
       AND (
            :keyword IS NULL OR
            lower(u.fullName) LIKE lower(concat('%', :keyword, '%')) OR
            lower(u.username) LIKE lower(concat('%', :keyword, '%'))
       )
       ORDER BY u.createdAt
       """)
    Page<User> searchUsers(
            @Param("status") String status,
            @Param("roleName") String roleName,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
