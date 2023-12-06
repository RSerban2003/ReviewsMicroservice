package nl.tudelft.sem.v20232024.team08b.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    private Long id;

    public User(Long id) {
        this.id = id;
    }

    public User(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
