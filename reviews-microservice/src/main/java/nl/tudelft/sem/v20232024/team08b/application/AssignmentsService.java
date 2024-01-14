package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.phase.TrackPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.application.verification.PapersVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.TracksVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.UsersVerification;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.domain.TrackID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictException;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictOfInterestException;
import nl.tudelft.sem.v20232024.team08b.exceptions.ForbiddenAccessException;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssignmentsService {
    private final ReviewRepository reviewRepository;
    private final PapersVerification papersVerification;
    private final TracksVerification tracksVerification;
    private final UsersVerification usersVerification;
    private final ExternalRepository externalRepository;
    private final TrackPhaseCalculator trackPhaseCalculator;
    private final TrackRepository trackRepository;
    private final TracksService tracksService;

    /**
     * Default constructor for the service.
     *
     * @param reviewRepository repository storing the reviews
     * @param papersVerification object responsible for verifying paper information
     * @param tracksVerification object responsible for verifying track information
     * @param usersVerification object responsible for verifying user information
     * @param externalRepository class, that talks to outside microservices
     * @param trackPhaseCalculator object responsible for getting the current phase
     * @param trackRepository repository storing the tracks
     * @param tracksService service responsible for tracks
     */
    @Autowired
    public AssignmentsService(
            ReviewRepository reviewRepository,
            PapersVerification papersVerification,
            TracksVerification tracksVerification,
            UsersVerification usersVerification,
            ExternalRepository externalRepository,
            TrackPhaseCalculator trackPhaseCalculator,
            TrackRepository trackRepository,
            TracksService tracksService
    ) {
        this.reviewRepository = reviewRepository;
        this.papersVerification = papersVerification;
        this.tracksVerification = tracksVerification;
        this.usersVerification = usersVerification;
        this.externalRepository = externalRepository;
        this.trackPhaseCalculator = trackPhaseCalculator;
        this.trackRepository = trackRepository;
        this.tracksService = tracksService;
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
        tracksVerification.verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.ASSIGNING));

        verifyIfUserCanAssign(requesterID, paperID, UserRole.CHAIR);
        verifyIfUserCanAssign(reviewerID, paperID, UserRole.REVIEWER);
        tracksVerification.verifyIfTrackExists(paperID);

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
    public List<Long> assignments(Long requesterID, Long paperID) throws IllegalAccessException, NotFoundException {
        if (!papersVerification.verifyPaper(paperID)) {
            throw new NotFoundException("this paper does not exist");
        }
        if (!usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)) {
            throw new IllegalAccessException("Only pc chairs are allowed to do that");
        }
        tracksVerification.verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.ASSIGNING,
            TrackPhase.FINAL, TrackPhase.REVIEWING));
        List<Long> userIds = new ArrayList<>();
        List<Review> reviews = reviewRepository.findByReviewIDPaperID(paperID);
        for (Review review : reviews) {
            userIds.add(review.getReviewID().getReviewerID());
        }
        return userIds;
    }

    /**
     * This method finalizes the assignment of reviewers, so they can no longer be changed
     * manually or automatically. It moves the track into the REVIEWING phase.
     *
     * @param requesterID ID of a requester
     * @param trackID     ID of a track
     * @throws ForbiddenAccessException If the requester is not a PC chair
     * @throws NotFoundException        If the track does not exist
     * @throws ConflictException        If there is less than 3 reviewers assigned to a paper
     *                                  or the track is not in ASSIGNING phase
     */
    public void finalization(Long requesterID, TrackID trackID)
            throws ForbiddenAccessException, NotFoundException, ConflictException {
        // Ensure the track exists
        externalRepository.getTrack(trackID.getConferenceID(), trackID.getTrackID());

        // Ensure the requester is a PC chair
        if (!usersVerification.verifyRoleFromTrack(
                requesterID, trackID.getConferenceID(), trackID.getTrackID(), UserRole.CHAIR
        )) {
            throw new ForbiddenAccessException();
        }

        // Ensure the track is in the ASSIGNING phase
        if (trackPhaseCalculator.getTrackPhase(trackID.getConferenceID(), trackID.getTrackID())
                != TrackPhase.ASSIGNING) {
            throw new ConflictException();
        }

        // Ensure there is at least 3 reviewers assigned to each paper
        var submissions = externalRepository.getSubmissionsInTrack(trackID, requesterID);
        if (submissions.stream().anyMatch(submission ->
                reviewRepository.findByReviewIDPaperID(submission.getSubmissionId()).size() < 3
        )) {
            throw new ConflictException();
        }

        // Ensure the track is in our repository
        var track = tracksService.getTrackWithInsertionToOurRepo(trackID.getConferenceID(), trackID.getTrackID());

        track.setReviewersHaveBeenFinalized(true);
        trackRepository.save(track);
    }
}
