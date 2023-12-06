package nl.tudelft.sem.v20232024.team08b.repos;

import nl.tudelft.sem.v20232024.team08b.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
