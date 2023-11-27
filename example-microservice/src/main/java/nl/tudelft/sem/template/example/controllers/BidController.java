package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.Bid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controls the bids
 */

@RestController
@RequestMapping("/paper/{paperID}/bid")
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
     * Deletes a bid
     * TODO: add failed deletes
     * @return OK status if the deletion was successful
     */
    @DeleteMapping("/delete")
    public ResponseEntity deleteBid(@PathVariable UUID paperID, @RequestBody UUID userID) {
        return ResponseEntity.ok().build();

    }

    /**
     * Creates (or edits) a bid from a user to a paper
     * @return OK status if the bid was successful
     */
    @PutMapping("/create")
    public ResponseEntity<UUID> bidForPaper(@PathVariable UUID paperID, @RequestBody UUID userID) {
        return ResponseEntity.ok(UUID.fromString(""));
    }
}
