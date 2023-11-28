package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.Paper;
import nl.tudelft.sem.template.example.reponses.PaperAbstract;
import nl.tudelft.sem.template.example.reponses.PaperState;
import nl.tudelft.sem.template.example.reponses.WholePaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controls the papers
 */
@RestController
@RequestMapping("/paper/{paperID}")
public class PaperController {

    private final transient AuthManager authManager;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public PaperController(AuthManager authManager) {
        this.authManager = authManager;
    }

    /**
     * Gets the paper title and abstract
     *
     * @return the paper, its ID and abstract
     */
    @GetMapping("/titleAndAbstract")
    public ResponseEntity<PaperAbstract> getTitleAndAbstract(@PathVariable UUID paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Returns the whole paper to read
     *
     * @return the paper for the reviewers to read
     */
    @GetMapping("")
    public ResponseEntity<WholePaper> readPaper(@PathVariable UUID paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/state")
    public ResponseEntity<PaperState> getState(@PathVariable UUID paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
