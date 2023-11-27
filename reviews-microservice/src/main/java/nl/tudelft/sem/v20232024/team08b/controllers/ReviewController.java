package nl.tudelft.sem.v20232024.team08b.controllers;

import nl.tudelft.sem.v20232024.team08b.domain.Review;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController("/review")
public class ReviewController {
    /**
     * Allows specific users to read the reviews for a specific paper.
     * @param UserID the ID for users
     * @param PaperID the ID for the paper
     */
    @GetMapping("/read/{UserID}/{PaperID}")
    public void read(@PathVariable UUID UserID, @PathVariable UUID PaperID) {
        return;
    }

    /**
     * Submits a review for a specific paper.
     * @param review the review object containing the review details
     * @return the saved review object
     */
    @PostMapping("/submit")
    public ResponseEntity<Review> submit(@RequestBody Review review) {
        return null;
    }


}
