package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController("/confidentialComment")
public class ConfidentialCommentCntrl {
    private final transient AuthManager authManager;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public ConfidentialCommentCntrl(AuthManager authManager) {
        this.authManager = authManager;
    }


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
