package nl.tudelft.sem.v20232024.team08b.controllers;

import nl.tudelft.sem.v20232024.team08b.dtos.PaperAbstract;
import nl.tudelft.sem.v20232024.team08b.dtos.PaperState;
import nl.tudelft.sem.v20232024.team08b.dtos.WholePaper;
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
