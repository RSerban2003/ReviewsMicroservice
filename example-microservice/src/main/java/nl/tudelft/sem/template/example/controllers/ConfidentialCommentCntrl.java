package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * @param PaperID the ID for the paper
     * @param UserID the ID for the user
     * @return the saved review object
     */
    @PostMapping("/submit/{UserID}/{PaperID}")
    public ResponseEntity<Review> submitConfidentialComment(@PathVariable String PaperID, @PathVariable String UserID) {
        return null;
    }

}
