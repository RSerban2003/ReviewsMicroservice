package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.repos.CommentRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewsService {
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final PaperRepository paperRepository;
    private final VerificationService verificationService;
    private final ExternalRepository externalRepository;

    /**
     * Default constructor for the service.
     *
     * @param reviewRepository repository storing the reviews
     * @param commentRepository repository storing the comments
     * @param paperRepository repository storing the papers
     * @param verificationService service that handles authentication
     * @param externalRepository  repository storing everything outside of
     *                            this microservice
     */
    @Autowired
    public ReviewsService(ReviewRepository reviewRepository,
                          CommentRepository commentRepository,
                          PaperRepository paperRepository,
                          VerificationService verificationService,
                          ExternalRepository externalRepository) {
        this.reviewRepository = reviewRepository;
        this.commentRepository = commentRepository;
        this.paperRepository = paperRepository;
        this.verificationService = verificationService;
        this.externalRepository = externalRepository;
    }

    /**
     * Checks if a user is a reviewer of a paper.
     * TODO: make sure that when a user is assigned to review some paper, an empty
     *       review is created with that user and the paper ID.
     *
     * @param reviewerID the ID of the user
     * @param paperID the ID of the paper
     * @return true, iff the user is a reviewer for the paper
     */
    private boolean isReviewerForPaper(Long reviewerID, Long paperID) {
        return reviewRepository.findById(new ReviewID(paperID, reviewerID)).isPresent();
    }

    /**
     * Verifies if:
     *  - given user exists
     *  - given paper exists
     *  - the user is a reviewer of that paper
     *  TODO: at the moment, the review phases are not taken into account of. For instance.
     *        the user should not be able to resubmit after finalization or before review phase.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper that is accessed
     * @throws NotFoundException if such paper is not found
     * @throws IllegalAccessException if the user is not allowed to access the paper
     * @throws IllegalCallerException if such user does not exist
     */
    public void verifyIfReviewerCanSubmitReview(Long requesterID,
                                                Long paperID) throws NotFoundException,
                                                                     IllegalAccessException,
                                                                     IllegalCallerException {
        // Check if such paper exists
        if (!verificationService.verifyPaper(paperID)) {
            throw new NotFoundException("No such paper exists");
        }

        // Check if such user exists and has correct privileges
        Long containingTrackID = externalRepository.getSubmission(paperID).getTrackId();
        Long containingConferenceID = externalRepository.getSubmission(paperID).getEventId();
        if (!verificationService.verifyUser(requesterID, containingConferenceID,
                containingTrackID, UserRole.REVIEWER)) {
            throw new IllegalCallerException("No such user exists");
        }

        // Check if the user is allowed to review this paper
        if (!isReviewerForPaper(requesterID, paperID)) {
            throw new IllegalAccessException("The user is not a reviewer for this paper.");
        }
    }

    /**
     * Adds or updates a review by a user for a specific paper.
     * TODO: take into account the phase of the review status.
     *
     * @param reviewDTO the review object that was given by the user
     * @param requesterID the ID of the submitter
     * @param paperID the ID of the for which the review is submitted
     */
    public void submitReview(nl.tudelft.sem.v20232024.team08b.dtos.review.Review reviewDTO,
                             Long requesterID,
                             Long paperID) throws Exception {

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
    private Review getReview(Long reviewerID, Long paperID) {
        ReviewID reviewId = new ReviewID(paperID, reviewerID);
        return reviewRepository.findById(reviewId).get();
    }

    /**
     * Checks if a review exists.
     *
     * @param conferenceID the conference where the review is in.
     * @param trackID the track where the conference is in.
     * @param reviewerID the ID of the reviewer of the review.
     * @param paperID the ID of the reviewer paper
     * @return true, iff such review exists.
     */
    public boolean checkIfReviewExists(Long conferenceID, Long trackID,
                                        Long reviewerID, Long paperID) {
        // Check if such paper exists
        if (!verificationService.verifyPaper(paperID)) {
            return false;
        }

        // Check if the reviewer that is supposed to have reviewed the given paper even exists
        if (!verificationService.verifyUser(reviewerID, conferenceID, trackID, UserRole.REVIEWER)) {
            return false;
        }

        // Finally, if such reviewer exists, and such paper exists, we have to chek
        if (!isReviewerForPaper(reviewerID, paperID)) {
            return false;
        }

        return true;
    }

    /**
     * Verifies if a user can access a review. The user either has to be a chair
     * of the track of the review, or the reviewer himself.
     * TODO: the review phase information is not taken into account. For instance, after
     *       the review process has finalized, the author should also be able to access
     *       the review.
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


        // Get the containing track ID.
        Long containingTrackID = externalRepository.getSubmission(paperID).getTrackId();
        Long containingConferenceID = externalRepository.getSubmission(paperID).getEventId();

        boolean isReviewer = verificationService.verifyUser(requesterID, containingConferenceID,
                containingTrackID, UserRole.REVIEWER);
        boolean isChair = verificationService.verifyUser(requesterID, containingConferenceID,
                containingTrackID, UserRole.CHAIR);

        // Check if the requesting user is either a chair or a reviewer in that conference
        if (!isReviewer && !isChair) {
            throw new IllegalCallerException("The requester is not allowed to access the paper");
        }

        // Check if such review exists
        if (!checkIfReviewExists(containingConferenceID, containingTrackID, reviewerID, paperID)) {
            throw new NotFoundException("Such review does not exist");
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
        // Verify if the requesting user is allowed to access and if the given review exists
        verifyIfUserCanAccessReview(requesterID, reviewerID, paperID);

        // Get the review from local repository
        Review review = getReview(reviewerID, paperID);

        // Map the review from local domain object to a DTO
        nl.tudelft.sem.v20232024.team08b.dtos.review.Review reviewDTO =
                new nl.tudelft.sem.v20232024.team08b.dtos.review.Review(review);

        return reviewDTO;
    }
}
