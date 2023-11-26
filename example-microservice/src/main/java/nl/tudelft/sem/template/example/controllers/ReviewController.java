package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @GetMapping("/readReview")
    public ResponseEntity<Review> readReview(UUID UserID, UUID PaperID, UUID ReviewerID) {
        return null;
    }





}
