package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.phase.TrackPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.domain.Track;
import nl.tudelft.sem.v20232024.team08b.domain.TrackID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TracksService {
    private final VerificationService verificationService;
    private final TrackPhaseCalculator trackPhaseCalculator;
    private final TrackRepository trackRepository;
    private final ExternalRepository externalRepository;

    /**
     * Default constructor for the service.
     *
     * @param verificationService service responsible for verifying validity
     *                            of provided IDs
     * @param trackPhaseCalculator object responsible for getting the current phase
     *                             of a track
     * @param trackRepository repository storing the tracks
     * @param externalRepository class, that talks to outside microservices
     */
    @Autowired
    public TracksService(VerificationService verificationService,
                         TrackPhaseCalculator trackPhaseCalculator) {
                         TrackRepository trackRepository,
                         ExternalRepository externalRepository) {
        this.verificationService = verificationService;
        this.trackPhaseCalculator = trackPhaseCalculator;
        this.trackRepository = trackRepository;
        this.externalRepository = externalRepository;
    }


    /**
     * Checks if a given track exists, and if the given user is either a
     * reviewer or a chair of that track.
     *
     * @param requesterID the ID of the requesting user
     * @param conferenceID the ID of the conference the track is in
     * @param trackID the ID of the track
     * @throws NotFoundException if such track does not exist
     * @throws IllegalAccessException if the user is not a chair/reviewer of the track
     */
    public void verifyIfUserCanAccessTrack(Long requesterID,
                                           Long conferenceID,
                                           Long trackID) throws NotFoundException, IllegalAccessException {
        // Verify such track exists
        if (!verificationService.verifyTrack(conferenceID, trackID)) {
            throw new NotFoundException("Such track could not be found");
        }

        boolean isReviewer = verificationService.verifyRoleFromTrack(requesterID, conferenceID,
                trackID, UserRole.REVIEWER);
        boolean isChair = verificationService.verifyRoleFromTrack(requesterID, conferenceID,
                trackID, UserRole.CHAIR);
        boolean isAuthor = verificationService.verifyRoleFromTrack(requesterID, conferenceID,
                trackID, UserRole.AUTHOR);

        // Check if the requesting user is either a chair or a reviewer in that conference
        if (!isReviewer && !isChair && !isAuthor) {
            throw new IllegalAccessException("The requester is not allowed to access the track");
        }
    }

    /**
     * Returns the current phase of a given track. Also checks if the
     * requesting user has access to the track.
     *
     * @param conferenceID the ID of the conference the track is in
     * @param trackID the ID of the track
     * @return the current phase of the track
     * @throws NotFoundException if such track does not exist
     * @throws IllegalAccessException if the requesting user does not have permissions
     */
    public TrackPhase getTrackPhase(Long requesterID,
                                    Long conferenceID,
                                    Long trackID) throws NotFoundException, IllegalAccessException {
        // Verify if the user and track exist, and if the user is reviewer
        // or chair of the track. Throws respective exceptions
        verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);

        return trackPhaseCalculator.getTrackPhase(conferenceID, trackID);
    }


    /**
     * Get a track from our local repository, but if it is not present there,
     * it adds it to there. Also, if such track does not exist (according to Users
     * microservice, this throws an exception).
     *
     * @param conferenceID the ID of the conference the track is in
     * @param trackID the ID of the track
     * @return the gotten track
     * @throws NotFoundException if no such track exists
     */
    public Track getTrackWithInsertionToOurRepo(Long conferenceID, Long trackID) throws NotFoundException {
        // If such track does not exist, return empty
        if (!verificationService.verifyTrack(conferenceID, trackID)) {
            throw new NotFoundException("Such track does not exist");
        }

        // Get the track from our local DB
        TrackID id = new TrackID(conferenceID, trackID);
        Optional<Track> optional = trackRepository.findById(id);

        // If it is not in the repository, we need to add it there, taking
        // information from the other microservices
        if (optional.isEmpty()) {
            insertTrackToOurDB(conferenceID, trackID);
            optional = trackRepository.findById(id);
        }

        // If the track is still not in the repository, we did something very wrong
        if(optional.isEmpty()) {
            throw new RuntimeException("Track was not inserted into our " +
                    "repository, even though it should have been");
        }

        return optional.get();
    }

    /**
     * Assuming that a track exists in the other microservices, this method
     * inserts such track into ours.
     * TODO: implement this method. It should be implemented by another issue.
     *
     * @param conferenceID the ID of the conference the track is in.
     * @param trackID the ID of the track itself
     */
    public void insertTrackToOurDB(Long conferenceID,
                                   Long trackID) {
        return ;
    }

}
