package org.example.studyhub.reponsitory;

import org.example.studyhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
