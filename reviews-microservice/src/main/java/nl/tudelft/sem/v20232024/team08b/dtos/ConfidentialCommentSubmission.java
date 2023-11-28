package nl.tudelft.sem.v20232024.team08b.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ConfidentialCommentSubmission {
    UUID reviewerID;
    String comment;
}
