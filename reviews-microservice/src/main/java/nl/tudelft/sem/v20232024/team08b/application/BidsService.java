package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.domain.BidID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Bid;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.exceptions.ForbiddenAccessException;
import nl.tudelft.sem.v20232024.team08b.repos.BidRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BidsService {
    private final ReviewRepository reviewRepository;
    private final BidRepository bidRepository;
    private final VerificationService verificationService;

    /**
     * Default constructor for the service.
     *
     * @param bidRepository repository storing the bids
     * @param reviewRepository repository storing the reviews
     * @param verificationService service responsible for verification
     */
    @Autowired
    public BidsService(
            BidRepository bidRepository,
            ReviewRepository reviewRepository,
            VerificationService verificationService
    ) {
        this.bidRepository = bidRepository;
        this.reviewRepository = reviewRepository;
        this.verificationService = verificationService;
    }

    /**
     * Retrieves the bid for a paper by a reviewer.
     *
     * @param requesterID The ID of the requester.
     * @param paperID     The ID of the paper.
     * @param reviewerID  The ID of the reviewer.
     * @return The bid for the paper.
     * @throws NotFoundException        If the bid doesn't exist.
     * @throws ForbiddenAccessException If the requester doesn't have the required role. They must be a chair or a reviewer of the track the paper is in.
     */
    public Bid getBidForPaperByReviewer(Long requesterID, Long paperID, Long reviewerID)
            throws NotFoundException, ForbiddenAccessException {
        var bid = bidRepository.findById(new BidID(paperID, reviewerID));
        if (bid.isEmpty()) {
            throw new NotFoundException("The bid doesn't exist");
        }
        if (!verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)
                && !verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)) {
            throw new ForbiddenAccessException();
        }
        return bid.get().getBid();
    }
}
