package nl.tudelft.sem.v20232024.team08b.domain;

import lombok.Data;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperStatus;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@Data
public class Paper implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Review> listOfReviews;

    @ManyToOne(cascade = CascadeType.ALL)
    private Track track;

    private PaperStatus status;

    private PaperPhase phase;

    public Paper(){}
}
