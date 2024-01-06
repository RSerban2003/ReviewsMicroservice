package nl.tudelft.sem.v20232024.team08b.application.phase;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.domain.Paper;
import nl.tudelft.sem.v20232024.team08b.domain.Track;
import nl.tudelft.sem.v20232024.team08b.domain.TrackID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.repos.ExternalRepository;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class TrackPhaseCalculator {
    private final PaperRepository paperRepository;
    private final TrackRepository trackRepository;
    private final ExternalRepository externalRepository;
    private final PaperPhaseCalculator paperPhaseCalculator;

    /**
     * Default constructor for the phase calculator.
     *
     * @param paperRepository repository storing the papers
     * @param trackRepository repository storing the tracks
     * @param externalRepository repository storing everything outside of
     *                           this microservice
     * @param paperPhaseCalculator object that calculates phase of the paper
     */
    @Autowired
    public TrackPhaseCalculator(PaperRepository paperRepository,
                                TrackRepository trackRepository,
                                ExternalRepository externalRepository,
                                PaperPhaseCalculator paperPhaseCalculator) {
        this.paperRepository = paperRepository;
        this.trackRepository = trackRepository;
        this.externalRepository = externalRepository;
        this.paperPhaseCalculator = paperPhaseCalculator;
    }

    /**
     * Method that gets bidding deadline of the track and returns it as a UNIX
     * timestamp, as Integer.
     * If the deadline is not set, returns NULL.
     * TODO: implement this method as part of bidding deadline issue.
     *
     * @param conferenceID the ID of the conference the track is in
     * @param trackID the ID of the track
     * @return bidding deadline as an integer
     */
    public Long getBiddingDeadlineAsLong(Long conferenceID,
                                         Long trackID) {
        return 1L;
    }

    /**
     * Checks if all papers in a given track have been finalized.
     *
     * @param conferenceID the ID of the conference the track is in
     * @param trackID the ID of the track
     * @return true, iff all papers in a given track have been finalized
     */
    public Boolean checkIfAllPapersFinalized(Long conferenceID,
                                             Long trackID) throws NotFoundException {
        // Check if such track exists
        externalRepository.getTrack(conferenceID, trackID);

        // Get the track
        Optional<Track> trackOptional = trackRepository.findById(
                new TrackID(conferenceID, trackID)
        );
        if (trackOptional.isEmpty()) {
            // In theory, this method should be called only after reviews have
            // been added, so this should not be reached.
            return false;
        }

        // Get the papers of the track
        List<Paper> papers = trackOptional.get().getPapers();

        // Check if all the papers are finalized
        Boolean ret = true;
        for (var paper : papers) {
            Long paperID = paper.getId();
            ret &= paperPhaseCalculator.getPaperPhase(paperID) == PaperPhase.REVIEWED;
        }
        return ret;
    }

    /**
     * Calculates the current phase of a track.
     * The logic of calculation is the following:
     * - If the submission deadline has not yet passed -> SUBMITTING
     * - else, if the bidding deadline is not set or has not passed -> BIDDING
     * - else, if the reviewers have not yet been assigned to papers -> ASSIGNING
     * - else, if all papers of the track have not been finalized -> REVIEWING
     * - finally, if all papers have been finalized -> FINAL
     *
     * @param conferenceID the ID of the conference the track is in
     * @param trackID the ID of the track
     * @return the current phase of the track
     */
    public TrackPhase getTrackPhase(Long conferenceID,
                                    Long trackID) throws NotFoundException {

        // Get the track from external repository. Such track should always
        // exist, since we verified, so the following line will not throw.
        nl.tudelft.sem.v20232024.team08b.dtos.users.Track track =
                externalRepository.getTrack(conferenceID, trackID);

        // Get the current timestamp
        Long currentTime = Instant.now().toEpochMilli();

        // Check if submission deadline has not yet passed
        Long submissionDeadline = Long.valueOf(track.getDeadline());
        if (currentTime <= submissionDeadline) {
            return TrackPhase.SUBMITTING;
        }

        // Get the bidding deadline
        Long biddingDeadline = getBiddingDeadlineAsLong(conferenceID, trackID);
        if (biddingDeadline == null || currentTime <= biddingDeadline) {
            return TrackPhase.BIDDING;
        }

        // Check if the reviewers haven't yet been assigned
        Boolean reviewersAssigned = paperPhaseCalculator
                .checkIfReviewersAreAssignedToTrack(conferenceID, trackID);
        if (!reviewersAssigned) {
            return TrackPhase.ASSIGNING;
        }

        // Check if all papers in a track have been finalized
        Boolean allPapersFinalized = checkIfAllPapersFinalized(conferenceID, trackID);
        if (!allPapersFinalized) {
            return TrackPhase.REVIEWING;
        }

        return TrackPhase.FINAL;
    }
}
