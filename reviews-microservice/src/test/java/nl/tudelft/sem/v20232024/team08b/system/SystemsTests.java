package nl.tudelft.sem.v20232024.team08b.system;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperSummary;
import nl.tudelft.sem.v20232024.team08b.dtos.review.PaperSummaryWithID;
import nl.tudelft.sem.v20232024.team08b.dtos.submissions.Submission;
import nl.tudelft.sem.v20232024.team08b.dtos.users.Event;
import nl.tudelft.sem.v20232024.team08b.dtos.users.Track;
import nl.tudelft.sem.v20232024.team08b.dtos.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.opentest4j.TestAbortedException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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

@ExtendWith(SpringExtension.class)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SystemsTests {
    private final TestRestTemplate testRestTemplate = new TestRestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final String submissionsURL = "http://localhost:8081";
    private final String usersURL = "http://localhost:8082";
    private final String reviewsURL = "http://localhost:8080";

    private Long submitter1ID;
    private Long submitter2ID;
    private Long submitter3ID;

    private Long reviewer1ID;

    private Long track1ID;
    private Long track2ID;
    private Long event1ID;
    private Long chair1ID;

    private Long submission1ID;
    private Long submission2ID;
    private Long submission3ID;

    @BeforeEach
    void setup() {
        // Verify that the other microservices are running
        try {
            sendRequest(RequestType.DELETE, null, Object.class, usersURL, "debug");
        } catch (Exception e) {
            if (e.getCause() != null && e.getCause().toString().contains("java.net" +
                    ".ConnectException")) {
                throw new TestAbortedException();
            }
        }
        System.out.println("[Systems Testing Log] Users microservice is running.");

        try {
            sendRequest(RequestType.GET, null, Object.class, submissionsURL, "submission",
                    "1000000");
        } catch (Exception e) {
            if (e.getCause() != null && e.getCause().toString().contains("java.net" +
                    ".ConnectException")) {
                throw new TestAbortedException();
            }
        }
        System.out.println("[Systems Testing Log] Submissions microservice is running.");

        // Make some test data
        Random rng = new Random();
        var user = new User();
        user.name("John");
        user.surname("Doe");
        user.setWebsite("www.tudelft.nl");
        user.email(rng.nextInt() + "@tudelft.nl");
        var submitter = (User) sendRequest(RequestType.POST, user, User.class, usersURL, "user");
        submitter1ID = submitter.getId();

        user.name("John");
        user.surname("Doe");
        user.setWebsite("www.tudelft.nl");
        user.email(rng.nextInt() + "@tudelt.nl");
        submitter = sendRequest(RequestType.POST, user, User.class, usersURL, "user");
        submitter2ID = submitter.getId();

        user.name("John");
        user.surname("Doe");
        user.setWebsite("www.tudelft.nl");
        user.email(rng.nextInt() + "@tudelt.nl");
        submitter = sendRequest(RequestType.POST, user, User.class, usersURL, "user");
        submitter3ID = submitter.getId();

        System.out.println(submitter1ID + " " + submitter2ID + " " + submitter3ID);

        user.name("John");
        user.surname("Doe");
        user.setWebsite("www.tudelft.nl");
        user.email(rng.nextInt() + "@tudelt.nl");
        submitter = sendRequest(RequestType.POST, user, User.class, usersURL, "user");
        reviewer1ID = submitter.getId();

        System.out.println(reviewer1ID);

        user.name("John");
        user.surname("Doe");
        user.website("www.tudelft.nl");
        user.email(rng.nextInt() + "@tudelt.nl");
        submitter = sendRequest(RequestType.POST, user, User.class, usersURL, "user");
        final var chair1 = submitter;

        var event1 = new Event();
        event1.name("TestEvent1");
        event1.generalChairs(List.of(chair1));
        event1.description("This is a test event.");
        event1 = sendRequest(RequestType.POST, event1, Event.class, usersURL, "event");
        System.out.println(event1);

        var track1 = new Track();
        track1.name("TestTrack1");
        track1.maxLength(10000);
        track1.description("This is a test track.");
        track1.setDeadline(System.currentTimeMillis() + 10000);
        track1 = sendRequest(RequestType.POST, track1, Track.class, usersURL, "track",
                event1.getId().toString());
        System.out.println(track1);

        // make chair1 pc chair of track1
        sendRequest(RequestType.PUT, null, null, usersURL, "event", event1.getId().toString(),
                track1.getId().toString(), "role",
                chair1.getId().toString() + "?Assignee=" + chair1.getId().toString()
                        + "&roleType=PCchair");

        sendRequest(RequestType.PUT, null, null, usersURL, "event", event1.getId().toString(),
                track1.getId().toString(), "role",
                chair1.getId().toString() + "?Assignee=" + chair1.getId().toString()
                        + "&roleType=PCchair");

        var track2 = new Track();
        track2.setId((long) rng.nextInt());
        track2.name("Test Track 2");
        track2.maxLength(10000);
        track2.description("This is a test track.");
        track2.setDeadline(System.currentTimeMillis() + 1000);
        track2 = sendRequest(RequestType.POST, track2, Track.class, usersURL, "track",
                event1.getId().toString());
        System.out.println(track2);

        // make chair1 pc chair of track2

        sendRequest(RequestType.PUT, null, null, usersURL, "event", event1.getId().toString(),
                track2.getId().toString(), "role",
                chair1.getId().toString() + "?Assignee=" + chair1.getId().toString()
                        + "&roleType=PCchair");

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
        User submitter1User = sendRequest(RequestType.GET, null, User.class, usersURL, "user",
                submitter1ID.toString());
        submitter1.setUserId(submitter1ID);
        submitter1.setEmail(submitter1User.getEmail());
        submitter1.setName(submitter1User.getName());
        submitter1.setSurname(submitter1.getSurname());
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
        User submitter2User = sendRequest(RequestType.GET, null, User.class, usersURL, "user",
                submitter1ID.toString());
        submitter2.setUserId(submitter1ID);
        submitter2.setEmail(submitter2User.getEmail());
        submitter2.setName(submitter2User.getName());
        submitter2.setSurname(submitter2.getSurname());
        fakeSubmission.setAuthors(List.of(submitter2));
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
        User submitter3User = sendRequest(RequestType.GET, null, User.class, usersURL, "user",
                submitter1ID.toString());
        submitter3.setUserId(submitter1ID);
        submitter3.setEmail(submitter3User.getEmail());
        submitter3.setName(submitter3User.getName());
        submitter3.setSurname(submitter3.getSurname());
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

        sendRequest(RequestType.PUT, null, null, usersURL, "event", event1.getId().toString(),
                track1.getId().toString(), "role",
                reviewer1ID + "?Assignee=" + chair1.getId().toString()
                        + "&roleType=PCmember");
    }


    /**
     * Tests Must-have Requirement #3: Reviewers can see all of the papers in their tracks.
     * Using endpoint: GET /conferences/{conferenceID}/tracks/{trackID}/papers
     */
    @Test
    void reviewersCanSeeSubmittedPapersInATrack() {
        var conferenceID = event1ID;
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

        var response = testRestTemplate.getForEntity(reviewsURL + "/conferences/" + event1ID +
                "/tracks/" + track1ID + "/papers?requesterID=" + chair1ID, Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(papersSummaryWithIDS, response.getBody());
    }

    /**
     * Tests Must-have Requirement #2: Reviewers can read the titles and abstracts of the submitted papers.
     * Using endpoint: GET /papers/{paperID}/title-and-abstract
     */
    @Test
    void reviewersCanSeeTitlesAndAbstractsOfPapers() {
        PaperSummaryWithID paper1 = new PaperSummaryWithID();
        paper1.setTitle("Title 1");
        paper1.setAbstractSection("Abstract 1");
        paper1.setPaperID(submission1ID);

        ResponseEntity<PaperSummary> response = testRestTemplate.getForEntity(reviewsURL + "/papers/" + submission1ID +
                "/title-and-abstract?requesterID=" + chair1ID, PaperSummary.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Title 1", response.getBody().getTitle());
        assertEquals("Abstract 1", response.getBody().getAbstractSection());
    }

    /**
     * Tests Could-have Requirement #2: Chairs can read the titles and abstracts of the submitted
     * papers, but not the authors of the papers.
     * Using endpoint: GET /papers/{paperID}/title-and-abstract
     */
    @Test
    void chairsCanReadTitlesAndAbstractsOfPapersInTheirTrackInBiddingPhase() {
        var paperSummaryWithoutID = new PaperSummary();
        paperSummaryWithoutID.setTitle("Title 1");
        paperSummaryWithoutID.setAbstractSection("Abstract 1");


        var response = testRestTemplate.getForEntity(reviewsURL + "/papers/" + submission1ID +
            "/title-and-abstract?requesterID=" + chair1ID, Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paperSummaryWithoutID, response.getBody());
    }

    /**
     * Tests Should-have Requirement #5: Reviewers can read the papers that they are assigned to.
     * Must-have Requirement #4: Reviewers should not see the author of a paper.
     * Using endpoint: GET /papers/{paperID}
     */
    @Test
    void reviewersCanReadPapersTheyAreAssignedTo() {
    }

    /**
     * Test Could-have Requirement #5: Chairs can read the papers that are a part of their track(s).
     * Using endpoint: GET /papers/{paperID}
     */
    @Test
    void chairsCanReadPapersInTheirTrackInAssignmentPhase() {

    }

    /**
     * Tests Must-have Requirement #5: Reviewers can decide to review/not review/stay neutral towards a paper.
     * Must-have Requirement #1: The bidding phase automatically starts when the submission deadline passes.
     * Must-have Requirement #6: The bidding phase must end after a certain deadline (default is a few days).
     * Using endpoints: PUT /papers/{paperID}/bid
     *                  GET /papers/{paperID}/bids/by-reviewer/{reviewerID}
     */
    @Test
    void reviewersCanBidOnPapers() {
    }

    /**
     * Tests Must-have Requirement #7: Chairs can initiate an automated process that assigns papers
     * based on the bidding information for papers in their track(s).
     * Must-have Requirement #8: Chairs can view the current assignments of the reviews of papers in their track(s).
     * Must-have Requirement #9: Each submitted paper must be reviewed by three reviewers.
     * Could-have Requirement #3: Reviewers should be assigned to similar amounts of papers
     * by the automatic assignment.
     * Using endpoints: PUT /conferences/{conferenceID}/tracks/{trackID}/automatic
     * GET /papers/{paperID}/assignees
     */
    @Test
    void chairsCanAssignPapersAutomatically() {
    }

    /**
     * Tests Must-have Requirement #10: Chairs can finalize the assignments, so they are no longer editable.
     * Must-have Requirement #9: Each submitted paper must be reviewed by three reviewers.
     * Using endpoints: POST /conferences/{conferenceID}/tracks/{trackID}/finalization
     * GET /conferences/{conferenceID}/tracks/{trackID}/phase
     */
    @Test
    void chairsCanFinalizeAssignments() {
    }

    /**
     * Tests Must-have Requirement #11: Reviewers can submit their review for each submission they are assigned to.
     * Must-have Requirement #12: Reviewers can resubmit their reviews
     * as long as the Review phase of the track is still ongoing.
     * Using endpoints: PUT /papers/{paperID}/reviews
     * GET /papers/{paperID}/reviews/by-reviewer/{reviewerID}
     */
    @Test
    void reviewersCanSubmitAndResubmitReviews() {
    }

    /**
     * Tests Must-have Requirement #14: Once all reviews for a paper are submitted, the Review phase
     * for this paper automatically ends, and the Discussion phase for the paper begins.
     * Using endpoint: PUT /papers/{paperID}/reviews
     * GET /papers/{paperID}/reviews/phase
     */
    @Test
    void discussionPhaseBeginsSuccessfully() {
    }

    /**
     * Tests Must-have Requirement #15: Reviewers can read the reviews of other reviewers
     * if they are assigned to the same paper.
     * Using endpoint: GET /papers/{paperID}/reviews/by-reviewer/{reviewerID}
     */
    @Test
    void reviewersCanReadOtherReviewsDuringDiscussionPhase() {
        discussionPhaseBeginsSuccessfully();
    }

    /**
     * Tests Must-have Requirement #16: Chairs can read the reviews of the papers in their track(s).
     * Using endpoint: GET /papers/{paperID}/reviews
     */
    @Test
    void chairsCanReadReviewsDuringDiscussionPhase() {
        discussionPhaseBeginsSuccessfully();
    }

    /**
     * Tests Must-have Requirement #17: Reviewers can edit their own reviews, by submitting them again.
     * Using endpoint: PUT /papers/{paperID}/reviews
     */
    @Test
    void reviewersCanEditTheirOwnReviewsDuringDiscussionPhase() {
        discussionPhaseBeginsSuccessfully();
    }

    /**
     * Tests Must-have Requirement #18: Once a Chair has approved the reviews for a paper,
     * the Discussion phase for this paper ends and the Final phase begins.
     * Must-have Requirement #19: Chairs can approve (finalize) the reviews for a paper
     * in their track(s) and only if the reviews for the paper are all positive or all negative.
     * Using endpoints: POST /papers/{paperID}/reviews/finalization
     * GET /papers/{paperID}/reviews/phase
     */
    @Test
    void chairsCanFinalizeReviewsDuringDiscussionPhase() {
        discussionPhaseBeginsSuccessfully();
    }

    /**
     * Tests Must-have Requirement #21: Chairs and Reviewers can see the finalized reviews
     * for the appropriate papers.
     * Could-have Requirement #8: Chairs view the finalized reviews of all papers in their track(s).
     * Using endpoint: GET /papers/{paperID}/reviews
     */
    @Test
    void chairsAndReviewersCanSeeFinalizedReviews() {
        chairsCanFinalizeReviewsDuringDiscussionPhase();
    }

    /**
     * Tests Must-have Requirement #22: Authors can check if their paper was accepted.
     * Must-have Requirement #20: If all scores for a paper are positive, the paper is accepted.
     * Using endpoint: GET /papers/{paperID}/status
     */
    @Test
    void authorsCanCheckTheStatusOfTheirPaper() {
        chairsCanFinalizeReviewsDuringDiscussionPhase();
    }

    /**
     * Tests Should-have Requirement #1: Reviewers cannot be assigned papers, for review,
     * if conflicts of interest (COIs) have been indicated.
     * Using endpoint: PUT /papers/{paperID}/bid
     */
    @Test
    void reviewersCannotBeAssignedToPapersIfCOI() {
    }

    /**
     * Tests Should-have Requirement #2: Chairs can manually assign papers in their track(s)
     * to each reviewer based on the bidding information.
     * Should-have Requirement #3: Chairs can edit any of the assignments before the finalization.
     * Should-have Requirement #4: Chairs can delete any of the assignments before the finalization.
     * Using endpoints: POST /papers/{paperID}/assignees/{reviewerID}
     * DELETE /papers/{paperID}/assignees/{reviewerID}
     * GET /papers/{paperID}/assignees
     */
    @Test
    void chairsCanManuallyAssignPapers() {
    }

    /**
     * Tests Should-have Requirement #6: Reviewers can write discussion comments on each other's reviews
     * Could-have Requirement #6: Chairs can also make discussion comments on the reviews.
     * Using endpoints: GET /papers/{paperID}/reviews/by-reviewer/{reviewerID}/discussion-comments
     * POST /papers/{paperID}/reviews/by-reviewer/{reviewerID}/discussion-comments
     */
    @Test
    void reviewersAndChairsCanWriteDiscussionCommentsDuringDiscussionPhase() {
        discussionPhaseBeginsSuccessfully();
    }

    /**
     * Tests Should-have Requirement #7: Authors can read the reviews for their papers
     * Must-have Requirement #13: Authors should never be shown the confidential comments,
     * review changes, and discussion comments under any circumstance.
     * Using endpoints: GET /papers/{paperID}/reviewers
     * GET /papers/{paperID}/reviews/by-reviewer/{reviewerID}
     */
    @Test
    void authorsCanReadReviews() {
        chairsCanFinalizeReviewsDuringDiscussionPhase();
    }

    /**
     * Tests Could-have Requirement #1: Before the bidding has started, Chairs can decide on the deadline
     * for the bidding phase for their track(s), i.e., the deadline for bidding for reviews
     * after the submission of papers.
     * Using endpoints: PUT /conferences/{conferenceID}/tracks/{trackID}/bidding-deadline
     * GET /conferences/{conferenceID}/tracks/{trackID}/bidding-deadline
     */
    @Test
    void chairsCanSetBiddingDeadline() {
    }

    /**
     * Tests Could-have Requirement #4: Chairs can view analytics for their track(s):
     * the number of papers that will be accepted, rejected, and papers with no final verdict.
     * Using endpoint: GET /conferences/{conferenceID}/tracks/{trackID}/analytics
     */
    @Test
    void chairsCanViewAnalytics() {
    }

    /**
     * Tests Could-have Requirement #7: Reviewers can check the final decision
     * of the papers they were assigned to.
     * Using endpoint: GET /papers/{paperID}/status
     */
    @Test
    void reviewersCanCheckStatusOfPaper() {
    }

    /**
     * Tests Requirements:
     * Must-have #23: We verify that the users are properly authenticated and have the correct
     * privileges for what they are trying to do.
     */
    @Test
    void verification() {
    }

    /**
     * Sends an http request.
     *
     * @param requestType           GET, POST, PUT, or DELETE
     * @param body                  The body of the request
     * @param expectedResponseClass The class of the expected response
     * @param url                   The url of the request
     * @param <T>                   The type of the request body
     * @param <U>                   The type of the response body
     * @return The response body converted to the expected class
     */
    <T, U> U sendRequest(RequestType requestType, T body, Class<T> expectedResponseClass,
                         String... url) {
        HttpRequest request;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(String.join("/", url)))
                    .header("Content-Type", "application/json")
                    .method(requestType.toString(), body == null ?
                            HttpRequest.BodyPublishers.noBody() :
                            HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body))
                    ).build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("[Systems Test Error] Failed to send request.");
            throw new RuntimeException(e);
        }

        switch (HttpStatus.valueOf(response.statusCode())) {
            case OK, CREATED -> {
                try {
                    return expectedResponseClass == null ? null :
                            (U) objectMapper.readValue((String) response.body(), expectedResponseClass);
                } catch (JsonProcessingException e) {
                    System.out.println("[Systems Test Error] Failed to parse response body.");
                    throw new RuntimeException(e);
                }
            }
            default -> {
                System.out.println("[Systems Testing Error] Request was: " + request.toString());
                throw new RuntimeException("Unexpected response status: " + response.statusCode());
            }
        }
    }

    /**
     * Enum for the type of request.
     */
    enum RequestType {
        GET, POST, PUT, DELETE
    }
}
