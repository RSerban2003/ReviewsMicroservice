package nl.tudelft.sem.v20232024.team08b.application.phase;

import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperPhase;
import org.springframework.stereotype.Component;

@Component
public class PaperPhaseCalculator {
    /**
     * Calculates current phase of the paper.
     *
     * @param paperID the ID of the paper
     * @return current phase of the given paper.
     */
    public PaperPhase getPaperPhase(Long paperID) {
        return PaperPhase.REVIEWED;
    }
}
