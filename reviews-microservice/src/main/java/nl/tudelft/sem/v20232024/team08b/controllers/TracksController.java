package nl.tudelft.sem.v20232024.team08b.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Date;
import java.util.List;
import nl.tudelft.sem.v20232024.team08b.dtos.PaperSummaryWithID;
import nl.tudelft.sem.v20232024.team08b.dtos.TrackAnalytics;
import nl.tudelft.sem.v20232024.team08b.dtos.TrackPhase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conferences/{conferenceID}/tracks/{trackID}")
@Tag(name = "Conference Tracks", description = "Operations for dealing with conference tracks: getting summaries and setting deadlines")
public class TracksController {

    @Operation(summary = "Get the papers for track",
        description = "Returns all the papers in the given track of a conference."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of papers in given track of given conference"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester must be a chair of the track or a reviewer in the track.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested track or conference was not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @ResponseBody
    @GetMapping(path = "/papers", produces = "application/json")
    public ResponseEntity<List<PaperSummaryWithID>> getPapers(@RequestParam Long requesterID,
                                                              @PathVariable Long conferenceID,
                                                              @PathVariable Long trackID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Get the summary stats of a track",
          description = "Returns the numbers of accepted, rejected and not-yet-decided papers."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of track summary stats"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested track was not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @GetMapping(path = "/analytics", produces = "application/json")
    @ResponseBody
    public ResponseEntity<TrackAnalytics> getAnalytics(@RequestParam Long requesterID,
                                                       @PathVariable Long conferenceID,
                                                       @PathVariable Long trackID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Changes the bidding deadline",
        description = "Changes the bidding deadline to the one provided in the body. If the deadline isn't set, " +
            "it will automatically be set to a few days after the submission deadline for the track. " +
            "Once the bidding deadline passes, the bidding phase of the track automatically ends " +
            "and the review assignments phase begins."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested track or conference were not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "409", description =
            "Conflict. An invalid bidding deadline was given, or the old bidding deadline has already passed." +
                " The new given bidding deadline, must also not have passed already." +
                "must not have already passed.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @PutMapping(path = "/bidding-deadline", consumes = "application/json")
    public ResponseEntity<Void> setBiddingDeadline(@RequestParam Long requesterID,
                                                   @PathVariable Long conferenceID,
                                                   @PathVariable Long trackID,
                                                   @RequestBody Date newDeadline) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Get the bidding deadline",
        description = "Responds with the bidding deadline for the given track. If the deadline hasn't been set, " +
            "it will automatically be set to a few days after the submission deadline for the track. " +
            "Once the bidding deadline passes, the bidding phase of the track automatically ends " +
            "and the review assignments phase begins."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful pull of the bidding deadline", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester must be a chair of the track, or a reviewer in the track.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested track or conference were not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @GetMapping(path = "/bidding-deadline", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Date> getBiddingDeadline(
        @RequestParam Long requesterID,
        @PathVariable Long conferenceID,
        @PathVariable Long trackID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Get the review phase",
        description = "Responds with the review phase of the track. " +
            "Consult with the documentation of the returned enum for more information."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful pull of the review phase"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester must be a chair of the track, or a reviewer in the track.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested track or conference were not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @GetMapping(path = "/phase", produces = "application/json")
    @ResponseBody
    public ResponseEntity<TrackPhase> getPhase(
        @RequestParam @Parameter(description = "ID of the user making the request.") Long requesterID,
        @PathVariable @Parameter(description = "ID of the conference the track is in.") Long conferenceID,
        @PathVariable @Parameter(description = "ID of the track.") Long trackID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
