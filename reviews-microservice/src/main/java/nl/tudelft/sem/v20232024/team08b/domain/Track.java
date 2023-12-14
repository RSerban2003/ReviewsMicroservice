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
@EqualsAndHashCode
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


    /**
     * Construction for track.
     *
     * @param biddingDeadLine bidding deadline for track
     * @param currentPhase current phase of a track
     * @param conference conference to which trac belongs
     * @param papers papers that belong to this track
     */
    public Track(Date biddingDeadLine, TrackPhase currentPhase, Conference conference, List<Paper> papers) {
        this.biddingDeadLine = biddingDeadLine;
        this.currentPhase = currentPhase;
        this.conference = conference;
        this.papers = papers;
    }

    public Track(){}

}
