package nl.tudelft.sem.v20232024.team08b.dtos.review;

public enum UserRole {
    CHAIR,
    REVIEWER,
    AUTHOR;

    /**
     * Parses a string given from other microservices into an enum
     * that can be used internally.
     *
     * @param userString the string describing a role.
     * @return a parsed enum
     */
    public static UserRole parse(String userString) {
        switch (userString) {
            case "PC Chair":
            case "General Chair":
                return CHAIR;
            case "Author":
                return AUTHOR;
            case "PC Member":
            case "Sub-reviewer":
                return REVIEWER;
            default:
                throw new RuntimeException("Failed to parse user role");
        }
    }
}
