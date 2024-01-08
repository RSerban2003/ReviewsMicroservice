package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewsService {
    private final ReviewRepository reviewRepository;
    private final VerificationService verificationService;

    /**
     * Default constructor for the service.
     *
     * @param reviewRepository repository storing the reviews
     * @param verificationService service that handles authentication
     */
    @Autowired
    public ReviewsService(ReviewRepository reviewRepository,
                          VerificationService verificationService) {
        this.reviewRepository = reviewRepository;
        this.verificationService = verificationService;
    }

    /**
     * Verifies if:
     *  - given user exists
     *  - given paper exists
     *  - the user is a reviewer of that paper.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper that is accessed
     * @throws NotFoundException if such paper is not found
     * @throws IllegalAccessException if the user is not allowed to access the paper
     */
    private void verifyIfReviewerCanSubmitReview(Long requesterID,
                                                Long paperID) throws NotFoundException,
                                                                     IllegalAccessException {
        // Check if such paper exists
        if (!verificationService.verifyPaper(paperID)) {
            throw new NotFoundException("No such paper exists");
        }

        // Check if such user exists and has correct privileges
        if (!verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER)) {
            throw new IllegalAccessException("No such user exists");
        }

        // Check if the user is allowed to review this paper
        if (!verificationService.isReviewerForPaper(requesterID, paperID)) {
            throw new IllegalAccessException("The user is not a reviewer for this paper.");
        }

        // Verify that the current phase is the submitting phase
        verificationService.verifyTrackPhaseThePaperIsIn(paperID,
                List.of(TrackPhase.SUBMITTING)
        );
    }

    /**
     * Adds or updates a review by a user for a specific paper.
     *
     * @param reviewDTO the review object that was given by the user
     * @param requesterID the ID of the submitter
     * @param paperID the ID of the for which the review is submitted
     */
    public void submitReview(nl.tudelft.sem.v20232024.team08b.dtos.review.Review reviewDTO,
                             Long requesterID,
                             Long paperID) throws Exception {
        // Phase checking is done inside verifyIf...() method
        verifyIfReviewerCanSubmitReview(requesterID, paperID);

        ReviewID reviewId = new ReviewID(paperID, requesterID);
        Review review = new Review(reviewDTO, reviewId);
        reviewRepository.save(review);
    }

    /**
     * Returns a review from the repository.
     *
     * @param reviewerID the ID of the reviewer
     * @param paperID the ID of the paper
     * @return the review
     */
    private Review getReview(Long reviewerID, Long paperID) throws NotFoundException {

        ReviewID reviewId = new ReviewID(paperID, reviewerID);
        Optional<Review> optional = reviewRepository.findById(reviewId);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new NotFoundException("The review could not be found");
        }
    }

    /**
     * Verifies if a user can access a review. The user either has to be a chair
     * of the track of the review, or the reviewer himself.
     *
     * @param requesterID the ID of the requesting user.
     * @param reviewerID the ID of the reviewer who wrote that review.
     * @param paperID the ID of the paper reviewer.
     * @throws NotFoundException if requested review does not exist.
     * @throws IllegalAccessException if the requester is not allowed to access the review.
     */
    public void verifyIfUserCanAccessReview(Long requesterID,
                                           Long reviewerID,
                                           Long paperID) throws NotFoundException, IllegalAccessException {
        if (!verificationService.verifyPaper(paperID)) {
            throw new NotFoundException("No such paper exists");
        }

        boolean isReviewer = verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER);
        boolean isChair = verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR);
        boolean isAuthor = verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.AUTHOR);
        boolean reviewerExists = verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER);
        boolean reviewerIsAuthorOfReview = verificationService.isReviewerForPaper(reviewerID, paperID);

        // Check if such review even exists
        if (!reviewerExists || !reviewerIsAuthorOfReview) {
            throw new NotFoundException("Such review does not exist");
        }

        if (isAuthor) {
            // If the requesting user is author, then he can only access the paper during final phase
            verificationService.verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.FINAL));
        } else if (isChair || isReviewer) {
            // If the requesting user is chair or reviewer, they cannot access the review before the
            // reviewing phase
            verificationService.verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.REVIEWING, TrackPhase.FINAL));
        } else {
            throw new IllegalAccessException("The requester is not a member of the track");
        }
    }

    /**
     * Gets a review from the local repository. Also performs all checks: checks if the
     * user is allowed to access the review, if such review exists, etc.
     *
     * @param requesterID ID of requesting user
     * @param reviewerID ID of the reviewer
     * @param paperID ID of the reviewed paper
     * @return review DTO object to return
     * @throws NotFoundException if such review was not found
     * @throws IllegalAccessException if the requester is not allowed to access the paper
     * @throws IllegalCallerException if such requester does not exist
     */
    public nl.tudelft.sem.v20232024.team08b.dtos.review.Review getReview(Long requesterID,
                                                                         Long reviewerID,
                                                                         Long paperID) throws NotFoundException,
                                                                                              IllegalAccessException,
                                                                                              IllegalCallerException {
        // Verify if the requesting user is allowed to access and if the given review exists.
        // Also performs elaborate phase checking, depending on requesting user
        verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);

        // Get the review from local repository.
        // TODO: make sure that if the requesting user is author, the confidential comment is stripped.
        Review review = getReview(reviewerID, paperID);

        // Map the review from local domain object to a DTO and return it
        return new nl.tudelft.sem.v20232024.team08b.dtos.review.Review(review);
    }
}
