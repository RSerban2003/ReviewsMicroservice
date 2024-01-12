package nl.tudelft.sem.v20232024.team08b.dtos.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.v20232024.team08b.domain.Comment;

@Getter
@Setter
@Schema(description = "Discussion comment - it can only be seen by the reviewers and chairs" +
        " and not authors.")
public class DiscussionComment {
    @Schema(description = "The ID of the commenter", example = "1")
    Long commenterID;

    @Schema(description = "A confidential comment for other reviewers", example = "Some comment")
    private String comment;

    /**
     * Constructor for creating a DiscussionComment DTO from a Comment domain object.
     *
     * @param comment The Comment object from which to extract the ID of the commenter and the text
     */
    public DiscussionComment(Comment comment) {
        this.commenterID = comment.getAuthorID();
        this.comment = comment.getText();
    }
}
