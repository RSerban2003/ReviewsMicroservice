package nl.tudelft.sem.v20232024.team08b.repos;

import nl.tudelft.sem.v20232024.team08b.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
