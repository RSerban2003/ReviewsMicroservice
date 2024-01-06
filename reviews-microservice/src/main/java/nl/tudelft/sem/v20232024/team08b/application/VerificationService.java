package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUser;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUserTracksInner;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {
    ExternalRepository externalRepository;

    /**
     * Default constructor.
     *
     * @param externalRepository the external repository (injected)
     */
    @Autowired
    public VerificationService(ExternalRepository externalRepository) {
        this.externalRepository = externalRepository;
    }

    /**
     * Checks whether a user with a given ID exists.
     *
     * @param userID the ID of the user.
     * @param conferenceID the ID of the conference
     * @param trackID the ID of the track.
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
     * Checks whether a paper with a given ID exists.
     *
     * @param paperID the ID of the paper.
     * @return true, iff the given paper exists.
     */
    public boolean verifyPaper(Long paperID) {
        try {
            Submission submission = externalRepository.getSubmission(paperID);
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
}
