package nl.tudelft.sem.v20232024.team08b.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
@AllArgsConstructor
public class ReviewId implements Serializable {

    private Long paperID;

    private Long reviewerID;

    public ReviewId() {
    }
}
