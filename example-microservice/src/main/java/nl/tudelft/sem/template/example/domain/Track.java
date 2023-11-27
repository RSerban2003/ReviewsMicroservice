package nl.tudelft.sem.template.example.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.Date;
import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Setter;

@Getter
@Setter
@Entity
@JsonIdentityInfo(scope = Track.class, generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id")
public class Track implements Serializable {
  enum Phase{
    Submitting,
    Bidding,
    Assigning,
    Reviewing,
    Final
  }
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Basic(optional = false)
  private Date biddingDeadLine;
  @Basic
  private Phase currentPhase;

  @SuppressWarnings("unused")
  public Track() {

  }

  /**
   * Constructor of track
   * @param biddingDeadLine of the track
   */
  public Track(Date biddingDeadLine) {
    this.biddingDeadLine = biddingDeadLine;
    currentPhase = Phase.Submitting;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Track track = (Track) o;
    return Objects.equals(id, track.id) && Objects.equals(biddingDeadLine,
        track.biddingDeadLine) && currentPhase == track.currentPhase;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, biddingDeadLine, currentPhase);
  }
}