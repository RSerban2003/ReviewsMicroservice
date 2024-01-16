package nl.tudelft.sem.v20232024.team08b.application.verification;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.phase.TrackPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictException;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictOfInterestException;
import nl.tudelft.sem.v20232024.team08b.exceptions.ForbiddenAccessException;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentsVerification {
    private final PapersVerification papersVerification;
    private final UsersVerification usersVerification;
    private final TracksVerification tracksVerification;
    private final ExternalRepository externalRepository;
    private final TrackPhaseCalculator trackPhaseCalculator;

    /**
     * Default constructor.
     *
     * @param papersVerification object that does paper verification
     * @param usersVerification object that performs users verification
     * @param tracksVerification object that performs tracks verification
     * @param externalRepository object that stores objects from external repository
     * @param trackPhaseCalculator calculates track phases
     */
    @Autowired
    public AssignmentsVerification(PapersVerification papersVerification,
                                   UsersVerification usersVerification,
                                   TracksVerification tracksVerification,
                                   ExternalRepository externalRepository,
                                   TrackPhaseCalculator trackPhaseCalculator) {
        this.papersVerification = papersVerification;
        this.usersVerification = usersVerification;
        this.tracksVerification = tracksVerification;
        this.externalRepository = externalRepository;
        this.trackPhaseCalculator = trackPhaseCalculator;
    }

    /**
     * Verifies if the requester can remove assignments from the given paper.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper that we are un-assigning reviews from
     * @throws IllegalAccessException if the user is not a chair
     * @throws NotFoundException if such paper does not exist
     */
    public void verifyPermissionToRemoveAssignment(Long requesterID, Long paperID) throws IllegalAccessException,
                                                                                          NotFoundException {
        if (!papersVerification.verifyPaper(paperID)) {
            throw new NotFoundException("this paper does not exist");
        }
        if (!usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)) {
            throw new IllegalAccessException("Only pc chairs are allowed to do that");
        }
        tracksVerification.verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.ASSIGNING));
    }

    /**
     * Verify if the requesting user has permission to finalize the assignments
     * for the given track.
     *
     * @param requesterID the ID of the requesting user
     * @param conferenceID the ID of the conference of the track
     * @param trackID the ID of the track that is finalized
     * @throws ConflictException if the track phase is not assigning
     * @throws ForbiddenAccessException if the user is not a chair of the track
     * @throws NotFoundException if such track does not exist
     */
    public void verifyPermissionToFinalize(Long requesterID,
                                           Long conferenceID,
                                           Long trackID) throws ConflictException,
                                                                ForbiddenAccessException,
                                                                NotFoundException {
        // Ensure the track exists
        externalRepository.getTrack(conferenceID, trackID);

        // Ensure the requester is a PC chair
        if (!usersVerification.verifyRoleFromTrack(
                requesterID, conferenceID, trackID, UserRole.CHAIR
        )) {
            throw new ForbiddenAccessException();
        }

        // Ensure the track is in the ASSIGNING phase
        if (trackPhaseCalculator.getTrackPhase(conferenceID, trackID)
                != TrackPhase.ASSIGNING) {
            throw new ConflictException();
        }
    }

    /**
     * Verifies if the user has permission to get all current assignments
     * for a given paper.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper
     * @throws NotFoundException if no such paper exists
     * @throws IllegalAccessException if the requesting user is not a chair
     */
    public void verifyPermissionToGetAssignments(Long requesterID, Long paperID) throws NotFoundException,
                                                                                        IllegalAccessException {
        if (!papersVerification.verifyPaper(paperID)) {
            throw new NotFoundException("this paper does not exist");
        }
        if (!usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)) {
            throw new IllegalAccessException("Only pc chairs are allowed to do that");
        }
        tracksVerification.verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.ASSIGNING,
                TrackPhase.FINAL, TrackPhase.REVIEWING));
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
        tracksVerification.verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.ASSIGNING));

        switch (role) {
            case CHAIR:
                if (!usersVerification.verifyRoleFromPaper(userID, paperID, UserRole.CHAIR)) {
                    throw new IllegalAccessException("You are not PC chair for this track");
                }
                break;
            case REVIEWER:
                if (!usersVerification.verifyRoleFromPaper(userID, paperID, UserRole.REVIEWER)) {
                    throw new NotFoundException("There is no such a user in this track");
                }
                papersVerification.verifyCOI(paperID, userID);
                break;
            default:
                throw new IllegalAccessException("You are not pc chair for this track");
        }

        return true;
    }

    /**
     * Verifies if the requesting user is allowed to get the list of papers
     * that they are assigned to.
     *
     * @param requesterID the ID of the requesting user
     * @throws NotFoundException if no such user exists
     */
    public void verifyIfUserCanGetAssignedPapers(Long requesterID) throws NotFoundException {
        if (!usersVerification.verifyIfUserExists(requesterID)) {
            throw new NotFoundException("User does not exist!");
        }

    }

    /**
     * Verifies if the requester can assign a reviewer to a paper.
     *
     * @param requesterID the ID of the requesting user
     * @param paperID the ID of the paper to assign
     * @param reviewerID the ID of the reviewer to assign to
     * @throws NotFoundException if no such paper is found
     * @throws ConflictOfInterestException if the current track phase is not assigning
     * @throws IllegalAccessException if the requester is not a chair in the track
     */
    public void verifyIfManualAssignmentIsPossible(Long requesterID,
                                                   Long paperID,
                                                   Long reviewerID) throws NotFoundException,
                                                                           ConflictOfInterestException,
                                                                           IllegalAccessException {
        verifyIfUserCanAssign(requesterID, paperID, UserRole.CHAIR);
        verifyIfUserCanAssign(reviewerID, paperID, UserRole.REVIEWER);
        tracksVerification.verifyIfTrackExists(paperID);
    }
}
