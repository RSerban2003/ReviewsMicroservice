package nl.tudelft.sem.v20232024.team08b.controllers;

import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.api.TracksAPI;
import nl.tudelft.sem.v20232024.team08b.application.TracksService;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperSummaryWithID;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackAnalytics;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.List;

@RestController
public class TracksController implements TracksAPI {
    private final TracksService tracksService;

    /**
     * Default constructor for the controller.
     *
     * @param tracksService the respective service to inject
     */
    @Autowired
    public TracksController(TracksService tracksService) {
        this.tracksService = tracksService;
    }

    /**
     * Returns all the papers in the given track of a conference.
     *
     * @param requesterID the ID of the requesting user
     * @param conferenceID the ID of the conference
     * @param trackID the ID of the track
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<List<PaperSummaryWithID>> getPapers(Long requesterID,
                                                              Long conferenceID,
                                                              Long trackID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Returns the numbers of accepted, rejected and not-yet-decided papers.
     *
     * @param requesterID the ID of the requesting user
     * @param conferenceID the ID of the conference
     * @param trackID the ID of the track
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<TrackAnalytics> getAnalytics(Long requesterID,
                                                       Long conferenceID,
                                                       Long trackID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Changes or sets the bidding deadline of a track.
     *
     * @param requesterID the ID of the requesting user
     * @param conferenceID the ID of the conference
     * @param trackID the ID of the track
     * @param newDeadline the new deadline to be set
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<Void> setBiddingDeadline(Long requesterID,
                                                   Long conferenceID,
                                                   Long trackID,
                                                   Date newDeadline) {
        try {
            tracksService.setBiddingDeadline(
                    requesterID,
                    conferenceID,
                    trackID,
                    newDeadline
            );
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets the bidding deadline of a track.
     *
     * @param requesterID the ID of the requesting user
     * @param conferenceID the ID of the conference
     * @param trackID the ID of the track
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<Date> getBiddingDeadline(Long requesterID,
                                                   Long conferenceID,
                                                   Long trackID) {
        try {
            Date biddingDeadline = tracksService.getBiddingDeadline(
                    requesterID,
                    conferenceID,
                    trackID
            );
            return ResponseEntity.ok(biddingDeadline);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds with the review phase of the track.
     *
     * @param requesterID the ID of the requesting user
     * @param conferenceID the ID of the conference
     * @param trackID the ID of the track
     * @return response entity with the result
     */
    @Override
    public ResponseEntity<TrackPhase> getPhase(Long requesterID,
                                               Long conferenceID,
                                               Long trackID) {
        try {
            TrackPhase trackPhase = tracksService.getTrackPhase(
                    requesterID,
                    conferenceID,
                    trackID
            );
            return ResponseEntity.ok(trackPhase);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
