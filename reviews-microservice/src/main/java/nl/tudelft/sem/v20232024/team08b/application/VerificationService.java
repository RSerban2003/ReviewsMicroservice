package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.phase.TrackPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.User;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUser;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUserTracksInner;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@Service
public class VerificationService {
    private final ExternalRepository externalRepository;
    private final ReviewRepository reviewRepository;
    private final TrackPhaseCalculator trackPhaseCalculator;

    /**
     * Default constructor.
     *
     * @param externalRepository the external repository (injected)
     * @param reviewRepository repository responsible for storing reviews
     * @param trackPhaseCalculator object, responsible for getting current phase
     */
    @Autowired
    public VerificationService(ExternalRepository externalRepository,
                               ReviewRepository reviewRepository,
                               TrackPhaseCalculator trackPhaseCalculator) {
        this.externalRepository = externalRepository;
        this.reviewRepository = reviewRepository;
        this.trackPhaseCalculator = trackPhaseCalculator;
    }

    /**
     * Checks whether a user with a given ID exists and is in the same conference and track as a paper with a give ID.
     *
     * @param userID the ID of the user.
     * @param trackID the ID of the track.
     * @param conferenceID the ID of the conference.
     * @param role the role to check for that user.
     * @return true, iff the given user exists with the given role.
     */
    public boolean verifyRoleFromTrack(Long userID, Long conferenceID, Long trackID, UserRole role) {
        try {
            // Get all roles of each track from other microservice
            RolesOfUser roles = externalRepository.getRolesOfUser(userID);
            boolean ret = false;

            // Iterate over each role/track
            for (RolesOfUserTracksInner thisTrack : roles.getTracks()) {

                // Cast from Integer to Long
                Long thisTrackID = Long.valueOf(thisTrack.getTrackId());
                Long thisConferenceID = Long.valueOf(thisTrack.getEventId());

                // If we do not care about this track, continue
                if (!thisTrackID.equals(trackID) || !thisConferenceID.equals(conferenceID)) {
                    continue;
                }

                // Parse the role string into enum
                UserRole thisRole = UserRole.parse(thisTrack.getRoleName());

                boolean rolesAreEqual = thisRole.equals(role);
                ret = ret || rolesAreEqual;
            }
            return ret;
        } catch (NotFoundException e) {
            return false;
        }
    }


    /**
     * Checks whether a user with a given ID exists and is in the same conference and track as a paper with a give ID.
     *
     * @param userID the ID of the user.
     * @param paperID the ID of the paper.
     * @param role the role to check for that user.
     * @return true, iff the given user exists with the given role.
     */
    public boolean verifyRoleFromPaper(Long userID, Long paperID, UserRole role) {
        try {
            Long trackID = externalRepository.getSubmission(paperID).getTrackId();
            Long conferenceID = externalRepository.getSubmission(paperID).getEventId();
            return verifyRoleFromTrack(userID, conferenceID, trackID, role);
        } catch (NotFoundException e) {
            return false;
        }
    }

    /**
     * Checks whether a user is assigned as a reviewer to a specific paper.
     *
     * @param reviewerID the ID of the user.
     * @param paperID the ID of the paper.
     * @return whether the user is assigned to the paper or not.
     */
    public boolean isReviewerForPaper(Long reviewerID, Long paperID) {
        return reviewRepository.isReviewerForPaper(reviewerID, paperID);
    }

    /**
     * Checks whether a paper with a given ID exists.
     *
     * @param paperID the ID of the paper.
     * @return true, iff the given paper exists.
     */
    public boolean verifyPaper(Long paperID) {
        try {
            externalRepository.getSubmission(paperID);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    /**
     * Verifies if a given track exists.
     *
     * @param conferenceID the ID of the conference of the track
     * @param trackID the ID of the track
     * @return true, iff such track exists
     */
    public boolean verifyTrack(Long conferenceID,
                               Long trackID) {
        try {
            externalRepository.getTrack(conferenceID, trackID);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    /**
     * Method that checks if the current phase is one of the given phases.
     *
     * @param conferenceID the ID of the conference of the track.
     * @param trackID the ID of the conference.
     * @param acceptablePhases a list of all acceptable phases
     * @throws IllegalAccessException exception thrown if the current phase is not in the list
     * @throws NotFoundException exception thrown if the track couldn't be found
     */
    public void verifyTrackPhase(Long conferenceID,
                                 Long trackID,
                                 List<TrackPhase> acceptablePhases) throws IllegalAccessException, NotFoundException {
        try {
            TrackPhase currentPhase = trackPhaseCalculator.getTrackPhase(conferenceID, trackID);
            if (!acceptablePhases.contains(currentPhase)) {
                throw new IllegalAccessException("This action is not allowed in the current phase");
            }
        } catch (NotFoundException e) {
            throw new NotFoundException("No such track exists");
        }
    }

    /**
     * Finds the track a paper is in and checks if the current phase of that track
     * is one of the listed.
     *
     * @param paperID the ID of the paper
     * @param acceptablePhases a list of all acceptable phases
     * @throws IllegalAccessException exception thrown if the current phase is not in the list
     * @throws NotFoundException exception thrown if the track couldn't be found
     */
    public void verifyTrackPhaseThePaperIsIn(Long paperID,
                                          List<TrackPhase> acceptablePhases) throws IllegalAccessException,
                                                                                    NotFoundException {
        Submission submission = externalRepository.getSubmission(paperID);
        Long conferenceID = submission.getEventId();
        Long trackID = submission.getTrackId();
        verifyTrackPhase(conferenceID, trackID, acceptablePhases);
    }

    /**
     *  Checks whether a user given by userID is an author of a paper given by paperID.
     *
     * @param userID the ID of the user
     * @param paperID the ID of the paper
     * @return true if the user is an author of the paper, false otherwise
     * @throws NotFoundException if the paperID is not valid
     */
    public boolean isAuthorToPaper(Long userID, Long paperID) {
        try {
            Submission submission = externalRepository.getSubmission(paperID);
            List<@Valid User> authors = submission.getAuthors();
            for (@Valid User author : authors) {
                if (Objects.equals(author.getUserId(), userID)) {
                    return true;
                }
            }
        } catch (NotFoundException e) {}
        return false;
    }
}