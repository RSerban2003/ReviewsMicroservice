package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.phase.TrackPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.application.verification.UsersVerification;
import nl.tudelft.sem.v20232024.team08b.domain.BidID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Bid;
import nl.tudelft.sem.v20232024.team08b.dtos.review.BidByReviewer;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictException;
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
    private final UsersVerification usersVerification;
    private final ExternalRepository externalRepository;
    private final TrackPhaseCalculator trackPhaseCalculator;

    /**
     * Default constructor for the service.
     *
     * @param bidRepository repository storing the bids
     * @param usersVerification service responsible for verification
     * @param externalRepository repository responsible for external requests to other microservices
     * @param trackPhaseCalculator calculator for the phase of the track
     */
    @Autowired
    public BidsService(
            BidRepository bidRepository,
            UsersVerification usersVerification,
            ExternalRepository externalRepository,
            TrackPhaseCalculator trackPhaseCalculator
    ) {
        this.bidRepository = bidRepository;
        this.usersVerification = usersVerification;
        this.externalRepository = externalRepository;
        this.trackPhaseCalculator = trackPhaseCalculator;
    }

    /**
     * Retrieves the bid for a paper by a reviewer.
     *
     * @param requesterID The ID of the requester.
     * @param paperID     The ID of the paper.
     * @param reviewerID  The ID of the reviewer.
     * @return The bid for the paper.
     * @throws NotFoundException        If the bid doesn't exist.
     * @throws ForbiddenAccessException If the requester doesn't have the required role.
     *      They must be a chair or a reviewer of the track the paper is in.
     */
    public Bid getBidForPaperByReviewer(Long requesterID, Long paperID, Long reviewerID)
            throws NotFoundException, ForbiddenAccessException {
        var bid = bidRepository.findById(new BidID(paperID, reviewerID));
        if (bid.isEmpty()) {
            throw new NotFoundException("The bid doesn't exist");
        }
        if (!usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)
                && !usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)) {
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
        if (!usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)) {
            throw new ForbiddenAccessException();
        }
        var bids = bidRepository.findByPaperID(paperID);
        return bids.stream().map(bid -> new BidByReviewer(bid.getBidderID(), bid.getBid())).collect(Collectors.toList());
    }

    /**
     * Saves the preference (based on expertise) of the requester regarding reviewing the given paper.
     *
     * @param requesterID the ID of the requester (the reviewer)
     * @param paperID     the ID of the paper
     * @param bid         the preference of the reviewer in regard to reviewing the paper
     * @throws ForbiddenAccessException if the requester is not a reviewer of the track the paper is in
     * @throws NotFoundException        if the paper/track doesn't exist
     * @throws ConflictException        if the bidding phase has passed or it hasn't started
     */
    public void bid(Long requesterID, Long paperID, Bid bid)
            throws ForbiddenAccessException, NotFoundException, ConflictException {
        var paper = externalRepository.getSubmission(paperID);
        if (!usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)) {
            throw new ForbiddenAccessException();
        }
        if (trackPhaseCalculator.getTrackPhase(paper.getEventId(), paper.getTrackId()) != TrackPhase.BIDDING) {
            throw new ConflictException();
        }
        bidRepository.save(new nl.tudelft.sem.v20232024.team08b.domain.Bid(paperID, requesterID, bid));
    }
}
