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
        // TODO make sure the names of the roles correspond to the ones the Users team is using
        //  it seems they are a bit inconsistent about it
        switch (userString) {
            case "PC Chair", "PCchair":
            case "General Chair", "GeneralChair":
                return CHAIR;
            case "Author", "author":
                return AUTHOR;
            case "PC Member", "PCMember":
            case "Sub-reviewer", "Subreviewer":
                return REVIEWER;
            default:
                throw new RuntimeException("Failed to parse user role");
        }
    }
}
