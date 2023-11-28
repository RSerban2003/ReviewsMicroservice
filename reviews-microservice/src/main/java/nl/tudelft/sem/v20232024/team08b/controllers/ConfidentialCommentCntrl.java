package nl.tudelft.sem.v20232024.team08b.controllers;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paper/{paperID}/confidentialComment")
public class ConfidentialCommentCntrl {
    /**
     * @param paperID the ID for the paper
     * @param comment stores the reviewer ID and the comment itself
     * @return the saved review object
     */
    @PostMapping("")
    public ResponseEntity submitConfidentialComment(@PathVariable UUID paperID,
                                                    @RequestBody nl.tudelft.sem.template.example.reponses.ConfidentialCommentSubmission comment) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
