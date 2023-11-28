package nl.tudelft.sem.v20232024.team08b.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.dtos.ReviewSubmission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/papers/{paperID}/reviews/{reviewerID}")
public class ReviewController {
    /**
     * Allows specific users to read the reviews for a specific paper.
     * @param userID the ID of the user
     * @param reviewerID the ID of the reviewer
     * @param paperID the ID of the paper
     * @return the review object
     */
    @Operation(summary = "Gets a review",
            description = "Responds with the review of a specific paper using userID and reviewerID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @GetMapping("")
    public ResponseEntity<Review> read(@RequestParam Long userID, @PathVariable Long reviewerID,
                                       @PathVariable Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * @param review the review object containing the review details and the submitter ID
     * @param reviewerID
     * @param paperID
     * @return a response entity with code 200 if adding  was successful
     */
    @Operation(summary = "Posts a review",
            description = "Gives a review to a specific paper." +
                            "The requester must be a valid reviewer and will be identified using userID and reviewerID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @PostMapping("")
    public ResponseEntity<Void> submit(@RequestBody ReviewSubmission review, @PathVariable Long reviewerID,
                                 @PathVariable Long paperID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * @param paperID the ID for the paper
     * @param comment stores the reviewer ID and the comment itself
     * @return the saved review object
     */
    @Operation(summary = "Posts a confidential comment",
            description = "Posts a confidential comment for a review of a specific paper using userID and reviewerID." +
                            " The requester must be a chair of the track that the paper is in." +
                            " 403 FORBIDDEN error would be given when the requester is not a valid chair"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @PostMapping("/confidentialComment")
    public ResponseEntity submitConfidentialComment(@RequestParam Long userID,
                                                    @PathVariable Long reviewerID,
                                                    @PathVariable Long paperID,
                                                    @RequestBody String comment) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
