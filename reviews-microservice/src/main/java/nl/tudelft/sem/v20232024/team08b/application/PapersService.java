package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.phase.PaperPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.dtos.review.*;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import nl.tudelft.sem.v20232024.team08b.domain.Paper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PapersService {
    private final ExternalRepository externalRepository;
    private final PaperRepository paperRepository;
    private final VerificationService verificationService;
    private final PaperPhaseCalculator paperPhaseCalculator;

    /**
     * Default constructor for the service.
     *
     * @param externalRepository repository storing everything outside of
     *                           this microservice
     * @param paperRepository repository storing papers
     * @param verificationService service that handles verification
     * @param paperPhaseCalculator class that calculates the phase of a paper
     */
    @Autowired
    public PapersService(ExternalRepository externalRepository,
                         PaperRepository paperRepository,
                         VerificationService verificationService,
                         PaperPhaseCalculator paperPhaseCalculator) {
        this.externalRepository = externalRepository;
        this.paperRepository = paperRepository;
        this.verificationService = verificationService;
        this.paperPhaseCalculator = paperPhaseCalculator;
    }

    /**
     * Verifies whether the user has permission to view the paper. This method
     * does not and SHOULD NOT do any phase checking.
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
        if (!isChair && !isReviewer) {
            throw new IllegalCallerException("No such user exists");
        }
        if (isReviewer && !isReviewerForPaper) {
            throw new IllegalAccessException("The user is not a reviewer for this paper.");
        }
    }

    /**
     * Returns the content of the paper from the repository.
     *
     * @param reviewerID ID of the reviewer requesting the paper
     * @param paperID ID of the paper being requested
     * @return the paper, if all conditions are met
     */
    public nl.tudelft.sem.v20232024.team08b.dtos.review.Paper getPaper(Long reviewerID, Long paperID)
            throws NotFoundException, IllegalAccessException {
        // Verify that user has permission to view the paper
        verifyPermissionToViewPaper(reviewerID, paperID);

        // Verify that the current track phase allows for reading full papers
        verificationService.verifyTrackPhaseThePaperIsIn(
                paperID,
                List.of(TrackPhase.SUBMITTING, TrackPhase.REVIEWING, TrackPhase.FINAL)
        );

        Submission submission = externalRepository.getSubmission(paperID);

        return new nl.tudelft.sem.v20232024.team08b.dtos.review.Paper(submission);
    }

    /**
     * Verifies whether the user has permission to view the title and abstract.
     *
     * @param reviewerID the ID of the requesting user
     * @param paperID the ID of the paper that is requested
     * @throws NotFoundException if such paper is not found
     * @throws IllegalCallerException if the user is not reviewer or chair in the track of the paper
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

        // Track phase verification for reading an abstract of a paper is not necessary,
        // since title and abstract can be read during all phases

        Submission submission = externalRepository.getSubmission(paperID);

        return new PaperSummary(submission);
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

    /**
     * Verifies the permission of a user given by requesterID
     * to view the status of a paper given by paperID.
     *
     * @param requesterID the ID of the user
     * @param paperID the ID of the paper
     * @throws IllegalAccessException if the user does not have permission to view the status of the paper
     */
    public void verifyPermissionToViewStatus(Long requesterID,
                                             Long paperID) throws IllegalAccessException {
        boolean isReviewer = verificationService.isReviewerForPaper(requesterID, paperID) &&
                verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER);
        boolean isAuthor = verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.AUTHOR) &&
                verificationService.isAuthorToPaper(requesterID, paperID);
        boolean isChair = verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR);

        if (!isReviewer && !isAuthor && !isChair) {
            throw new IllegalAccessException("User does not have permission to view the status of this paper");
        }
    }

    /**
     * Shows the status of a paper given by paperID to a user given by userID
     * after verifying if the user has the appropriate permissions.
     *
     * @param requesterID the ID of the user
     * @param paperID the ID of the paper
     * @return the status of the paper
     * @throws NotFoundException if the paper does not exist
     * @throws IllegalAccessException if the user does not have the required permissions
     */
    public PaperStatus getState(Long requesterID,
                                Long paperID) throws NotFoundException, IllegalAccessException {
        verifyPermissionToViewStatus(requesterID, paperID);
        Optional<Paper> optional = paperRepository.findById(paperID);
        if (optional.isPresent()) {
            return optional.get().getStatus();
        } else {
            throw new NotFoundException("The paper could not be found");
        }
    }
}
