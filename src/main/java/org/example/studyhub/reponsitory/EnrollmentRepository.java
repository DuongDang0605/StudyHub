package org.example.studyhub.reponsitory;

import org.example.studyhub.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {
}
