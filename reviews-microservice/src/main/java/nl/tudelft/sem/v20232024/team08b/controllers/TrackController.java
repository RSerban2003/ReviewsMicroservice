package nl.tudelft.sem.v20232024.team08b.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Date;
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

  @Operation(summary = "Get the summary stats of a track",
          description = "Returns the numbers of accepted, rejected and not-yet-decided papers."
  )
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200"),
          @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
          @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
          @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @GetMapping(path = "/analytics", produces = "application/json")
  @ResponseBody
  public ResponseEntity<TrackAnalytics> getAnalytics(@RequestParam Long userID,
                                                @PathVariable Long conferenceID,
                                                @PathVariable Long trackID) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @Operation(summary = "Get the summary stats of a track",
          description = "Returns the numbers of accepted, rejected and not-yet-decided papers."
  )
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema())}),
          @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
          @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
          @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @PostMapping(path = "/bidding-deadline", produces = "application/json")
  @ResponseBody
  public ResponseEntity setBiddingDeadline(@RequestParam Long userID,
                                           @PathVariable Long conferenceID,
                                           @PathVariable Long trackID,
                                           @RequestBody Date newDeadline) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}
