package nl.tudelft.sem.v20232024.team08b.application.verification;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.phase.TrackPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictException;
import nl.tudelft.sem.v20232024.team08b.exceptions.ForbiddenAccessException;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BidsVerification {
    private final UsersVerification usersVerification;
    private final TrackPhaseCalculator trackPhaseCalculator;
    private final ExternalRepository externalRepository;

    /**
     * Default constructor.
     *
     * @param usersVerification object responsible for verifying users
     * @param trackPhaseCalculator object responsible for calculating current track phase
     * @param externalRepository stores access to the objects stored in other microservices
     */
    @Autowired
    public BidsVerification(UsersVerification usersVerification,
                            TrackPhaseCalculator trackPhaseCalculator,
                            ExternalRepository externalRepository) {
        this.usersVerification = usersVerification;
        this.trackPhaseCalculator = trackPhaseCalculator;
        this.externalRepository = externalRepository;
    }

    public void verifyPermissionToAccessBid(Long requesterID, Long paperID) throws ForbiddenAccessException {
        boolean isReviewer = usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER);
        boolean isChair = usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR);
        if (!isReviewer && !isChair) {
            throw new ForbiddenAccessException();
        }
    }
    public void verifyPermissionToAccessAllBids(Long requesterID, Long paperID) throws NotFoundException,
                                                                                       ForbiddenAccessException {
        externalRepository.getSubmission(paperID);
        boolean isChair = usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR);
        if (!isChair) {
            throw new ForbiddenAccessException();
        }
    }
    public void verifyPermissionToAddBid(Long requesterID, Long paperID) throws NotFoundException,
                                                                                ForbiddenAccessException,
                                                                                ConflictException {
        boolean isReviewer = usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.REVIEWER);
        if (!isReviewer) {
            throw new ForbiddenAccessException();
        }

        Submission paper = externalRepository.getSubmission(paperID);
        if (trackPhaseCalculator.getTrackPhase(paper.getEventId(), paper.getTrackId()) != TrackPhase.BIDDING) {
            throw new ConflictException();
        }
    }
}
