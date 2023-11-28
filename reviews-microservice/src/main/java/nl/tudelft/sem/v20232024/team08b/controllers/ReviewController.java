package nl.tudelft.sem.v20232024.team08b.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.tudelft.sem.v20232024.team08b.dtos.ReviewDTO;
import nl.tudelft.sem.v20232024.team08b.dtos.ReviewSubmission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/papers/{paperID}/reviews/{reviewerID}")
@Tag(name = "Reviews", description = "Operations to deal with reviews: reading them, submitting, commenting, etc")
public class ReviewController {
    @Operation(summary = "Gets a review",
            description = "Responds with the review of a specific paper using userID and reviewerID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of the review"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper or reviewer was not found.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<ReviewDTO> read(
        @RequestParam Long requesterID,
        @PathVariable Long reviewerID,
        @PathVariable Long paperID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Posts a review",
        description = "The requester submits (or resubmits) a review to a specific paper. " +
            "The requester must be a valid reviewer and will be identified using userID and reviewerID. " +
            "Once all the reviewers for a paper have submitted a review for that paper, the Discussion phase for that paper " +
            "begins and reviewers can see each others reviews and write comments on them."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Review successfully submitted"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper or reviewer was not found.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @PutMapping(path = "", consumes = {"application/json"})
    public ResponseEntity<Void> submit(
        @RequestBody ReviewSubmission review,
        @RequestParam Long requesterID,
        @PathVariable Long paperID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Posts a confidential comment",
            description = "Posts a confidential comment for a review of a specific paper using userID and reviewerID." +
                            " The requester must be a chair of the track that the paper is in." +
                            " 403 FORBIDDEN error would be given when the requester is not a valid chair"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Confidential comment successfully posted"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester is not a valid chair.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper or reviewer was not found.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @PutMapping(path = "/confidential-comment", consumes = "application/json")
    public ResponseEntity<Void> submitConfidentialComment(@RequestParam Long userID,
                                                    @PathVariable Long reviewerID,
                                                    @PathVariable Long paperID,
                                                    @RequestBody @Schema(description = "Comment", example = "Some comment")
                                                    String comment) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
