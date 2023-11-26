package nl.tudelft.sem.template.example.controllers;

import java.util.Date;
import java.util.UUID;
import nl.tudelft.sem.template.example.domain.Track;
import nl.tudelft.sem.template.example.reponses.Analytics;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/Track")
public class TrackController {
  private final Track repo;

  public TrackController(Track repo) {
    this.repo = repo;
  }

  @GetMapping(path = {"", "/getAnalytics"})
  public ResponseEntity<Analytics> getAnalytics(@RequestBody  UUID UserId,
                                                @RequestBody UUID TrackID) {
    return null;
  }
  @GetMapping(path = {"", "/setBiddingDeadLine"})
  public ResponseEntity<Void> getAnalytics(@RequestBody  UUID UserId,
                                           @RequestBody UUID TrackID,
                                           @RequestBody Date DeadLine) {

    return null;
  }





}
