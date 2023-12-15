package nl.tudelft.sem.v20232024.team08b.dtos.review;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Exactly how far along a paper is in the review process\n\n" +
    "BEFORE_REVIEW: Before the Review phase of the track the paper is in\n\n" +
    "IN_REVIEW: The reviewers all need to submit a review. Once that happens the discussion phase for the paper " +
    "automatically starts.\n\n" +
    "IN_DISCUSSION: The reviewers can now see each others reviews and comment on them. " +
    "If all of the reviews are either positive or negative, " +
    "the chair of the track can finalize the reviews which automatically puts the paper into the REVIEW phase.\n\n" +
    "REVIEWED: The reviews cannot be changed now. Once all of the papers in a track are REVIEWED, the track goes into " +
    "the final phase and the reviews can be viewed by the authors.")
public enum PaperPhase {
    BEFORE_REVIEW,
    IN_REVIEW,
    IN_DISCUSSION,
    REVIEWED
}
