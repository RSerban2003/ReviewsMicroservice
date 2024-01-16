package nl.tudelft.sem.v20232024.team08b.system;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperSummaryWithID;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.dtos.users.Event;
import nl.tudelft.sem.v20232024.team08b.dtos.users.Track;
import nl.tudelft.sem.v20232024.team08b.dtos.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.opentest4j.TestAbortedException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
//@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.MOCK, classes={ Application.class })
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@RunWith((SpringRunner.class)
public class SystemsTests {
    //@Autowired
    //private WebApplicationContext webApplicationContext;

    //@Autowired
    //private MockMvc mockMvc;

    private final Long ourID = -1L;
    private final String submissionsURL = "http://localhost:8081";
    private final String usersURL = "http://localhost:8082";
    private final String reviewsURL = "http://localhost:8080";

    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpClient httpClient = HttpClient.newHttpClient();

    private Long submitter1ID, submitter2ID, submitter3ID;
    private Long reviewer1ID;

    private Long track1ID, track2ID;
    private Long event1ID;
    private Long chair1ID;

    private Long submission1ID, submission2ID, submission3ID;

    @BeforeEach
    void setup() {
        //externalRepository = Mockito.mock(ExternalRepository.class);
        //mockMvc = MockMvcBuilders.standaloneSetup().build();

        /*when(externalRepository.getRolesOfUser(anyLong())).thenThrow(new RuntimeException(
                "Cowabunga it's a mock!"));*/
        /*when(externalRepository.getSubmission(anyLong())).thenCallRealMethod();
        when(externalRepository.getSubmissionsInTrack(any(), any())).thenCallRealMethod();
        when(externalRepository.getSubmissionsInTrack(any())).thenCallRealMethod();
        when(externalRepository.getTrack(any(), any())).thenCallRealMethod();*/

        //mockMvc = webAppContextSetup(webApplicationContext).build();

        // Verify that the other microservices are running
        try {
            sendRequest(RequestType.GET, null, Object.class, usersURL, "event");
        } catch (Exception e) {
            if (e.getCause().toString().equals("java.net.ConnectException")) {
                throw new TestAbortedException();
            }
        }
        System.out.println("[Systems Testing Log] Users microservice is running.");

        try {
            sendRequest(RequestType.GET, null, Object.class, submissionsURL, "submission",
                    "1000000");
        } catch (Exception e) {
            if (e.getCause().toString().equals("java.net.ConnectException")) {
                throw new TestAbortedException();
            }
        }
        System.out.println("[Systems Testing Log] Submissions microservice is running.");

        // Make some test data
        User chair1;
        Event event1;
        Track track1, track2;

        Random rng = new Random();
        var user = new User();
        user.name("1");
        user.surname("Doe");
        user.email(rng.nextInt() + "@tudelt.nl");
        var submitter = (User) sendRequest(RequestType.POST, user, User.class, usersURL, "user");
        submitter1ID = submitter.getId();

        user.name("2");
        user.surname("Doe");
        user.email(rng.nextInt() + "@tudelt.nl");
        submitter = sendRequest(RequestType.POST, user, User.class, usersURL, "user");
        submitter2ID = submitter.getId();

        user.name("3");
        user.surname("Doe");
        user.email(rng.nextInt() + "@tudelt.nl");
        submitter = sendRequest(RequestType.POST, user, User.class, usersURL, "user");
        submitter3ID = submitter.getId();

        System.out.println(submitter1ID + " " + submitter2ID + " " + submitter3ID);

        user.name("4");
        user.surname("Doe");
        user.email(rng.nextInt() + "@tudelt.nl");
        submitter = sendRequest(RequestType.POST, user, User.class, usersURL, "user");
        reviewer1ID = submitter.getId();

        System.out.println(reviewer1ID);

        user.name("5");
        user.surname("Doe");
        user.email(rng.nextInt() + "@tudelt.nl");
        submitter = sendRequest(RequestType.POST, user, User.class, usersURL, "user");
        chair1 = submitter;

        event1 = new Event();
        event1.name("Test Event 1");
        event1.generalChairs(List.of());
        event1.description("This is a test event.");
        event1.setId(rng.nextLong());
        /*var role = new Role();
        role.setEventId(event1.getId());
        role.setUserId(chair1.getId());
        role.setRoleName("PCchair");
        chair1.addRolesItem(role);*/
        event1 = sendRequest(RequestType.POST, event1, Event.class, usersURL, "event");
        System.out.println(event1);

        track1 = new Track();
        track1.setId((long) rng.nextInt());
        track1.name("Test Track 1");
        track1.maxLength(10000);
        track1.description("This is a test track.");
        track1.setDeadline(System.currentTimeMillis() + 1000);
        track1 = sendRequest(RequestType.POST, track1, Track.class, usersURL, "track",
                event1.getId().toString());
        System.out.println(track1);

        // Make chair1 PCchair of track1
        sendRequest(RequestType.PUT, null, null, usersURL, "event", event1.getId().toString(),
                track1.getId().toString(), "role",
                chair1.getId().toString() + "?Assignee=" + chair1.getId().toString()
                        + "&roleType=PCchair");

        track2 = new Track();
        track2.setId((long) rng.nextInt());
        track2.name("Test Track 2");
        track2.maxLength(10000);
        track2.description("This is a test track.");
        track2.setDeadline(System.currentTimeMillis() + 1000);
        track2 = sendRequest(RequestType.POST, track2, Track.class, usersURL, "track",
                event1.getId().toString());
        System.out.println(track2);

        sendRequest(RequestType.PUT, null, null, usersURL, "event", event1.getId().toString(),
                track1.getId().toString(), "role",
                submitter1ID + "?Assignee=" + chair1.getId().toString()
                        + "&roleType=Author");

        Submission fakeSubmission = new Submission();
        fakeSubmission.setTitle("Title 1");
        fakeSubmission.setKeywords(List.of("Keywords"));
        fakeSubmission.setAbstract("Abstract 1");
        fakeSubmission.setPaper("Content".getBytes());
        fakeSubmission.setEventId(event1.getId());
        fakeSubmission.setTrackId(track1.getId());
        fakeSubmission.setLinkToReplicationPackage("https://github.com");
        var submitter1 = new nl.tudelft.sem.v20232024.team08b.dtos.submissions.User();
        submitter1.setUserId(submitter1ID);
        submitter1.setEmail("email@tudelft.nl");
        submitter1.setName("1");
        submitter1.setSurname("Doe");
        fakeSubmission.setAuthors(List.of(submitter1));
        fakeSubmission.setConflictsOfInterest(List.of());
        fakeSubmission = sendRequest(RequestType.POST, fakeSubmission, Submission.class,
                submissionsURL, "submission", submitter1ID.toString());
        submission1ID = fakeSubmission.getSubmissionId();
        System.out.println(fakeSubmission);

        sendRequest(RequestType.PUT, null, null, usersURL, "event", event1.getId().toString(),
                track1.getId().toString(), "role",
                submitter2ID + "?Assignee=" + chair1.getId().toString()
                        + "&roleType=Author");

        fakeSubmission = new Submission();
        fakeSubmission.setTitle("Title 2");
        fakeSubmission.setKeywords(List.of("Keywords"));
        fakeSubmission.setAbstract("Abstract 2");
        fakeSubmission.setPaper("Content".getBytes());
        fakeSubmission.setEventId(event1.getId());
        fakeSubmission.setTrackId(track1.getId());
        fakeSubmission.setLinkToReplicationPackage("https://github.com");
        var submitter2 = new nl.tudelft.sem.v20232024.team08b.dtos.submissions.User();
        submitter1.setUserId(submitter1ID);
        submitter1.setEmail("email@tudelft.nl");
        submitter1.setName("1");
        submitter1.setSurname("Doe");
        fakeSubmission.setAuthors(List.of(submitter1));
        fakeSubmission.setConflictsOfInterest(List.of());
        fakeSubmission = sendRequest(RequestType.POST, fakeSubmission, Submission.class,
                submissionsURL, "submission", submitter1ID.toString());
        submission2ID = fakeSubmission.getSubmissionId();
        System.out.println(fakeSubmission);

        sendRequest(RequestType.PUT, null, null, usersURL, "event", event1.getId().toString(),
                track2.getId().toString(), "role",
                submitter3ID + "?Assignee=" + chair1.getId().toString()
                        + "&roleType=Author");

        fakeSubmission = new Submission();
        fakeSubmission.setTitle("Title 3");
        fakeSubmission.setKeywords(List.of("Keywords"));
        fakeSubmission.setAbstract("Abstract 3");
        fakeSubmission.setPaper("Content".getBytes());
        fakeSubmission.setEventId(event1.getId());
        fakeSubmission.setTrackId(track2.getId());
        fakeSubmission.setLinkToReplicationPackage("https://github.com");
        var submitter3 = new nl.tudelft.sem.v20232024.team08b.dtos.submissions.User();
        submitter1.setUserId(submitter3ID);
        submitter1.setEmail("email@tudelft.nl");
        submitter1.setName("1");
        submitter1.setSurname("Doe");
        fakeSubmission.setAuthors(List.of(submitter3));
        fakeSubmission.setConflictsOfInterest(List.of());
        fakeSubmission = sendRequest(RequestType.POST, fakeSubmission, Submission.class,
                submissionsURL, "submission", submitter3ID.toString());
        submission3ID = fakeSubmission.getSubmissionId();
        System.out.println(fakeSubmission);

        track1ID = track1.getId();
        track2ID = track2.getId();
        event1ID = event1.getId();
        chair1ID = chair1.getId();

        // Give reviewer 1 PCmember role in track 1
        sendRequest(RequestType.PUT, null, null, usersURL, "event", event1.getId().toString(),
                track1.getId().toString(), "role",
                reviewer1ID + "?Assignee=" + chair1.getId().toString()
                        + "&roleType=PCmember");
    }

    @Test
    void ReviewersCanSeeTheTitlesAndAbstractsOfSubmittedPapers() throws Exception {
        var conferenceID = event1ID;
        var trackID = track1ID;
        var paper1 = new PaperSummaryWithID();
        paper1.setTitle("Title 1");
        paper1.setAbstractSection("Abstract 1");
        paper1.setPaperID(submission1ID);
        var paper2 = new PaperSummaryWithID();
        paper2.setTitle("Title 2");
        paper2.setAbstractSection("Abstract 2");
        paper2.setPaperID(submission2ID);
        var papersSummaryWithIDS = new ArrayList<PaperSummaryWithID>();
        papersSummaryWithIDS.add(paper1);
        papersSummaryWithIDS.add(paper2);
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        var response =
                testRestTemplate.getForEntity(reviewsURL + "/conferences/" + conferenceID +
                        "/tracks" +
                        "/" + track1ID
                        + "/papers?requesterID=" + chair1ID, Object.class);
        System.out.println(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(papersSummaryWithIDS, response.getBody());

        /*var roles = new RolesOfUser();
        var role = new RolesOfUserTracksInner();
        role.setEventId(event1ID);
        // TODO make sure the names of the roles correspond to the ones the Users team is using
        //  it seems they are a bit inconsistent about it
        role.setRoleName("PC Chair");
        role.setTrackId(track1ID);
        roles.setTracks(List.of(role));
        when(externalRepository.getRolesOfUser(chair1ID)).thenReturn(roles);

        roles = new RolesOfUser();
        role = new RolesOfUserTracksInner();
        role.setEventId(event1ID);
        role.setTrackId(track1ID);
        role.setRoleName("PC Member");
        roles.setTracks(List.of(role));
        when(externalRepository.getRolesOfUser(reviewer1ID)).thenReturn(roles);*/

        /*mockMvc.perform(MockMvcRequestBuilders.get("/conferences/{conferenceID}/tracks/{trackID" +
                                        "}/papers",
                        conferenceID,
                        trackID)
                .param("requesterID", chair1ID.toString()))
                        .andExpect(MockMvcResultMatchers.status().is(200))
                        .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(papersSummaryWithIDS)));
        */
    }

    <T, U> U sendRequest(RequestType requestType, T body, Class<T> expectedResponseClass,
                         String... url) {
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(String.join("/", url)))
                    .header("Content-Type", "application/json")
                    .method(requestType.toString(), body == null ?
                            HttpRequest.BodyPublishers.noBody() :
                            HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body))
                    ).build();
        } catch (JsonProcessingException e) {
            System.out.println("[Systems Test Error] Failed to write request body to JSON.");
            throw new RuntimeException(e);
        }

        HttpResponse response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("[Systems Test Error] Failed to send request.");
            throw new RuntimeException(e);
        }

        switch (HttpStatus.valueOf(response.statusCode())) {
            case OK, CREATED -> {
                try {
                    if (expectedResponseClass == null) {
                        return null;
                    }
                    return (U) objectMapper.readValue((String) response.body(), expectedResponseClass);
                } catch (JsonProcessingException e) {
                    System.out.println("[Systems Test Error] Failed to parse response body.");
                    throw new RuntimeException(e);
                }
            }
            /*case NOT_FOUND -> throw new NotFoundException("404 NOT FOUND");
            case FORBIDDEN, UNAUTHORIZED -> throw new ForbiddenAccessException();
            case BAD_REQUEST -> throw new BadHttpRequest();*/
            default -> {
                System.out.println("[Systems Testing Error] Request was: " + request.toString());
                throw new RuntimeException("Unexpected response status: " + response.statusCode());
            }
        }
    }

    enum RequestType {
        GET, POST, PUT, DELETE
    }
}
