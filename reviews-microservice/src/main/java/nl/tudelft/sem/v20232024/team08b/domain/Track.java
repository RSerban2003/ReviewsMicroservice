package nl.tudelft.sem.v20232024.team08b.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;

@Data
@Entity
public class Track implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Basic(optional = false)
    private Date biddingDeadLine;
    @Basic(optional = false)
    private TrackPhase currentPhase;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Paper> papers;

    @ManyToOne(cascade = CascadeType.ALL)
    private Conference conference;


    public Track(){}

}
