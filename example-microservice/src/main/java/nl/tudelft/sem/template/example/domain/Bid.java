package nl.tudelft.sem.template.example.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class Bid implements Serializable {
    private UUID paperID;
    private UUID userId;

    private Status status;

    private enum Status {
        WANT,
        NEUTRAL,
        DONTWANT
    };
    public Bid() {
        this.status = Status.NEUTRAL;
    }
    public Bid(UUID paperID, UUID userId) {
        this.status = Status.NEUTRAL;
        this.paperID = paperID;
        this.userId = userId;
    }
}
