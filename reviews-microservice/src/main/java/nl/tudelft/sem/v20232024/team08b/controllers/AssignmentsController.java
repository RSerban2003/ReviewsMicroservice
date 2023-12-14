package nl.tudelft.sem.v20232024.team08b.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import nl.tudelft.sem.v20232024.team08b.application.AssignmentsService;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperSummaryWithID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
@Tag(name = "Assignments", description = "Operations for assigning reviewers to papers.")
public class AssignmentsController {
    private final AssignmentsService assignmentsService;

    /**
     * Default constructor for the controller.
     *
     * @param assignmentsService the respective service to inject
     */
    @Autowired
    public AssignmentsController(AssignmentsService assignmentsService) {
        this.assignmentsService = assignmentsService;
    }

    @Operation(summary = "Manually assign reviewers",
        description = "Manually assigns reviewer to a specific paper." +
            "At least 3 reviewers must be assigned to a paper."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reviewer successfully assigned to the paper.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "403", description = "Forbidden. You are not allowed to assign reviewers." +
            " Only chairs for track can do that", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The specified paper " +
            "or user does not exist.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "409", description =
            "Conflict. There is a Conflict of Interest and the reviewer cannot " +
                "review this paper; or the reviewer has already been assigned to this paper.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server " +
            "error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @PostMapping(path = "/papers/{paperID}/assignees/{reviewerID}")
    public ResponseEntity<Void> assignManual(
        @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of a user to assign as a reviewer") Long reviewerID,
        @PathVariable @Parameter(description = "The ID of a paper to assign") Long paperID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Automatically assign the reviewers",
        description = "Automatically assigns a reviewer to a specific paper. " +
            "At least 3 reviewers will be assigned to each paper (taking into account the manual assignments as well), " +
            "such that each reviewer in the track has a similar amount of reviews assigned to them."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reviewers successfully assigned to the track.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "403", description = "Forbidden. You are not allowed to assign reviewers. " +
            "Only the chairs for tracks are allowed.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The specified track or user " +
            "does not exist.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "409", description = "Conflict. Automatic assignment not possible. " +
            "There must be at least one reviewer in the track.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error." +
            " An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @PutMapping(path = "/conferences/{conferenceID}/tracks/{trackID}/automatic")
    public ResponseEntity<Void> assignAuto(
        @RequestParam @Parameter(description = "The ID of the user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of the conference the track belongs to") Long conferenceID,
        @PathVariable @Parameter(description = "The ID of the track for which to do the automatic assignment") Long trackID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Finalise reviewers",
        description = "Finalises the assignment of reviewers, so they can no longer be changed manually or automatically. "
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reviewers has been successfully finalized " +
            "to this paper.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "403", description = "Forbidden. You are not allowed to finalize the assignments." +
            " You must be a chair of the track.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found." +
            " The specified paper or user does not exist.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "409", description = "Conflict." +
            " There must be at least 3 reviewers assigned to each paper to finalize the assignments.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @PostMapping(path = "/conferences/{conferenceID}/tracks/{trackID}/finalization")
    public ResponseEntity<Void> finalization(
        @RequestParam @Parameter(description = "The ID of the user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of the conference the track belongs to") Long conferenceID,
        @PathVariable @Parameter(description = "The ID of the track for which the assignments should be finalized")
        Long trackID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Get current assignments for a paper",
        description = "Responds with a list of reviewer IDs for a specific paper."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully returned the list of reviewers assigned " +
            "to this paper.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "403", description = "Forbidden. You are not allowed to view the reviewers. " +
            "Only a chair or a reviewer in this track can do that", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. " +
            "The specified paper or user does not exist.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @GetMapping(path = "/papers/{paperID}/assignees", produces = "application/json")
    public ResponseEntity<List<Long>> assignments(
        @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of a paper assignments belong to") Long paperID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Removes a reviewer from a paper",
        description = "Removes a reviewer previously assigned to a paper."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully removed the reviewer for this paper.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "403", description = "Forbidden. You are not allowed to remove a reviewer. " +
            "Only chairs can do that.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. " +
            "The specified paper or user does not exist, or the user is not assigned to this paper.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @DeleteMapping(path = "/papers/{paperID}/assignees/{reviewerID}", consumes = {"application/json"})
    public ResponseEntity<Void> remove(
        @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of a paper reviewer belongs to") Long paperID,
        @PathVariable @Parameter(description = "The ID of a reviewer to remove") Long reviewerID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Gets all papers a reviewer (the requester) is assigned to.",
        description = "Responds with a list of papers a user is assigned to in all tracks. " +
            "This endpoint can be called by any reviewer."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success."),
        @ApiResponse(responseCode = "404", description = "Not Found. Such user does not exist", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @GetMapping(path = "/papers/by-reviewer", produces = "application/json")
    @Tag(name = "Reviews")
    public ResponseEntity<List<PaperSummaryWithID>> getAssignedPapers(
        @RequestParam @Parameter(description = "The ID of the user making the request.") Long requesterID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
