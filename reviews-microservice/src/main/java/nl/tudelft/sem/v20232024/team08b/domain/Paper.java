package nl.tudelft.sem.v20232024.team08b.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@JsonIdentityInfo(scope = Paper.class, generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "ID")
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class Paper implements Serializable {
    @Id
    @GeneratedValue
    private UUID paperID;




}
