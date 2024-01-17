package nl.tudelft.sem.v20232024.team08b.application;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.phase.TrackPhaseCalculator;
import nl.tudelft.sem.v20232024.team08b.application.verification.TracksVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.UsersVerification;
import nl.tudelft.sem.v20232024.team08b.communicators.SubmissionsMicroserviceCommunicator;
import nl.tudelft.sem.v20232024.team08b.communicators.UsersMicroserviceCommunicator;
import nl.tudelft.sem.v20232024.team08b.domain.Track;
import nl.tudelft.sem.v20232024.team08b.domain.TrackID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperSummaryWithID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.exceptions.ForbiddenAccessException;
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TracksService {
    private final TracksVerification tracksVerification;
    private final UsersVerification usersVerification;
    private final TrackPhaseCalculator trackPhaseCalculator;
    private final TrackRepository trackRepository;
    private final UsersMicroserviceCommunicator usersCommunicator;
    private final SubmissionsMicroserviceCommunicator submissionsCommunicator;


    /**
     * Default constructor for the service.
     *
     * @param trackPhaseCalculator object responsible for getting the current phase
     *                             of a track
     * @param trackRepository repository storing the tracks
     * @param usersCommunicator class, that talks to outside microservice
     * @param tracksVerification object responsible for verifying track information
     * @param usersVerification object responsible for verifying user information
     * @param submissionsCommunicator gets objects from submissions microservice
     */
    @Autowired
    public TracksService(TrackPhaseCalculator trackPhaseCalculator,
                         TrackRepository trackRepository,
                         UsersMicroserviceCommunicator usersCommunicator,
                         TracksVerification tracksVerification,
                         UsersVerification usersVerification,
                         SubmissionsMicroserviceCommunicator submissionsCommunicator) {
        this.trackPhaseCalculator = trackPhaseCalculator;
        this.trackRepository = trackRepository;
        this.usersCommunicator = usersCommunicator;
        this.tracksVerification = tracksVerification;
        this.usersVerification = usersVerification;
        this.submissionsCommunicator = submissionsCommunicator;
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
        tracksVerification.verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);

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
        // If such track does not exist, throw error
        if (!tracksVerification.verifyTrack(conferenceID, trackID)) {
            throw new NotFoundException("Such track does not exist");
        }

        // Get the track from our local repository
        TrackID id = new TrackID(conferenceID, trackID);
        Optional<Track> optional = trackRepository.findById(id);

        // If it is not in the repository, we need to add it there, taking
        // information from the other microservices
        if (optional.isEmpty()) {
            tracksVerification.insertTrack(conferenceID, trackID);
            optional = trackRepository.findById(id);
        }

        // If the track is still not in the repository, we did something very wrong
        if (optional.isEmpty()) {
            throw new RuntimeException("Track was not inserted into our " +
                    "repository, even though it should have been");
        }

        return optional.get();
    }


    /**
     * Sets the bidding deadline of a track and saves it in the repository.
     *
     * @param conferenceID the ID of the conference of the track
     * @param trackID the ID of the track
     * @param deadline the deadline to set
     * @throws NotFoundException if such track does not exist
     */
    private void setBiddingDeadlineCommon(Long conferenceID,
                                          Long trackID,
                                          Date deadline) throws NotFoundException {
        Track track = getTrackWithInsertionToOurRepo(conferenceID, trackID);
        track.setBiddingDeadline(deadline);
        trackRepository.save(track);
    }

    /**
     * Sets the default bidding deadline for a track. Assumes that the track
     * exists.
     *
     * @param conferenceID the ID of the conference the track is in.
     * @param trackID the ID of the track.
     */
    public void setDefaultBiddingDeadline(Long conferenceID,
                                          Long trackID) throws NotFoundException {
        // Get the submission deadline from the other microservice
        Long submissionDeadlineUnix =
                usersCommunicator.getTrack(conferenceID, trackID).getDeadline();

        // Add exactly 2 days (in milliseconds) to the submission deadline
        long biddingDeadlineUnix = submissionDeadlineUnix + (1000 * 60 * 60 * 24 * 2);

        // Convert from Unix timestamp to Date
        Date biddingDeadline = Date.from(Instant.ofEpochMilli(biddingDeadlineUnix));

        // Set the bidding deadline for the track
        setBiddingDeadlineCommon(conferenceID, trackID, biddingDeadline);
    }


    /**
     * Gets the current bidding deadline of a track.
     *
     * @param requesterID the ID of the requesting user
     * @param conferenceID the ID of the conference of the track
     * @param trackID the ID of the track
     * @return the bidding deadline
     * @throws NotFoundException if such track does not exist
     * @throws IllegalAccessException if the user has no access to the track
     */
    public Date getBiddingDeadline(Long requesterID,
                                   Long conferenceID,
                                   Long trackID) throws NotFoundException,
                                                        IllegalAccessException {
        // No phase verification needs to be done. We only need to check if
        // the requester belongs to the given track.
        tracksVerification.verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);

        // Check if the track is in our repo at the moment
        boolean isInRepo = trackRepository.findById(new TrackID(conferenceID, trackID)).isPresent();

        // If the track was not in the repository before getting it, we need to
        // add it to the repo and set the default deadline for it
        if (!isInRepo) {
            getTrackWithInsertionToOurRepo(conferenceID, trackID);
            setDefaultBiddingDeadline(conferenceID, trackID);
        }

        // Finally, get the deadline of the track
        Track track = getTrackWithInsertionToOurRepo(conferenceID, trackID);
        return track.getBiddingDeadline();
    }

    /**
     * Sets the bidding deadline to the one provided. Also performs checks if
     * the user is allowed to change the deadline, and if the current phase allows
     * it.
     *
     * @param requesterID the ID of the requester
     * @param conferenceID the ID of the conference
     * @param trackID the ID of the track
     * @param newDeadline the new deadline to be set
     * @throws NotFoundException if no such track exists
     * @throws IllegalAccessException if the user does not have permissions to set deadline
     */
    public void setBiddingDeadline(Long requesterID,
                                   Long conferenceID,
                                   Long trackID,
                                   Date newDeadline) throws NotFoundException, IllegalAccessException {
        // First, we check if the user in general can access the track
        tracksVerification.verifyIfUserCanAccessTrack(requesterID, conferenceID, trackID);

        // We also check if the user is a chair in the track
        boolean isChair = usersVerification.verifyRoleFromTrack(requesterID, conferenceID,
                trackID, UserRole.CHAIR);

        if (!isChair) {
            throw new IllegalAccessException("The user is not a chair in the track");
        }

        // Verify that the current phase is either bidding or submitting phase
        tracksVerification.verifyTrackPhase(conferenceID, trackID,
                List.of(TrackPhase.BIDDING, TrackPhase.SUBMITTING));

        setBiddingDeadlineCommon(conferenceID, trackID, newDeadline);
    }

    /**
     * Gets the submissions of a track and transforms them into an instance of PaperSummaryWithID.
     *
     * @param requesterID the ID of the requester
     * @param conferenceID the ID of the conference
     * @param trackID the ID of the track
     * @return A list of PaperSummaryWithID
     * @throws ForbiddenAccessException if the track does not exist
     * @throws NotFoundException if the user is not a pc chair
     */
    public List<PaperSummaryWithID> getPapers(Long requesterID,
                                              Long conferenceID,
                                              Long trackID) throws ForbiddenAccessException,
            NotFoundException {
        if (!usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.CHAIR)) {
            throw new ForbiddenAccessException();
        }
        if (!tracksVerification.verifyTrack(conferenceID, trackID)) {
            throw new NotFoundException(
                    "Not Found. The requested track or conference was not found."
            );
        }
        var submissions = submissionsCommunicator.getSubmissionsInTrack(conferenceID, trackID, requesterID);

        final List<PaperSummaryWithID> papers = new ArrayList<>();
        for (Submission submission : submissions) {
            PaperSummaryWithID paperSummaryWithID = new PaperSummaryWithID();
            paperSummaryWithID.setPaperID(submission.getSubmissionId());
            paperSummaryWithID.setTitle(submission.getTitle());
            paperSummaryWithID.setAbstractSection(submission.getAbstract());
            papers.add(paperSummaryWithID);
        }
        return papers;
    }
}
