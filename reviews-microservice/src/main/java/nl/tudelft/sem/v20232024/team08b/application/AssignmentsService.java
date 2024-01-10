package nl.tudelft.sem.v20232024.team08b.application;

import java.util.ArrayList;
import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.ConflictOfInterestException;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.repos.BidRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignmentsService {
    private final ReviewRepository reviewRepository;
    private final BidRepository bidRepository;

    private final VerificationService verificationService;

    /**
     * Default constructor for the service.
     *
     * @param bidRepository repository storing the bids
     * @param reviewRepository repository storing the reviews
     */
    @Autowired
    public AssignmentsService(BidRepository bidRepository,
                              ReviewRepository reviewRepository, VerificationService verificationService) {
        this.bidRepository = bidRepository;
        this.reviewRepository = reviewRepository;
        this.verificationService = verificationService;
    }

    /**
     * This method verifies the permission to do certain tasks.
     *
     * @param userID ID of a user
     * @param paperID ID of a paper
     * @param role Role of the user
     * @return returns true if user has permission
     * @throws IllegalAccessException when the user does not have a permission
     * @throws NotFoundException when there is no reviewer with this ID in this track
     * @throws ConflictOfInterestException when there is conflict of interest
     */
    public boolean verifyIfUserCanAssign(Long userID, Long paperID, UserRole role)
        throws IllegalAccessException, NotFoundException, ConflictOfInterestException {
        switch (role) {
            case CHAIR:
                if (!verificationService.verifyRoleFromPaper(userID, paperID, UserRole.CHAIR)) {
                    throw new IllegalAccessException("You are not PC chair for this track");
                }
                break;
            case REVIEWER:
                if (!verificationService.verifyRoleFromPaper(userID, paperID, UserRole.REVIEWER)) {
                    throw new NotFoundException("There is no such a user in this track");
                }
                verificationService.verifyCOI(paperID, userID);
                break;
            default:
                throw new IllegalAccessException("You are not pc chair for this track");
        }

        return true;
    }

    /**
     * This method assigns manually reviewer to a paper.
     *
     * @param requesterID ID of a requester
     * @param reviewerID ID of a reviewer to be assigned
     * @param paperID ID of a paper to which the reviewer will be assigned
     * @throws IllegalAccessException If the requester does not have a permission to assign
     * @throws NotFoundException If the reviewer is not in the track of paper
     * @throws ConflictOfInterestException If reviewer can not be assigned due to conflict of interest
     */
    public void assignManually(Long requesterID, Long reviewerID, Long paperID)
        throws IllegalAccessException, NotFoundException, ConflictOfInterestException {
        List<TrackPhase> phases = new ArrayList<>();
        TrackPhase phase = TrackPhase.ASSIGNING;
        phases.add(phase);
        verificationService.verifyTrackPhaseThePaperIsIn(paperID, phases);

        verifyIfUserCanAssign(requesterID, paperID, UserRole.CHAIR);
        verifyIfUserCanAssign(reviewerID, paperID, UserRole.REVIEWER);
        verificationService.verifyIfTrackExists(paperID);

        ReviewID reviewID = new ReviewID(paperID, reviewerID);
        Review toSave = new Review();
        toSave.setReviewID(reviewID);
        reviewRepository.save(toSave);

    }

    /**
     * Method returns the list of all ID's of a reviewers for requested paper.
     *
     * @param requesterID ID of a requester
     * @param paperID ID of a requested paper
     * @return List of ID's of reviewers
     * @throws IllegalAccessException If the requester does not have permissions to see the assignments
     */
    public List<Long> assignments(Long requesterID, Long paperID) throws IllegalAccessException {
        if (!verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)) {
            throw new IllegalAccessException("Only pc chairs are allowed to do that");
        }
        List<Long> userIds = new ArrayList<>();
        List<Review> reviews = reviewRepository.findByReviewIDPaperID(paperID);
        for (Review review : reviews) {
            userIds.add(review.getReviewID().getReviewerID());
        }
        return userIds;
    }
}
