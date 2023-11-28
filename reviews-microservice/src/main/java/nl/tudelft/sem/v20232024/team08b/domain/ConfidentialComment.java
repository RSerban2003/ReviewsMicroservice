package nl.tudelft.sem.v20232024.team08b.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfidentialComment {
    @Schema(description = "A confidential comment for other reviewers", example = "Some comment")
    private String comment;
}
