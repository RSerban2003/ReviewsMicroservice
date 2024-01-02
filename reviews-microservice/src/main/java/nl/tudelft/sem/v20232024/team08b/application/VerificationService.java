package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUser;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUserTracksInner;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {
    ExternalRepository externalRepository;
    ReviewRepository reviewRepository;

    /**
     * Default constructor.
     *
     * @param externalRepository the external repository (injected)
     */
    @Autowired
    public VerificationService(ExternalRepository externalRepository,
                               ReviewRepository reviewRepository) {
        this.externalRepository = externalRepository;
        this.reviewRepository = reviewRepository;
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
    public boolean verifyUser(Long userID, Long conferenceID, Long trackID, UserRole role) {
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
    public boolean verifyRole(Long userID, Long paperID, UserRole role) throws NotFoundException {
        Long trackID = externalRepository.getSubmission(paperID).getTrackId();
        Long conferenceID = externalRepository.getSubmission(paperID).getEventId();
        return verifyUser(userID, trackID, conferenceID, role);
    }

    /**
     * Checks whether a user is assigned as a reviewer to a specific paper.
     *
     * @param reviewerID the ID of the user.
     * @param paperID the ID of the paper.
     * @return whether the user is assigned to the paper or not.
     * @throws IllegalAccessException if the user is not assigned to the paper.
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
}
