package nl.tudelft.sem.v20232024.team08b.controllers;

import nl.tudelft.sem.v20232024.team08b.domain.Review;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController("/confidentialComment")
public class ConfidentialCommentCntrl {
    /**
     * @param paperID the ID for the paper
     * @param userID the ID for the user
     * @param reviewerID the ID for the reviewer
     * @return the saved review object
     */
    @PostMapping("/submit/{paperID}")
    public ResponseEntity<Review> submitConfidentialComment(@PathVariable String paperID, @RequestBody UUID userID, @RequestBody UUID reviewerID) {
        return null;
    }

}
