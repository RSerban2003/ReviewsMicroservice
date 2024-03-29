package nl.tudelft.sem.v20232024.team08b.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import nl.tudelft.sem.v20232024.team08b.dtos.review.DiscussionComment;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Review;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@OpenAPIDefinition(info = @Info(title = "EasyConf Reviews Microservice", version = "0.0.1-SNAPSHOT",
    description = "EasyConf is web-based conference management system, designed to organize and administrate " +
        "academic conferences, workshops, and events. It offers a platform for authors, reviewers, " +
        "and organizers (chairs) to handle paper submissions, peer reviews, and program scheduling efficiently. " +
        "It provides tools for managing the review process, generating program schedules, and handling " +
        "communication between organizers and participants.\n\n" +
        "The Reviews Microservice handles the process of reviewing papers before a conference. " +
        "This microservice is responsible for assigning papers to reviews, allowing reviewers to add their comments, " +
        "and allowing the PC chairs to finalize the decision about acceptance and rejection of the submitted papers.\n\n" +
        "The different roles that the users can have are specified in the Users Microservice and the process for " +
        "submitting papers to conferences is specified in the Submissions Microservice.\n\n" +
        "TECHNICAL NOTE: in every endpoint, we require the requesting user to provide their ID. I.e., the requesterID " +
        "parameter must be provided. It is used for authentication in order to check if the user is allowed to perform " +
        "certain actions."
    ))
@RequestMapping("/papers/{paperID}")
@Tag(name = "Reviews", description = "Operations to deal with reviews: reading them, submitting, commenting, etc")
public interface ReviewsAPI {
    @Operation(summary = "Gets a review",
        description = "Responds with the review of a specific paper (paperID), reviewed by user (userID). " +
            "Confidential comments will not be revealed if the requester is the author of the paper."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of the review"),
        @ApiResponse(responseCode = "403", description = "Forbidden. The requester must be a reviewer for this paper, " +
            "a chair of the track the paper is in, or an author of the paper (in that case, the reviews for the track " +
            "the paper is in must all be finalized, and only then they can be revealed to authors).", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. " +
            "The requested paper or reviewer was not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @GetMapping(path = "/reviews/by-reviewer/{reviewerID}", produces = "application/json")
    public ResponseEntity<Review> read(
        @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of a reviewer in charge of this paper") Long reviewerID,
        @PathVariable @Parameter(description = "The ID of the paper") Long paperID
    );

    @Operation(summary = "Submit (or edit) a review",
        description = "The requester submits (or resubmits) a review to a specific paper. " +
            "Once all the reviewers for a paper have submitted a review for that paper, " +
            "the Discussion phase for that paper " +
            "begins and reviewers can see each others reviews and write comments on them. "
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review successfully submitted"),
        @ApiResponse(responseCode = "403", description = "Forbidden. " +
            "The requester must be a reviewer assigned to the given paper. ", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. " +
            "The requested paper or reviewer was not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @PutMapping(path = "/reviews", consumes = {"application/json"})
    public ResponseEntity<Void> submit(
        @RequestBody @Parameter(description = "The review that the requester wants to submit") Review review,
        @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of a paper that the review is submitted for") Long paperID);

    @Operation(summary = "Get the list of a reviewers for a given paper",
        description = "Responds with list of reviewers' IDs assigned to that paper."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of the list of reviewers"),
        @ApiResponse(responseCode = "403", description = "Forbidden. " +
            "The requester must be a chair of the track that the paper is in, " +
            "or a reviewer also assigned to the given paper.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper was not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @ResponseBody
    @GetMapping(path = "/reviewers", produces = "application/json")
    public ResponseEntity<List<Long>> getReviewers(
        @RequestParam @Parameter(description = "The ID of the user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of the paper") Long paperID
    );

    @Operation(summary = "Get the review phase of a paper",
        description =
            "Responds with how far along a paper is in the review process. Each paper whithin a track has its own phase. " +
                "For more information check the description of the returned enum."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of the paper review phase"),
        @ApiResponse(responseCode = "403", description = "Forbidden. " +
            "The requester must be a chair of the track the paper is in, or a reviewer assigned to that paper.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper was not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @ResponseBody
    @GetMapping(path = "/reviews/phase", produces = "application/json")
    public ResponseEntity<PaperPhase> getPhase(
        @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of the paper") Long paperID
    );

    @Operation(summary = "End the discussion phase for a paper",
        description = "The paper is now considered reviewed: the reviews cannot be changed. " +
            "Once all of the papers in a track are reviewed, the track automatically goes to the FINAL phase."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful finalization of the discussion phase"),
        @ApiResponse(responseCode = "403", description = "Forbidden. " +
            "The requester must be a chair of the track the paper is in.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. The requested paper was not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "409", description = "Conflict. The paper must be in its Discussion phase and " +
            "all of the reviews need to either be positive or negative. Only then can they be finalized.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @ResponseBody
    @PostMapping(path = "/reviews/finalization")
    public ResponseEntity<Void> finalization(
        @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of a paper to finalize the reviews for") Long paperID
    );

    @Operation(summary = "Posts a discussion comment",
        description = "Posts a discussion comment for a review of a specific paper using userID and reviewerID. " +
            "Discussion comments are comments that can be left on reviews during the Discussion phase. " +
            "These comments will not be revealed to authors. " +
            "The requester must be a chair of the track that the paper is in, or a reviewer " +
            "also assigned to the given paper. Once posted, these comments cannot be edited or deleted."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Success. A discussion comment was appended to the review."),
        @ApiResponse(responseCode = "403", description = "Forbidden. " +
            "The requester is not a valid chair or reviewer.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. " +
            "The requested paper or reviewer was not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @PostMapping(path = "/reviews/by-reviewer/{reviewerID}/discussion-comments", consumes = "application/json")
    public ResponseEntity<Void> submitDiscussionComment(
        @RequestParam @Parameter(description = "The ID of a user making the request") Long requesterID,
        @PathVariable @Parameter(description = "The ID of a reviewer in charge of this paper") Long reviewerID,
        @PathVariable @Parameter(description = "The ID of a paper to which to add the comment to") Long paperID,
        @RequestBody @Schema(description = "Comment", example = "Some comment")
        String comment
    );

    @Operation(summary = "Gets the discussion comments",
        description = "Responds with all the discussion comments for a paper. " +
            "Discussion comments are comments that can be left on reviews during the Discussion phase. " +
            "These comments will not be revealed to authors. " +
            "The requester must be a chair of the track that the paper is in, or a reviewer " +
            "also assigned to the given paper. Once posted, these comments cannot be edited or deleted."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success."),
        @ApiResponse(responseCode = "403", description = "Forbidden. " +
            "The requester is not a valid chair or reviewer.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "404", description = "Not Found. " +
            "The requested paper or reviewer was not found.", content = {
            @Content(schema = @Schema())}),
        @ApiResponse(responseCode = "500", description = "Internal Server Error. " +
            "An unexpected server error occurred.", content = {
            @Content(schema = @Schema())})
    })
    @GetMapping(path = "/reviews/by-reviewer/{reviewerID}/discussion-comments", produces = "application/json")
    public ResponseEntity<List<DiscussionComment>> getDiscussionComments(
        @RequestParam @Parameter(description = "The ID of the user making the request.") Long requesterID,
        @PathVariable @Parameter(description = "The ID of a reviewer in charge of this paper") Long reviewerID,
        @PathVariable @Parameter(description = "The ID of a paper to get the comments from") Long paperID
    );
}
