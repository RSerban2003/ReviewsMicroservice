package nl.tudelft.sem.template.example.domain;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class Bid implements Serializable {
    private UUID paperID;
    private UUID userId;

    private Status status;

    private enum Status {
        WANT,
        NEUTRAL,
        DONTWANT
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bid bid = (Bid) o;
        return paperID.equals(bid.paperID) && userId.equals(bid.userId) && status == bid.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(paperID, userId, status);
    }

    public UUID getPaperID() {
        return paperID;
    }

    public void setPaperID(UUID paperID) {
        this.paperID = paperID;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Bid(UUID paperID, UUID userId, Status status) {
        this.paperID = paperID;
        this.userId = userId;
        this.status = status;
    }
}
