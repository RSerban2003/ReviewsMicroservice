package nl.tudelft.sem.v20232024.team08b.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.dtos.ReviewDTO;
import nl.tudelft.sem.v20232024.team08b.dtos.ReviewSubmission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/papers/{paperID}/reviews/{reviewerID}")
@Tag(name = "Reviews", description = "Operations to deal with reviews: reading them, submitting, commenting, etc")
public class ReviewController {
    @Operation(summary = "Gets a review",
            description = "Responds with the review of a specific paper using userID and reviewerID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<ReviewDTO> read(@RequestParam Long userID, @PathVariable Long reviewerID,
                                          @PathVariable Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Posts a review",
            description = "Gives a review to a specific paper." +
                            "The requester must be a valid reviewer and will be identified using userID and reviewerID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @PutMapping(path = "", produces = "application/json")
    public ResponseEntity submit(@RequestBody ReviewSubmission review, @PathVariable Long reviewerID,
                                 @PathVariable Long paperID, @RequestParam Long userID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Posts a confidential comment",
            description = "Posts a confidential comment for a review of a specific paper using userID and reviewerID." +
                            " The requester must be a chair of the track that the paper is in." +
                            " 403 FORBIDDEN error would be given when the requester is not a valid chair"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @PutMapping(path = "/confidential-comment", produces = "application/json")
    public ResponseEntity submitConfidentialComment(@RequestParam Long userID,
                                                    @PathVariable Long reviewerID,
                                                    @PathVariable Long paperID,
                                                    @RequestBody @Schema(description = "Comment", example = "Some comment")
                                                    String comment) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
