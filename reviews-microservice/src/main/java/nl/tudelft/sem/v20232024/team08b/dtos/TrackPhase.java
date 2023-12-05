package nl.tudelft.sem.v20232024.team08b.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Phases of the review process for a track.\n\n" +
    "SUBMITTING: The review process hasn't started. Papers are still being submitted.\n\n" +
    "BIDDING: Papers cannot be submitted now. Reviewers can bid on the papers, to indicate their expertise level. " +
    "Ends after the bidding deadline for the track passes.\n\n" +
    "ASSIGNING: Chairs of the track can now assign papers to reviewers in the track.\n\n" +
    "REVIEWING: Reviewing process. During this phase, the papers in the track are either in Review or in Discussion.\n\n" +
    "FINAL: The reviews for all of the papers in the track have been finalized. They are now accessible to authors.")
public enum TrackPhase {
    SUBMITTING,
    BIDDING,
    ASSIGNING,
    REVIEWING,
    FINAL
}
