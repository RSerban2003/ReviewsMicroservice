package nl.tudelft.sem.v20232024.team08b.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.tudelft.sem.v20232024.team08b.domain.Paper;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.dtos.ReviewSubmission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/papers/{paperID}/assign")
@Tag(name = "Assignments", description = "Operations for assigning reviewers to papers.")
public class AssignmentsController {
    @Operation(summary = "Manually assign reviewers",
            description = "Manually assigns reviewer to a specific paper." +
                            " This can only be done by the chair and will respond with a 403 error if requester is not a valid chair." +
                            " At least 3 reviewers must be assigned to a paper."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reviewer successfully assigned to the paper.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden. You are not allowed to assign reviewers.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found. The specified paper or user does not exist.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "409", description = "Conflict. The reviewer has already been assigned to this paper.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @PutMapping(path="/assign-manual", produces = "application/json")
    public ResponseEntity<Void> assignManual(@PathVariable Long paperID, @RequestParam Long requesterID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Automatically assign reviewers",
            description = "Automatically assigns reviewer to a specific paper." +
                    " This can only be done by the chair and will respond with a 403 error if requester is not a valid chair. " +
                    "At least 3 reviewers must be assigned to a paper."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reviewer successfully assigned to the paper.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden. You are not allowed to assign reviewers.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found. The specified paper or user does not exist.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "409", description = "Conflict. The reviewer has already been assigned to this paper.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @PutMapping(path="/assign-auto", produces = "application/json")
    public ResponseEntity<Void> assignAuto(@PathVariable Long paperID, @RequestParam Long requesterID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Get current assignments",
            description = "Responds with a list of reviewers for a specific paper." +
                            " This can only be done by the chair and will respond with a 403 error if requester is not a valid chair."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned the list of reviewers assigned to this paper.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden. You are not allowed to view the reviewers.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found. The specified paper or user does not exist.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @GetMapping(path = "/assignments", produces = "application/json")
    public ResponseEntity<List<Long>> assignments( @PathVariable Long paperID, @RequestParam Long requesterID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
