package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Review;
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

    public void verifyIfRequesterCanAssign(Long requesterID, Long paperID) throws IllegalAccessException, NotFoundException {
        if (!verificationService.verifyPaper(paperID)) {
            throw new NotFoundException("No such paper exists");
        }

        if(!verificationService.verifyRoleFromPaper(requesterID,paperID, UserRole.CHAIR)){
            throw new IllegalAccessException("You are not PC chair for this track");
        }
    }

    public void verifyIfReviewerCanBeAssigned(Long reviewerID, Long paperID) throws NotFoundException {
        if(!verificationService.verifyRoleFromPaper(reviewerID,paperID, UserRole.REVIEWER)){
            throw new NotFoundException("There is no such a user in this track");
        }
        //verificationService.verifyCOI(re)
    }

    public Review assignManually(Long requesterID, Long reviewerID, Long paperID)
        throws IllegalAccessException, NotFoundException {

        verifyIfRequesterCanAssign(requesterID, paperID);
        verifyIfReviewerCanBeAssigned(reviewerID, paperID);

        Review review = new Review();

        return review;
    }
}
