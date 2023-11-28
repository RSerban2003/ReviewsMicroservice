package nl.tudelft.sem.v20232024.team08b.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Date;
import java.util.List;
import nl.tudelft.sem.v20232024.team08b.dtos.TrackAnalytics;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conferences/{conferenceID}/tracks/{trackID}")
@Tag(name = "Conference Tracks", description = "Operations for dealing with conference tracks: getting summaries and setting deadlines")
public class TrackController {

  @Operation(summary = "Get the papers for track",
      description = "Returns all the papers assigned to the track of a conference."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful retrieval of papers in given track of given conference"),
      @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {@Content(schema = @Schema())}),
      @ApiResponse(responseCode = "404", description = "Not Found. The requested track or conference was not found.", content = {@Content(schema = @Schema())}),
      @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
  })
  @ResponseBody
  @GetMapping(path = "/papers", produces = "application/json")
  public ResponseEntity<List<Long>> getPapers(@RequestParam Long requesterID,
                                              @PathVariable Long conferenceID,
                                              @PathVariable Long trackID){
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
  @Operation(summary = "Get the summary stats of a track",
          description = "Returns the numbers of accepted, rejected and not-yet-decided papers."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful retrieval of track summary stats"),
      @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {@Content(schema = @Schema())}),
      @ApiResponse(responseCode = "404", description = "Not Found. The requested track was not found.", content = {@Content(schema = @Schema())}),
      @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
  })
  @GetMapping(path = "/analytics", produces = "application/json")
  @ResponseBody
  public ResponseEntity<TrackAnalytics> getAnalytics(@RequestParam Long requesterID,
                                                @PathVariable Long conferenceID,
                                                @PathVariable Long trackID) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @Operation(summary = "Changes the bidding deadline",
          description = "Changes the bidding deadline to the one provided in the body"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful set of bidding deadline", content = {@Content(schema = @Schema())}),
      @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {@Content(schema = @Schema())}),
      @ApiResponse(responseCode = "404", description = "Not Found. The requested track was not found.", content = {@Content(schema = @Schema())}),
      @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
  })
  @PostMapping(path = "/bidding-deadline", consumes = "application/json")
  @ResponseBody
  public ResponseEntity<Void> setBiddingDeadline(@RequestParam Long userID,
                                           @PathVariable Long conferenceID,
                                           @PathVariable Long trackID,
                                           @RequestBody Date newDeadline) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}
