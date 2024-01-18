package nl.tudelft.sem.v20232024.team08b.repos;


import nl.tudelft.sem.v20232024.team08b.domain.Bid;
import nl.tudelft.sem.v20232024.team08b.domain.BidID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, BidID> {
    List<Bid> findByPaperID(Long paperID);
}
