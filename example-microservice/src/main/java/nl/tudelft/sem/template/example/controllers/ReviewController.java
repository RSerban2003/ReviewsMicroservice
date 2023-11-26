package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController("/review")
public class ReviewController {
    private final transient AuthManager authManager;

        /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public ReviewController(AuthManager authManager) {
        this.authManager = authManager;
    }


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
