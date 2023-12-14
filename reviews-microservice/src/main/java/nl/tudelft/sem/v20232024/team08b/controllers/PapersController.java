package nl.tudelft.sem.v20232024.team08b.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.tudelft.sem.v20232024.team08b.application.PapersService;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Paper;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperStatus;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/papers/{paperID}")
@Tag(name = "Papers", description = "Operations for viewing papers: their contents, review status, etc.")
public class PapersController {
    private final PapersService papersService;

    /**
     * Default constructor for the controller.
     *
     * @param papersService the respective service to inject
     */
    @Autowired
    public PapersController(PapersService papersService) {
        this.papersService = papersService;
    }

    @Operation(summary = "Get the title and abstract of a paper",
        description = "Responds with the title and abstract of the given paper. "
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of paper title and abstract"),
        @ApiResponse(responseCode = "403", description = "Forbidden. " +
            "The requester must be a reviewer or chair for the track the paper is in.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper was not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @ResponseBody
    @GetMapping(path = "/title-and-abstract", produces = "application/json")
    public ResponseEntity<PaperSummary> getTitleAndAbstract(
        @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of a paper to return") Long paperID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Get the whole paper without the author names",
        description = "Responds all the contents of a paper submission. " +
            "Excludes the names and credentials of the author. "
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of the complete paper"),
        @ApiResponse(responseCode = "403", description = "Forbidden. " +
            "The requester must be: a chair of the track the paper is in; " +
            "or a reviewer who has been assigned to the given paper " +
            "(the review phase for the paper must have started).", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper was not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @ResponseBody
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<Paper> get(
        @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of a paper to return") Long paperID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(summary = "Get the review status of a paper",
        description = "Responds with whether the paper has been accepted or rejected, " +
            "or if it hasn't been decided yet."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of the paper review status"),
        @ApiResponse(responseCode = "403", description = "Forbidden. " +
            "The requester must be an author of the paper, a chair of the paper's track, " +
            "or a reviewer assigned to the paper.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper was not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @ResponseBody
    @GetMapping(path = "/status", produces = "application/json")
    public ResponseEntity<PaperStatus> getState(
        @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of a paper to check status") Long paperID
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


}
