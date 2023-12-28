package nl.tudelft.sem.v20232024.team08b.repos;

import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.dtos.review.Paper;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.dtos.users.RolesOfUser;
import nl.tudelft.sem.v20232024.team08b.utils.HttpRequestSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ExternalRepository {
    private final int ourID = -1;
    private ObjectMapper objectMapper;
    private HttpRequestSender httpRequestSender;
    private String submissionsURL = "https://localhost:8081";
    private String usersURL = "https://localhost:8080";

    /**
     * Default constructor.
     *
     * @param httpRequestSender class used for sending HTTP requests
     */
    @Autowired
    public ExternalRepository(HttpRequestSender httpRequestSender) {
        this.httpRequestSender = httpRequestSender;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Constructor used for testing purposes.
     *
     * @param httpRequestSender class used for sending HTTP requests
     * @param objectMapper class used to map objects to json
     */
    public ExternalRepository(HttpRequestSender httpRequestSender, ObjectMapper objectMapper) {
        this.httpRequestSender = httpRequestSender;
        this.objectMapper = objectMapper;
    }

    /**
     * Gets from the Users microservice all the roles of a user.
     *
     * @param userID the ID of the user
     * @return a list of roles of that user
     */
    public RolesOfUser getRolesOfUser(Long userID) throws NotFoundException {
        try {
            String url = usersURL + "/user/" + userID + "/tracks/role";
            String response = httpRequestSender.sendGetRequest(url);
            RolesOfUser rolesOfUser;
            rolesOfUser = objectMapper.readValue(response, RolesOfUser.class);
            return rolesOfUser;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse the HTTP response");
        }
    }

    /**
     * Gets a paper (called submission) from the Submissions microservice.
     *
     * @param paperID the ID of the paper to get
     * @return the gotten Submission object
     */
    public Submission getSubmission(Long paperID) throws NotFoundException {
        try {
            String url = submissionsURL + "/submission/" + paperID + "/" + ourID;
            String response = httpRequestSender.sendGetRequest(url);
            Submission submission;
            submission = objectMapper.readValue(response, Submission.class);
            return submission;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse the HTTP response");
        }
    }

    /**
     * Retrieves and sets the details of a paper from an external submission database into a Paper DTO.
     *
     * @param paperID The identifier of the paper from which details are needed.
     * @return A Paper DTO filled with values from the submission.
     * @throws NotFoundException If the submission is not found in the external database.
     */
    public Paper getFullPaper(Long paperID) throws NotFoundException {

        Paper paper = new Paper();

        Submission submission = getSubmission(paperID);
        paper.setTitle(submission.getTitle());
        paper.setKeywords(submission.getKeywords());
        paper.setAbstractSection(submission.getAbstract());
        paper.setMainText(new String(submission.getPaper()));

        return paper;
    }

    /**
     * Retrieves and sets the title and abstract of a paper from an external submission database into a Paper DTO.
     *
     * @param paperID The unique identifier of the paper whose title and abstract are to be retrieved.
     * @return A Paper DTO containing the title and abstract from the submission.
     * @throws NotFoundException If the submission corresponding to the paperID is not found in the external database.
     */
    public Paper getTitleAndAbstract(Long paperID) throws NotFoundException {

        Paper paper = new Paper();

        Submission submission = getSubmission(paperID);
        paper.setTitle(submission.getTitle());
        paper.setAbstractSection(submission.getAbstract());

        return paper;
    }
}
