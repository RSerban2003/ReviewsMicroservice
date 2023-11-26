package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.Paper;
import nl.tudelft.sem.template.example.reponses.PaperAbstract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controls the papers
 */
@RestController
@RequestMapping("/paper")
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
    @GetMapping("/readPaper")
    public ResponseEntity<PaperAbstract> readPaper() {
        return ResponseEntity.ok(new PaperAbstract());
    }



}
