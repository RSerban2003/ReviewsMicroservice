package nl.tudelft.sem.v20232024.team08b.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.tudelft.sem.v20232024.team08b.dtos.Bid;
import nl.tudelft.sem.v20232024.team08b.dtos.BidByReviewer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/papers/{paper_id}/bids")
@Tag(name = "Bids", description = "Operations for bidding on papers, before the reviews are assigned")
public class BidsController {
    @Operation(summary = "Get all bids for a given paper",
            description = "Responds with a list of bids and the ID's of the corresponding" +
                    "reviewers. The requester must be a chair of the track that the paper is in." +
                    "If no bid is returned for a particular reviewer, it can be assumed that the " +
                    "reviewer has no preference in regards to this particular paper."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @GetMapping(path = "", produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<BidByReviewer>> getBidsForPaper(
            @RequestParam Long requester_id,
            @PathVariable Long paper_id
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Get all bids for a given paper and reviewer",
            description = "Responds with the preference (a bid) of the given reviewer for reviewing " +
                    "the given paper. If the reviewer hasn't indicated a preference, 404 NOT FOUND " +
                    "will be returned."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @GetMapping(path = "/by-reviewer-id/{reviewer_id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Bid> getBidsForPaperByReviewer(
            @RequestParam Long requester_id,
            @PathVariable Long paper_id,
            @PathVariable Long reviewer_id
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Bid on a paper",
            description = "Saves the preference of the requester in regards to reviewing the " +
                    "given paper."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @PutMapping(path = "", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<Void> bid(
            @RequestParam Long requester_id,
            @PathVariable Long paper_id,
            @RequestBody Bid bid
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Delete a bid",
            description = "Resets the preference of the requester for the given paper to neutral."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})
    })
    @DeleteMapping(path = "")
    @ResponseBody
    public ResponseEntity<Void> deleteBid(
            @RequestParam Long requester_id,
            @PathVariable Long paper_id
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
