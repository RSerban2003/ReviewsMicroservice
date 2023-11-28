package nl.tudelft.sem.v20232024.team08b.controllers;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.dtos.ReviewSubmission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController("/papers/{paperID}/reviews/{reviewerID}")
public class ReviewController {
    /**
     * Allows specific users to read the reviews for a specific paper.
     * @param userID the ID of the user
     * @param reviewerID the ID of the reviewer
     * @param paperID the ID of the paper
     * @return the review object
     */
    @GetMapping("")
    public ResponseEntity<Review> read(@RequestBody UUID userID, @PathVariable UUID reviewerID,
                                       @PathVariable UUID paperID) {
        return ResponseEntity.ok(new Review());
    }

    /**
     * @param review the review object containing the review details and the submitter ID
     * @param reviewerID
     * @param paperID
     * @return a response entity with code 200 if adding  was successful
     */
    @PostMapping("")
    public ResponseEntity submit(@RequestBody ReviewSubmission review, @PathVariable UUID reviewerID,
                                 @PathVariable UUID paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


}
