package nl.tudelft.sem.v20232024.team08b.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Conference implements Serializable {
    @Id
    private Long id;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Track> track;


    public Conference(){}

}