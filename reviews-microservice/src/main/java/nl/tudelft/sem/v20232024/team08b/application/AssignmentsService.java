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
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;
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

    public void verifyIfRequesterCanAssign(Long requesterID, Long paperID) throws IllegalAccessException{

        if(!verificationService.verifyRoleFromPaper(requesterID,paperID, UserRole.CHAIR)){
            throw new IllegalAccessException("You are not PC chair for this track");
        }
    }

    public void verifyIfReviewerCanBeAssigned(Long reviewerID, Long paperID)
        throws NotFoundException, ConflictOfInterestException {
        if(!verificationService.verifyRoleFromPaper(reviewerID,paperID, UserRole.REVIEWER)){
            throw new NotFoundException("There is no such a user in this track");
        }
        verificationService.verifyCOI(paperID, reviewerID);
    }

    public void assignManually(Long requesterID, Long reviewerID, Long paperID)
        throws IllegalAccessException, NotFoundException, ConflictOfInterestException {
        List<TrackPhase> phases = new ArrayList<>();
        TrackPhase phase = TrackPhase.ASSIGNING;
        phases.add(phase);
        verificationService.verifyTrackPhaseThePaperIsIn(paperID, phases);

        verifyIfRequesterCanAssign(requesterID, paperID);
        verifyIfReviewerCanBeAssigned(reviewerID, paperID);
        verificationService.verifyIfTrackExists(paperID);

        ReviewID reviewID = new ReviewID(paperID,reviewerID);
        Review toSave = new Review();
        toSave.setReviewID(reviewID);
        reviewRepository.save(toSave);

    }

    public List<Long> assignments(Long requesterID, Long paperID) throws IllegalAccessException {
        if(!verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR))
            throw new IllegalAccessException("Only pc chairs are allowed to do that");
        List<Long> userIds = new ArrayList<>();
        List<Review> reviews = reviewRepository.findByReviewIDPaperID(paperID);
        for(Review review : reviews){
            userIds.add(review.getReviewID().getReviewerID());
        }
        return userIds;
    }
}
