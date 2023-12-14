package nl.tudelft.sem.v20232024.team08b.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.v20232024.team08b.dtos.TrackPhase;

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


    public Track(){}



}
