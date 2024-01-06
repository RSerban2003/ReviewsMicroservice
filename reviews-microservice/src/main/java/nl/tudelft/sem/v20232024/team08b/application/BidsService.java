package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.domain.BidId;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Bid;
import nl.tudelft.sem.v20232024.team08b.repos.BidRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BidsService {
    private final ReviewRepository reviewRepository;
    private final BidRepository bidRepository;

    /**
     * Default constructor for the service.
     *
     * @param bidRepository repository storing the bids
     * @param reviewRepository repository storing the reviews
     */
    @Autowired
    public BidsService(
            BidRepository bidRepository, ReviewRepository reviewRepository
    ) {
        this.bidRepository = bidRepository;
        this.reviewRepository = reviewRepository;
    }

    public Bid getBidForPaperByReviewer(Long requesterID, Long paperID, Long reviewerID)
            throws NotFoundException {
        var bid = bidRepository.findById(new BidId(paperID, reviewerID));
        if (bid.isEmpty()) {
            throw new NotFoundException("The reviewer hasn't made a bid for the paper");
        }
        return bid.get().getBid();
    }
}
