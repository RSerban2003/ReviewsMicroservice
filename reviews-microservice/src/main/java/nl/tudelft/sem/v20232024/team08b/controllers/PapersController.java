package nl.tudelft.sem.v20232024.team08b.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import nl.tudelft.sem.v20232024.team08b.dtos.Paper;
import nl.tudelft.sem.v20232024.team08b.dtos.PaperPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.PaperStatus;
import nl.tudelft.sem.v20232024.team08b.dtos.PaperSummary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/papers/{paperID}")
@Tag(name = "Papers", description = "Operations for viewing papers -- their contents, review status, etc.")
public class PapersController {
    @Operation(summary = "Get the title and abstract of a paper",
        description = "Responds with the title and abstract of the given paper. " +
            "The requester must be a reviewer or chair for the track the paper is in."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of paper title and abstract"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper was not found.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @ResponseBody
    @GetMapping(path = "/title-and-abstract", produces = "application/json")
    public ResponseEntity<PaperSummary> getTitleAndAbstract(
        @RequestParam Long requesterID,
        @PathVariable Long paperID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @Operation(summary = "Get the whole paper without the author names",
        description = "Responds all the contents of a paper submission. " +
            "Excludes the names and credentials of the author. " +
            "The requester must be: a chair of the track the paper is in; or a reviewer " +
            "who has been assigned to the given paper (the review phase for the paper must have started)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of the complete paper"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper was not found.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @ResponseBody
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<Paper> get(
        @RequestParam Long requesterID,
        @PathVariable Long paperID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Get the review status of a paper",
        description = "Responds with whether the paper has been accepted or rejected, " +
            "or if it hasn't been decided yet. The requester must be an author of the paper, " +
            "a chair of the paper's track, or a reviewer assigned to the paper."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of the paper review status"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper was not found.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @ResponseBody
    @GetMapping(path = "/status", produces = "application/json")
    public ResponseEntity<PaperStatus> getState(
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
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper was not found.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @ResponseBody
    @GetMapping(path = "/review-phase", produces = "application/json")
    public ResponseEntity<PaperPhase> getPhase(
        @RequestParam Long requesterID,
        @PathVariable Long paperID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @Operation(summary = "Get the list of a reviewers for a given paper",
        description = "Responds with list of reviewers assigned to that paper." +
            "The requester must be a chair of the track the paper is in"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of the list of reviewers"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper was not found.", content = {@Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @ResponseBody
    @GetMapping(path = "/reviewers", produces = "application/json")
    public ResponseEntity<List<Long>> getReviewers(
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
            @ApiResponse(responseCode = "403", description = "Forbidden. The requester lacks necessary permissions.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found. The requested paper was not found.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error. An unexpected server error occurred.", content = {@Content(schema = @Schema())})
    })
    @ResponseBody
    @PutMapping(path = "/finalize-reviews")
    public ResponseEntity<Void> finalize(
            @RequestParam Long requesterID,
            @PathVariable Long paperID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
