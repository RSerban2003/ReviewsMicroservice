package nl.tudelft.sem.v20232024.team08b.dtos;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.v20232024.team08b.domain.Paper;

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
