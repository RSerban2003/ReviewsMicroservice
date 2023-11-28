package nl.tudelft.sem.v20232024.team08b.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
//import nl.tudelft.sem.v20232024.team08b.domain.Paper;
//import nl.tudelft.sem.v20232024.team08b.domain.Review;
//import nl.tudelft.sem.v20232024.team08b.dtos.ReviewSubmission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/papers/{paperID}/assign")
@Tag(name = "Assignments", description = "Operations for assigning reviewers to papers.")
public class AssignmentsController {


    @Operation(summary = "Manually assigns reviewer",
            description = "Manually assigns reviewer to a specific paper." +
                            "This can only be done by the chair and will respond with a 403 error if requester is not a valid chair"
    )
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @PostMapping("/assign-manual")
    public ResponseEntity<Void> assignManual(@PathVariable Long paperID, @RequestBody Long userID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Automatically assigns reviewers",
            description = "Automatically assigns reviewer to a specific paper." +
                    "This can only be done by the chair and will respond with a 403 error if requester is not a valid chair"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @PostMapping("/assign-auto")
    public ResponseEntity<Void> assignAuto(@PathVariable Long paperID, @RequestBody Long userID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Finalise reviewers",
            description = "Finalises the assignment of reviewers." +
                            "This can only be done by the chair and will respond with a 403 error if requester is not a valid chair"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @SuppressWarnings("PMD.FinalizeOverloaded")
    @PostMapping("/finalize")
    public ResponseEntity<Void> finalize(@PathVariable Long paperID, @RequestBody Long userID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Get current assignments",
            description = "Responds with a list of reviewers for a specific paper." +
                            "This can only be done by the chair and will respond with a 403 error if requester is not a valid chair"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/assignments")
    public ResponseEntity<List<String>> assignments(@PathVariable Long paperID, @RequestBody Long userID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
