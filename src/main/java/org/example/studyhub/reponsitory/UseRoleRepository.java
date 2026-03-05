package org.example.studyhub.reponsitory;

import org.example.studyhub.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UseRoleRepository extends JpaRepository<UserRole,Long> {
}
