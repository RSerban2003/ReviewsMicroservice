package nl.tudelft.sem.v20232024.team08b.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User implements Serializable {
    @Id
    private Long id;

    public User(){}

}
