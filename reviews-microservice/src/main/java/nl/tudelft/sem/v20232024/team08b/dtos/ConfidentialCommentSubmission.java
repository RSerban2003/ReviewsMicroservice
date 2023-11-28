package nl.tudelft.sem.template.example.reponses;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ConfidentialCommentSubmission {
    UUID reviewerID;
    String comment;
}
