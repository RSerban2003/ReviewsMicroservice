package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.domain.BidID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Bid;
import nl.tudelft.sem.v20232024.team08b.dtos.review.BidByReviewer;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.exceptions.ForbiddenAccessException;
import nl.tudelft.sem.v20232024.team08b.repos.BidRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BidsService {
    private final BidRepository bidRepository;
    private final VerificationService verificationService;
    private final ExternalRepository externalRepository;

    /**
     * Default constructor for the service.
     *
     * @param bidRepository repository storing the bids
     * @param verificationService service responsible for verification
     * @param externalRepository repository responsible for external requests to other microservices
     */
    @Autowired
    public BidsService(
            BidRepository bidRepository,
            VerificationService verificationService,
            ExternalRepository externalRepository
    ) {
        this.bidRepository = bidRepository;
        this.verificationService = verificationService;
        this.externalRepository = externalRepository;
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

    /**
     * Retrieves a list of bids made by reviewers for a specific paper.
     *
     * @param requesterID the ID of the requester
     * @param paperID     the ID of the paper
     * @return a list of BidByReviewer objects representing the bids for the paper
     * @throws NotFoundException        if the paper is not found
     * @throws ForbiddenAccessException if the requester is not a chair of the track the paper is in
     */
    public List<BidByReviewer> getBidsForPaper(Long requesterID, Long paperID)
            throws NotFoundException, ForbiddenAccessException {
        externalRepository.getSubmission(paperID);
        if (!verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)) {
            throw new ForbiddenAccessException();
        }
        var bids = bidRepository.findByPaperID(paperID);
        return bids.stream().map(bid -> new BidByReviewer(bid.getBidderID(), bid.getBid())).collect(Collectors.toList());
    }
}
