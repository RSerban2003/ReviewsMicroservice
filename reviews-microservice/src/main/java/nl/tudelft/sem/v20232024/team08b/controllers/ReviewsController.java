package nl.tudelft.sem.v20232024.team08b.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import nl.tudelft.sem.v20232024.team08b.domain.ConfidentialComment;
import nl.tudelft.sem.v20232024.team08b.dtos.PaperPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.ReviewDTO;
import nl.tudelft.sem.v20232024.team08b.dtos.ReviewSubmission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/papers/{paperID}")
@Tag(name = "Reviews", description = "Operations to deal with reviews: reading them, submitting, commenting, etc")
public class ReviewsController {
    @Operation(summary = "Gets a review",
            description = "Responds with the review of a specific paper (paperID), reviewed by user (userID). " +
                    "It does NOT contain the confidential comments. They have to be requested separately."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of the review"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper or reviewer was not found.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @GetMapping(path = "/reviews/by-reviewer/{reviewerID}", produces = "application/json")
    public ResponseEntity<ReviewDTO> read(
        @RequestParam Long requesterID,
        @PathVariable Long reviewerID,
        @PathVariable Long paperID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Submit a review",
        description = "The requester submits (or resubmits) a review to a specific paper. " +
            "The requester must be a valid reviewer and will be identified using userID and reviewerID. " +
            "Once all the reviewers for a paper have submitted a review for that paper, the Discussion phase for that paper " +
            "begins and reviewers can see each others reviews and write comments on them. " +
            "Once the discussion phase has begun, all of the reviewers have to resubmit their reviews (even if " +
            "the reviews are exactly the same), in order for the Chair to be able to approve the reviews."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Review successfully submitted"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper or reviewer was not found.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @PutMapping(path = "/reviews", consumes = {"application/json"})
    public ResponseEntity<Void> submit(
        @RequestBody ReviewSubmission review,
        @RequestParam Long requesterID,
        @PathVariable Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Get the list of a reviewers for a given paper",
        description = "Responds with list of reviewers assigned to that paper. " +
            "The requester must be a chair of the track that the paper is in, or a reviewer " +
            "also assigned to the given paper."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of the list of reviewers"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper was not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @ResponseBody
    @GetMapping(path = "/reviewers", produces = "application/json")
    public ResponseEntity<List<Long>> getReviewers(
        @RequestParam Long requesterID,
        @PathVariable Long paperID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Get the review phase of a paper",
        description = "Responds with how far along a paper is in the review process. " +
            "The requester must be a chair of the track the paper is in, " +
            "or a reviewer assigned to that paper."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of the paper review phase"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper was not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @ResponseBody
    @GetMapping(path = "/reviews/phase", produces = "application/json")
    public ResponseEntity<PaperPhase> getPhase(
        @RequestParam Long requesterID,
        @PathVariable Long paperID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "End the discussion phase for a paper",
        description = "The requester must be a chair of the track the paper is in"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of the list of reviewers"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper was not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @ResponseBody
    @PostMapping(path = "/reviews/finalization")
    public ResponseEntity<Void> finalize(
        @RequestParam Long requesterID,
        @PathVariable Long paperID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Posts a confidential comment",
            description = "Posts a confidential comment for a review of a specific paper using userID and reviewerID." +
                " The requester must be a chair of the track that the paper is in, or a reviewer " +
                "also assigned to the given paper."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Confidential comment successfully posted"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester is not a valid chair or reviewer.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper or reviewer was not found.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @PostMapping(path = "/reviews/by-reviewer/{reviewerID}/confidential-comments", consumes = "application/json")
    public ResponseEntity<Void> submitConfidentialComment(@RequestParam Long userID,
                                                    @PathVariable Long reviewerID,
                                                    @PathVariable Long paperID,
                                                    @RequestBody @Schema(description = "Comment", example = "Some comment")
                                                    String comment) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Gets the confidential comments",
            description = "Gets the confidential comments for a paper, if the user is a chair or a reviewer."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Confidential comment successfully posted"),
            @ApiResponse(responseCode = "403", description = "Forbidden. The requester is not a valid chair or reviewer.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found. The requested paper or reviewer was not found.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @GetMapping(path = "/reviews/by-reviewer/{reviewerID}/confidential-comments", produces = "application/json")
    public ResponseEntity<List<ConfidentialComment>>
                    getConfidentialComments(@RequestParam Long requesterID,
                                            @PathVariable Long reviewerID,
                                            @PathVariable Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
