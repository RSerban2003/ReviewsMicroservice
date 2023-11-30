package nl.tudelft.sem.v20232024.team08b.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conferences/{conferenceID}/tracks/{trackID}/{paperID}")
@Tag(name = "Assignments", description = "Operations for assigning reviewers to papers.")
public class AssignmentsController {
    @Operation(summary = "Manually assign reviewers",
            description = "Manually assigns reviewer to a specific paper."+
                    "At least 3 reviewers must be assigned to a paper."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reviewer successfully assigned to the paper.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden. You are not allowed to assign reviewers. Only chairs for track can do that", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found. The specified paper or user does not exist.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "409", description = "Conflict. There is a Conflict of Interest and the reviewer cannot " +
                "review this paper; or the reviewer has already been assigned to this paper.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @PostMapping(path = "/{reviewerID}")
    public ResponseEntity<Void> assignManual(
        @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of a track assignment belongs to") Long trackID,
        @PathVariable @Parameter(description = "The ID of a conference assignment belongs to")Long conferenceID,
        @PathVariable @Parameter(description = "The ID of a user to assign as a reviewer")Long reviewerID,
        @PathVariable @Parameter(description = "The ID of a paper to assign reviewers") String paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Automatically assign the reviewers",
            description = "Automatically assigns a reviewer to a specific paper. " +
                "At least 3 reviewers must be assigned to each paper, such that each reviewer in the track " +
                "has a similar amount of reviews assigned to them."
    )
    @ApiResponses(value = {

            @ApiResponse(responseCode = "201", description = "Reviewers successfully assigned to the track.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden. You are not allowed to assign reviewers. Only the chairs for tracks are allowed.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found. The specified track or user does not exist.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "409", description = "Conflict. There are no reviewers left to automatically assign", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @PutMapping(path = "/automatic")
    public ResponseEntity<Void> assignAuto(
            @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
            @PathVariable @Parameter(description = "The ID of a conference assignment belongs to") String conferenceID,
            @PathVariable @Parameter(description = "The ID of a track assignment belongs to") String trackID,
            @PathVariable @Parameter(description = "The ID of a paper to assign reviewers") String paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Finalise reviewers",
            description = "Finalises the assignment of reviewers, so they can no longer be changed manually or automatically. " +
                    "This can only be done by the chair and will respond with a 403 error if requester is not a valid chair " +
                    "or a reviewer in this track."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reviewers has been successfully finalized to this paper.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden. You are not allowed to finalize the assignments. You are either not a chair, or less than 3 reviewers have been assigned to a track", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found. The specified paper or user does not exist.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @PostMapping(path = "/finalization")
    public ResponseEntity<Void> finalization(
            @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
            @PathVariable @Parameter(description = "The ID of a conference assignment belongs to") String conferenceID,
            @PathVariable @Parameter(description = "The ID of a track assignment belongs to") String trackID,
            @PathVariable @Parameter(description = "The ID of a paper to finalize the assignments") String paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Get current assignments",
            description = "Responds with a list of reviewers for a specific paper."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned the list of reviewers assigned to this paper.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden. You are not allowed to view the reviewers. Only a chair or a reviewer in this track can do that", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found. The specified paper or user does not exist.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<List<Long>> assignments(
            @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
            @PathVariable @Parameter(description = "The ID of a conference the assignments belong to") String conferenceID,
            @PathVariable @Parameter(description = "The ID of a track assignments belong to") String trackID,
            @PathVariable @Parameter(description = "The ID of a paper assignments belong to") Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Removes a reviewer from a paper",
            description = "Removes a reviewer previously assigned to a paper."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed the reviewer for this paper.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden. You are not allowed to remove a reviewer. Only chairs can do that.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found. The specified paper or user does not exist.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @DeleteMapping(path = "/{reviewerID}", consumes = {"application/json"})
    public ResponseEntity<Void> remove(
            @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
            @PathVariable @Parameter(description = "The ID of a paper reviewer belongs to") Long paperID,
            @PathVariable @Parameter(description = "The ID of a reviewer to remove") Long reviewerID,
            @PathVariable @Parameter(description = "The ID of a conference reviewer belongs to") String conferenceID,
            @PathVariable @Parameter(description = "The ID of a track reviewer belongs to") String trackID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
