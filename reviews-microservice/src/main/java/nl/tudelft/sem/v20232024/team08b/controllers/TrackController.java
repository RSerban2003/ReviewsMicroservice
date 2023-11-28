package nl.tudelft.sem.v20232024.team08b.controllers;

import java.util.Date;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import nl.tudelft.sem.v20232024.team08b.dtos.TrackAnalytics;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conferences/{conferenceID}/tracks/{trackID}")
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
  @GetMapping("/analytics")
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
          @ApiResponse(responseCode = "201"),
          @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
          @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
          @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
  })
  @PostMapping("/bidding-deadline")
  @ResponseBody
  public ResponseEntity setBiddingDeadline(@RequestParam Long userID,
                                           @PathVariable Long conferenceID,
                                           @PathVariable Long trackID,
                                           @RequestBody Date newDeadline) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}
