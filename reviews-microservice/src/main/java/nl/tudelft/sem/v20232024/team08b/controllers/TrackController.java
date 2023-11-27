package nl.tudelft.sem.v20232024.team08b.controllers;

import java.util.Date;
import java.util.UUID;

import nl.tudelft.sem.v20232024.team08b.dtos.Analytics;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/Track")
public class TrackController {
  /*private final Track repo;
*/
  /**
   * Constructor Method for trackController
   * @param repo of the controller
   */
  /*public TrackController(Track repo) {
    this.repo = repo;
  }
*/
  /**
   * Method that returns the analytics of the track
   * (papers accepted, papers rejected and papers unknown)
   * @param UserId ID of user
   * @param TrackID ID of track
   * @return analytics of the track
   */
  @GetMapping(path = {"", "/getAnalytics"})
  public ResponseEntity<Analytics> getAnalytics(@RequestBody  UUID UserId,
                                                @RequestBody UUID TrackID) {
    return null;
  }

  /**
   * Method to change deadline of track
   * @param UserId user id
   * @param TrackID track id
   * @param DeadLine new deadline
   * @return a reponseEntity stating if it is successful or not
   */
  @GetMapping(path = {"", "/setBiddingDeadLine"})
  public ResponseEntity<Void> getAnalytics(@RequestBody  UUID UserId,
                                           @RequestBody UUID TrackID,
                                           @RequestBody Date DeadLine) {

    return null;
  }





}
