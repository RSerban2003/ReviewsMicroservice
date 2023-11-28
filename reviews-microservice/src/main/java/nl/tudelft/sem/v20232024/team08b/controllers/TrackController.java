package nl.tudelft.sem.v20232024.team08b.controllers;

import java.util.Date;

import nl.tudelft.sem.v20232024.team08b.dtos.Analytics;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conferences/{conferenceID}/tracks/{trackID}")
public class TrackController {

  /**
   * Method that returns the analytics of the track (papers accepted,
   * papers rejected and papers unknown)
   * @param userID ID of user
   * @param trackID ID of track
   * @return analytics of the track
   */
  @GetMapping("/analytics")
  public ResponseEntity<Analytics> getAnalytics(@RequestParam Long userID,
                                                @PathVariable Long conferenceID,
                                                @PathVariable Long trackID) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  /**
   * Method to change deadline of track
   * @param userID user id
   * @param conferenceID conference id
   * @param trackID track id
   * @param newDeadline new deadline
   * @return a response entity stating if it is successful or not
   */
  @PostMapping("/bidding-deadline")
  public ResponseEntity<Void> setBiddingDeadline(@RequestParam Long userID,
                                                 @PathVariable Long conferenceID,
                                                 @PathVariable Long trackID,
                                                 @RequestBody Date newDeadline) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}
