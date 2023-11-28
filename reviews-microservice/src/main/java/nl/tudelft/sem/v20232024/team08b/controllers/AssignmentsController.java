package nl.tudelft.sem.v20232024.team08b.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import nl.tudelft.sem.v20232024.team08b.domain.Paper;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.dtos.ReviewSubmission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/papers/{paperID}")
public class AssignmentsController {
    @PostMapping(path = "/assign-manual", produces = "application/json"zzzzzz)
    public ResponseEntity<Void> assignManual(@PathVariable Long paperID, @RequestBody Long userID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping("/assign-auto")
    public ResponseEntity<Void> assignAuto(@PathVariable Long paperID, @RequestBody Long userID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping("/finalize")
    public ResponseEntity<Void> finalize(@PathVariable Long paperID, @RequestBody Long userID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/assignments")
    public ResponseEntity<List<String>> assignments(@PathVariable Long paperID, @RequestBody Long userID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }



}
