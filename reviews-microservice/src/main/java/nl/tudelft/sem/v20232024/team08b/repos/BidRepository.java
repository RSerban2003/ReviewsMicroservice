package nl.tudelft.sem.v20232024.team08b.repos;

import java.util.List;
import nl.tudelft.sem.v20232024.team08b.domain.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
  List<Bid> getBidsOfPapers(Long requesterID, Long id);
}
