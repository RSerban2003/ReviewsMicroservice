package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.phase.PaperPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.dtos.review.*;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PapersService {
    private final ReviewRepository reviewRepository;
    private final ExternalRepository externalRepository;
    private final PaperRepository paperRepository;
    private final VerificationService verificationService;
    private final PaperPhaseCalculator paperPhaseCalculator;

    /**
     * Default constructor for the service.
     *
     * @param paperRepository  repository storing papers
     * @param reviewRepository repository storing the reviews
     * @param externalRepository repository storing everything outside of
     *                           this microservice
     * @param verificationService service that handles verification
     * @param paperPhaseCalculator class that calculates the phase of a paper
     */
    @Autowired
    public PapersService(PaperRepository paperRepository,
                         ReviewRepository reviewRepository,
                         ExternalRepository externalRepository,
                         VerificationService verificationService,
                         PaperPhaseCalculator paperPhaseCalculator) {
        this.paperRepository = paperRepository;
        this.reviewRepository = reviewRepository;
        this.externalRepository = externalRepository;
        this.verificationService = verificationService;
        this.paperPhaseCalculator = paperPhaseCalculator;
    }

    /**
     * Verifies whether the user has permission to view the paper.
     * TODO: add phase verification.
     *
     * @param reviewerID the ID of the requesting user
     * @param paperID the ID of the paper that is requested
     * @throws NotFoundException if such paper is not found
     * @throws IllegalCallerException if the user is not assigned as a reviewer to the paper
     * @throws IllegalAccessException if the user is not reviewer or chair in the track of the paper
     *
     */
    public void verifyPermissionToViewPaper(Long reviewerID,
                                            Long paperID) throws IllegalCallerException,
                                                                 IllegalAccessException,
                                                                 NotFoundException {
        if (!verificationService.verifyPaper(paperID)) {
            throw new NotFoundException("No such paper exists");
        }
        boolean isChair = verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.CHAIR);
        boolean isReviewer = verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER);
        boolean isReviewerForPaper = verificationService.isReviewerForPaper(reviewerID, paperID);
        if (!isChair) {
            if (!isReviewer) {
                throw new IllegalCallerException("No such user exists");
            } else if (!isReviewerForPaper) {
                throw new IllegalAccessException("The user is not a reviewer for this paper.");
            }
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
        verifyPermissionToViewPaper(reviewerID, paperID);

        Submission submission = externalRepository.getSubmission(paperID);
        Paper paper = new Paper(submission);

        return paper;
    }

    /**
     * Verifies whether the user has permission to view the title and abstract.
     * TODO: add phase verification.
     *
     * @param reviewerID the ID of the requesting user
     * @param paperID the ID of the paper that is requested
     * @throws NotFoundException if such paper is not found
     * @throws IllegalAccessException if the user is not reviewer or chair in the track of the paper
     *
     */
    public void verifyPermissionToViewTitleAndAbstract(Long reviewerID,
                                                       Long paperID) throws IllegalCallerException,
                                                                            NotFoundException {

        if (!verificationService.verifyPaper(paperID)) {
            throw new NotFoundException("No such paper exists");
        }

        if (!verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.REVIEWER) &&
                !verificationService.verifyRoleFromPaper(reviewerID, paperID, UserRole.CHAIR)) {

            throw new IllegalCallerException("No such user exists");
        }
    }

    /**
     * Returns the title and abstract of the paper from the external repository.
     *
     * @param reviewerID ID of the reviewer requesting the paper
     * @param paperID ID of the paper being requested
     * @return the paper, if all conditions are met
     */
    public PaperSummary getTitleAndAbstract(Long reviewerID, Long paperID) throws NotFoundException,
                                                                           IllegalAccessException {
        verifyPermissionToViewTitleAndAbstract(reviewerID, paperID);

        Submission submission = externalRepository.getSubmission(paperID);
        PaperSummary paperSummary = new PaperSummary(submission);

        return paperSummary;
    }

    /**
     * Gets the phase of the requested paper.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper
     * @return current phase of the paper
     * @throws NotFoundException if such paper does not exist
     * @throws IllegalAccessException if the user is not allowed to view the paper
     */
    public PaperPhase getPaperPhase(Long requesterID,
                                    Long paperID) throws NotFoundException, IllegalAccessException {
        verifyPermissionToViewPaper(requesterID, paperID);
        return paperPhaseCalculator.getPaperPhase(paperID);
    }
}
