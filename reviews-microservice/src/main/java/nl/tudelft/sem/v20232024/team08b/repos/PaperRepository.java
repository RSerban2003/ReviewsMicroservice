package nl.tudelft.sem.v20232024.team08b.repos;

import nl.tudelft.sem.v20232024.team08b.domain.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperRepository extends JpaRepository<Paper, Long> {
}
