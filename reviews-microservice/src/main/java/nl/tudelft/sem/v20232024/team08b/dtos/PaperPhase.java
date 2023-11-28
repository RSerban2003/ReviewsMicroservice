package nl.tudelft.sem.v20232024.team08b.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Exactly how far along a paper is in the review process")
public enum PaperPhase {
    BEFORE_REVIEW,
    IN_REVIEW,
    IN_DISCUSSION,
    REVIEWED
}
