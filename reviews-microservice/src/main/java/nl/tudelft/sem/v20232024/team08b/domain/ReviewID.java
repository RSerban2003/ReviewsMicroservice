package nl.tudelft.sem.v20232024.team08b.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewID implements Serializable {

    private Long paperID;

    private Long reviewerID;
}
