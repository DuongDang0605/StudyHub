package org.example.studyhub.reponsitory;

import org.example.studyhub.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {
}
