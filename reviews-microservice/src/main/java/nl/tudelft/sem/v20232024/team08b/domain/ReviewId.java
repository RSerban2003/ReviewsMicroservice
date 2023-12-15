package nl.tudelft.sem.v20232024.team08b.domain;

import java.io.Serializable;
import lombok.Data;

@Data
public class ReviewId implements Serializable {

    private Paper paper;
    private User reviewer;
}
