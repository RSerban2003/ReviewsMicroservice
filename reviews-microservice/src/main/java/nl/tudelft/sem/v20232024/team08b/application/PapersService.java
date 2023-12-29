package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Paper;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PapersService {
    private final PaperRepository paperRepository;
    private final ReviewRepository reviewRepository;
    private final ExternalRepository externalRepository;

    private final VerificationService verificationService;

    /**
     * Default constructor for the service.
     *
     * @param paperRepository repository storing the papers
     * @param reviewRepository repository storing the reviews
     * @param externalRepository repository storing everything outside of
     *                           this microservice
     */
    @Autowired
    public PapersService(PaperRepository paperRepository,
                         ReviewRepository reviewRepository,
                         ExternalRepository externalRepository,
                         VerificationService verificationService) {
        this.paperRepository = paperRepository;
        this.reviewRepository = reviewRepository;
        this.externalRepository = externalRepository;
        this.verificationService = verificationService;
    }

    /**
     * Checks if a user is assigned to review a paper.
     *
     * @param reviewerID the ID of the user
     * @param paperID the ID of the paper
     * @return true, iff the user is a reviewer for the paper
     */
    private boolean isReviewerForPaper(Long reviewerID, Long paperID) {

        return reviewRepository.findById(new ReviewID(paperID, reviewerID)).isPresent();
    }

    /**
     * Verifies whether the user has permission to view the paper.
     *
     * @param reviewerID the ID of the requesting user
     * @param paperID the ID of the paper that is requested
     * @throws NotFoundException if such paper is not found
     * @throws IllegalCallerException if such user does not exist
     * @throws IllegalAccessException if the user is not allowed to access the paper
     *
     */
    public void verifyReviewerPermissionToViewPaper(Long reviewerID,
                                                    Long paperID) throws NotFoundException,
                                                                         IllegalCallerException {

        // Check if such paper exists
        if (!verificationService.verifyPaper(paperID)) {
            throw new NotFoundException("No such paper exists");
        }

        Long trackId = externalRepository.getSubmission(paperID).getTrackId();
        Long conferenceID = externalRepository.getSubmission(paperID).getEventId();
        //checks if the user is a reviewer or chair in the same track and conference as the paper
        if (!verificationService.verifyUser(reviewerID, conferenceID, trackId, UserRole.REVIEWER) ||
            !verificationService.verifyUser(reviewerID, conferenceID, trackId, UserRole.CHAIR)) {
            //throws error if user does not exist
            throw new IllegalCallerException("No such user exists");
        }
    }

    /**
     * Returns the content of the paper from the repository.
     *
     * @param reviewerID ID of the reviewer requesting the paper
     * @param paperID ID of the paper being requested
     * @return the paper, if all conditions are met
     */
    public Paper getPaper(Long reviewerID, Long paperID) throws NotFoundException,
                                                                IllegalAccessException {
        verifyReviewerPermissionToViewPaper(reviewerID, paperID);
        // Check if the user is assigned to the paper
        if (!isReviewerForPaper(reviewerID, paperID)) {
            throw new IllegalAccessException("The user is not a reviewer for this paper.");
        }

        Paper paper = new Paper();

        Submission submission = externalRepository.getSubmission(paperID);
        paper.setTitle(submission.getTitle());
        paper.setKeywords(submission.getKeywords());
        paper.setAbstractSection(submission.getAbstract());
        paper.setMainText(new String(submission.getPaper()));

        return paper;
    }

    /**
     * Returns the title and abstract of the paper from the external repository.
     *
     * @param reviewerID ID of the reviewer requesting the paper
     * @param paperID ID of the paper being requested
     * @return the paper, if all conditions are met
     */
    public Paper getTitleAndAbstract(Long reviewerID, Long paperID) throws NotFoundException,
                                                                           IllegalAccessException {
        verifyReviewerPermissionToViewPaper(reviewerID, paperID);
        Paper paper = new Paper();

        Submission submission = externalRepository.getSubmission(paperID);
        paper.setTitle(submission.getTitle());
        paper.setAbstractSection(submission.getAbstract());

        return paper;
    }
}
