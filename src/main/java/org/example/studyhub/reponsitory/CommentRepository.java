package org.example.studyhub.reponsitory;

import org.example.studyhub.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
