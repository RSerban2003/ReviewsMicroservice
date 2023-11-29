package nl.tudelft.sem.v20232024.team08b.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import nl.tudelft.sem.v20232024.team08b.dtos.Bid;
import nl.tudelft.sem.v20232024.team08b.dtos.BidByReviewer;
import org.springframework.http.HttpStatus;
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
public class BidsController {
    @Operation(summary = "Get all bids for a given paper",
        description = "Responds with a list of bids and the IDs of the corresponding " +
            "reviewers. By default reviewers are NEUTRAL towards a paper, so NEUTRAL \"bids\" " +
            "will not be returned. " +
            "The requester must be a chair of the track that the paper is in. " +
            "If no bid is returned for a particular reviewer, it can be assumed that the " +
            "reviewer has no preference in regards to this particular paper."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully accessed the bids for paper"),
            @ApiResponse(responseCode = "403", description = "Forbidden, you are not allowed to view bids",content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not found, the specified paper does not exist",content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.",content = {@Content(schema = @Schema())})
    })
    @GetMapping(path = "", produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<BidByReviewer>> getBidsForPaper(
            @RequestParam Long requesterID,
            @PathVariable Long paperID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Get the bid of a given reviewer for a given paper",
            description = "Responds with the preference (a bid) of the given reviewer for reviewing " +
                "the given paper. If the reviewer hasn't indicated a preference, NEUTRAL " +
                "will be returned. The requester must be the reviewer themselves, or a chair " +
                "of the track the paper is in."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully accessed the bids for paper by given reviewer"),
            @ApiResponse(responseCode = "403", description = "Forbidden, you are not allowed to view bids",content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not found, the specified bid does not exist",content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.",content = {@Content(schema = @Schema())})
    })
    @GetMapping(path = "/by-reviewer/{reviewerID}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Bid> getBidForPaperByReviewer(
            @RequestParam Long requesterID,
            @PathVariable Long paperID,
            @PathVariable Long reviewerID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Bid on a paper",
        description = "Saves the preference (based on expertise) of the requester in regards to reviewing the " +
            "given paper. NEUTRAL effectively resets the preference to the default (no preference). " +
            "The requester must be a reviewer of the track the paper is in. Also, the submission deadline " +
            "has to have passed and the bidding deadline must not have passed."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful bid on the paper"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester must be a reviewer in the track the paper is in.", content = {
            @Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found. The requested paper or track was not found.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "409", description = "Conflict. The bidding deadline must not have passed.", content = {
            @Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @PutMapping(path = "", consumes = "application/json")
    public ResponseEntity<Void> bid(
            @RequestParam Long requesterID,
            @PathVariable Long paperID,
            @RequestBody Bid bid
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
