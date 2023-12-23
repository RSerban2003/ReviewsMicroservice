package nl.tudelft.sem.v20232024.team08b.unit.dtos;

import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserRoleTests {
    @Test
    void testReviewer() {
        assertThat(UserRole.parse("PC Member")).isEqualTo(UserRole.REVIEWER);
        assertThat(UserRole.parse("Sub-reviewer")).isEqualTo(UserRole.REVIEWER);
    }

    @Test
    void testAuthor() {
        assertThat(UserRole.parse("Author")).isEqualTo(UserRole.AUTHOR);
    }

    @Test
    void testChair() {
        assertThat(UserRole.parse("General Chair")).isEqualTo(UserRole.CHAIR);
        assertThat(UserRole.parse("PC Chair")).isEqualTo(UserRole.CHAIR);
    }

    @Test
    void testNeither() {
        assertThrows(RuntimeException.class, () -> UserRole.parse("Some"));
    }
}
