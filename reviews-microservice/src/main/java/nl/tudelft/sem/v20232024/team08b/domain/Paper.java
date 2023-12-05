package nl.tudelft.sem.v20232024.team08b.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import nl.tudelft.sem.v20232024.team08b.dtos.PaperPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.PaperStatus;

@Entity
@Getter
public class Paper implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "paper", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Review> listOfReviews;

    @ManyToOne(cascade = CascadeType.ALL)
    private Track track;

    PaperStatus status;

    PaperPhase phase;

}
