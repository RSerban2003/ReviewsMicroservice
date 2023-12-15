package nl.tudelft.sem.v20232024.team08b.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import nl.tudelft.sem.v20232024.team08b.application.BidsService;
import nl.tudelft.sem.v20232024.team08b.dtos.Bid;
import nl.tudelft.sem.v20232024.team08b.dtos.BidByReviewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/papers/{paperID}/bids")
@Tag(name = "Bids", description = "Operations for bidding on papers, before the reviews are assigned")
public interface BidsAPI {
    @Operation(summary = "Get all bids for a given paper",
        description = "Responds with a list of bids and the IDs of the corresponding " +
            "reviewers. The requester must be a chair of the track that the paper is in."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully accessed the bids for paper"),
        @ApiResponse(responseCode = "403", description = "Forbidden, you are not allowed to view bids. " +
            "The requester must be a chair of the track.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not found, the specified paper does not exist.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @GetMapping(path = "", produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<BidByReviewer>> getBidsForPaper(
        @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of a paper to return") Long paperID
    );

    @Operation(summary = "Get the bid of a given reviewer for a given paper",
        description =
            "Responds with a bid (a preference to review or not review a paper, " +
                "based on expertise level, submitted by the reviewers) of the given reviewer for reviewing " +
                "the given paper. The requester must be the reviewer themselves, or a chair " +
                "of the track the paper is in."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully accessed the bids for paper by given reviewer"),
        @ApiResponse(responseCode = "403", description = "Forbidden, you are not allowed to view bids. " +
            "Only the chairs of the tracks and the bid submitter are allowed to do that.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not found, the specified bid does not exist", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @GetMapping(path = "/by-reviewer/{reviewerID}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Bid> getBidForPaperByReviewer(
        @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of the paper") Long paperID,
        @PathVariable @Parameter(description = "The the ID of the reviewer of the paper") Long reviewerID
    );

    @Operation(summary = "Bid on a paper",
        description = "Saves the preference (based on expertise) of the requester in regards to reviewing the " +
            "given paper. The requester must be a reviewer of the track the paper is in. " +
            "Also, the submission deadline has to have passed and the bidding deadline must not have passed."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful bid on the paper"),
        @ApiResponse(responseCode = "403", description = "Forbidden. " +
            "The requester must be a reviewer in the track the paper is in.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. " +
            "The requested paper or track was not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "409", description = "Conflict. The submission deadline must have passed. " +
            "The bidding deadline must not have passed.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @PutMapping(path = "", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<Void> bid(
        @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of the paper") Long paperID,
        @RequestBody Bid bid
    );
}
