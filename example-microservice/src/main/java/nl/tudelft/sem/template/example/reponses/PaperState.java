package nl.tudelft.sem.template.example.reponses;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.template.example.domain.Paper;

@Getter
@Setter
@JsonIdentityInfo(scope = Paper.class, generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "ID")
public class PaperState {
    enum State {
        ACCEPTED,
        REJECTED,
        NOT_DECIDED
    }
    State state;
}
