package nl.tudelft.sem.v20232024.team08b.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Conference implements Serializable {
    @Id
    private Long id;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Track> track;

    public Conference(Long id, List<Track> track) {
        this.id = id;
        this.track = track;
    }

    public Conference(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Track> getTrack() {
        return track;
    }

    public void setTrack(List<Track> track) {
        this.track = track;
    }
}
