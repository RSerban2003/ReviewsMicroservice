package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.Review;
import nl.tudelft.sem.template.example.reponses.ConfidentialCommentSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController("/paper/{paperID}/confidentialComment")
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
     * @param comment stores the reviewer ID and the comment itself
     * @return the saved review object
     */
    @PostMapping("")
    public ResponseEntity submitConfidentialComment(@PathVariable UUID paperID,
                                                    @RequestBody ConfidentialCommentSubmission comment) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
