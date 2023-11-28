package nl.tudelft.sem.v20232024.team08b.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Confidential comment - it can only be seen by the reviewers and chairs" +
        " and not authors.")
public class ConfidentialComment {
    @Schema(description = "The ID of the commenter", example = "1")
    Long commenterID;

    @Schema(description = "A confidential comment for other reviewers", example = "Some comment")
    private String comment;
}
