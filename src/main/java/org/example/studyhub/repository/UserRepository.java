package org.example.studyhub.repository;

import org.example.studyhub.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
   SELECT DISTINCT u
   FROM User u
   LEFT JOIN u.userRoles ur
   LEFT JOIN ur.role r
   WHERE (:status IS NULL OR u.status = :status)
   AND (:roleId IS NULL OR r.id = :roleId)
   AND (r IS NULL OR r.type.id = 1)
   AND (
        :keyword = '' OR
        lower(u.fullName) LIKE concat('%', :keyword, '%') OR
        lower(u.username) LIKE concat('%', :keyword, '%')
   )
   """)
    Page<User> searchUsers(
            @Param("status") String status,
            @Param("roleId") Long roleId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}