package org.example.studyhub.reponsitory;

import org.example.studyhub.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course,Long> {
}
