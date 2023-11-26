package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.Bid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controls the bids
 */

@RestController("/bid")
public class BidController {


    private final transient AuthManager authManager;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public BidController(AuthManager authManager) {
        this.authManager = authManager;
    }

    /**
     * Gets example by id.
     *
     * @return the example found in the database with the given id
     */
    @DeleteMapping("/deleteBid")
    public ResponseEntity<Bid> deleteBid(UUID paperId, UUID userId) {
        return ResponseEntity.ok(new Bid(null, null, null));

    }

    @PutMapping("/bidForPaper")
    public ResponseEntity<Bid> bidForPaper(UUID paperId, UUID userId, Enum status) {
        return ResponseEntity.ok(new Bid(null, null, null));

    }

    @PutMapping("/editAssignment")
    public ResponseEntity<Bid> editAssignment(UUID paperId, UUID oldUserId, UUID newUserId) {
        return ResponseEntity.ok(new Bid(null, null, null));
    }

}
