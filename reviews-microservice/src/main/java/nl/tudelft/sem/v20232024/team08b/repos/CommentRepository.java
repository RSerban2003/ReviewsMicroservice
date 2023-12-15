package nl.tudelft.sem.v20232024.team08b.repos;

import nl.tudelft.sem.v20232024.team08b.domain.Comment;
import nl.tudelft.sem.v20232024.team08b.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
