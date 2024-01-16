package nl.tudelft.sem.v20232024.team08b.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.application.verification.PapersVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.TracksVerification;
import nl.tudelft.sem.v20232024.team08b.application.verification.UsersVerification;
import nl.tudelft.sem.v20232024.team08b.domain.Bid;
import nl.tudelft.sem.v20232024.team08b.domain.Paper;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.domain.Track;
import nl.tudelft.sem.v20232024.team08b.domain.TrackID;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictOfInterestException;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.repos.BidRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssignmentsService {
    private final ReviewRepository reviewRepository;
    private final BidRepository bidRepository;
    private final TrackRepository trackRepository;
    private final PapersVerification papersVerification;
    private final TracksVerification tracksVerification;
    private final UsersVerification usersVerification;

    /**
     * Default constructor for the service.
     *
     * @param reviewRepository repository storing the reviews
     * @param trackRepository object responsible for verifying track information
     * @param papersVerification object responsible for verifying paper information
     * @param tracksVerification object responsible for verifying track information
     * @param usersVerification object responsible for verifying user information
     */
    @Autowired
    public AssignmentsService(BidRepository bidRepository,
                              ReviewRepository reviewRepository,
                              TrackRepository trackRepository,
                              PapersVerification papersVerification,
                              TracksVerification tracksVerification,
                              UsersVerification usersVerification) {
        this.bidRepository = bidRepository;
        this.reviewRepository = reviewRepository;
        this.trackRepository = trackRepository;
        this.papersVerification = papersVerification;
        this.tracksVerification = tracksVerification;
        this.usersVerification = usersVerification;
    }

    /**
     * This method verifies the permission to do certain tasks.
     *
     * @param userID ID of a user
     * @param paperID ID of a paper
     * @param role Role of the user
     * @return returns true if user has permission
     * @throws IllegalAccessException when the user does not have a permission
     * @throws NotFoundException when there is no reviewer with this ID in this track
     * @throws ConflictOfInterestException when there is conflict of interest
     */
    public boolean verifyIfUserCanAssign(Long userID, Long paperID, UserRole role)
        throws IllegalAccessException, NotFoundException, ConflictOfInterestException {
        switch (role) {
            case CHAIR:
                if (!usersVerification.verifyRoleFromPaper(userID, paperID, UserRole.CHAIR)) {
                    throw new IllegalAccessException("You are not PC chair for this track");
                }
                break;
            case REVIEWER:
                if (!usersVerification.verifyRoleFromPaper(userID, paperID, UserRole.REVIEWER)) {
                    throw new NotFoundException("There is no such a user in this track");
                }
                papersVerification.verifyCOI(paperID, userID);
                break;
            default:
                throw new IllegalAccessException("You are not pc chair for this track");
        }

        return true;
    }

    /**
     * This method assigns manually reviewer to a paper.
     *
     * @param requesterID ID of a requester
     * @param reviewerID ID of a reviewer to be assigned
     * @param paperID ID of a paper to which the reviewer will be assigned
     * @throws IllegalAccessException If the requester does not have a permission to assign
     * @throws NotFoundException If the reviewer is not in the track of paper
     * @throws ConflictOfInterestException If reviewer can not be assigned due to conflict of interest
     */
    public void assignManually(Long requesterID, Long reviewerID, Long paperID)
        throws IllegalAccessException, NotFoundException, ConflictOfInterestException {
        tracksVerification.verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.ASSIGNING));

        verifyIfUserCanAssign(requesterID, paperID, UserRole.CHAIR);
        verifyIfUserCanAssign(reviewerID, paperID, UserRole.REVIEWER);
        tracksVerification.verifyIfTrackExists(paperID);

        ReviewID reviewID = new ReviewID(paperID, reviewerID);
        Review toSave = new Review();
        toSave.setReviewID(reviewID);
        reviewRepository.save(toSave);

    }

    /**
     * Method returns the list of all ID's of a reviewers for requested paper.
     *
     * @param requesterID ID of a requester
     * @param paperID ID of a requested paper
     * @return List of ID's of reviewers
     * @throws IllegalAccessException If the requester does not have permissions to see the assignments
     */
    public List<Long> assignments(Long requesterID, Long paperID) throws IllegalAccessException, NotFoundException {
        if (!papersVerification.verifyPaper(paperID)) {
            throw new NotFoundException("this paper does not exist");
        }
        if (!usersVerification.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)) {
            throw new IllegalAccessException("Only pc chairs are allowed to do that");
        }
        tracksVerification.verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.ASSIGNING,
            TrackPhase.FINAL, TrackPhase.REVIEWING));
        List<Long> userIds = new ArrayList<>();
        List<Review> reviews = reviewRepository.findByReviewIDPaperID(paperID);
        for (Review review : reviews) {
            userIds.add(review.getReviewID().getReviewerID());
        }
        return userIds;
    }

    /**
     * This method assigns automatically reviewers to papers.
     *
     * @param requesterID ID of a requester
     * @param conferenceID ID of a conferenceID
     * @param trackID ID of a trackID
     * @throws IllegalAccessException If the requester does not have a permission to assign
     * @throws NotFoundException If the reviewer is not in the track of paper
     * @throws ConflictOfInterestException If reviewer can not be assigned due to conflict of interest
     */
    public void assignAuto(Long requesterID, Long conferenceID, Long trackID)
        throws NotFoundException, IllegalAccessException {
        verificationOfTrack(conferenceID, trackID, requesterID);

        TrackID trackID1 = new TrackID(conferenceID, trackID);
        Optional<Track> opTrack = trackRepository.findById(trackID1);
        Track track = opTrack.get();
        List<Paper> papers = track.getPapers();
        for (Paper paper : papers) {
            List<Bid> bids = bidRepository.getBidsOfPapers(requesterID, paper.getId());
            List<Long> users = bids.stream().map(Bid::getBidderID).collect(Collectors.toList());
            if (users.isEmpty()) {
                throw new IllegalArgumentException("At least One reviewer needed");
            }
            List<Integer> numberOfPapers = new ArrayList<>();
            for (Long user : users) {
                 numberOfPapers.add(byReviewer(user).size());
            }
            List<Integer> threeSmallest = gettingSmallest(numberOfPapers);
            for (Integer index : threeSmallest) {
                ReviewID reviewID = new ReviewID(paper.getId(), users.get(index));
                Review toSave = new Review();
                toSave.setReviewID(reviewID);
                reviewRepository.save(toSave);

            }
        }


    }

    public List<Paper> byReviewer(Long UserID) {
        return new ArrayList<>();
    }

    private void verificationOfTrack(Long conferenceID, long trackID, long requesterID)
        throws IllegalAccessException, NotFoundException {
        if (!tracksVerification.verifyTrack(conferenceID, trackID)) {
            throw new NotFoundException("No such track exists");
        }
        // Check if such user exists and has correct privileges
        if (!usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID,
            UserRole.REVIEWER)) {
            throw new NotFoundException("No such user exists");
        }
        // Check if such user exists and has correct privileges
        if (!usersVerification.verifyRoleFromTrack(requesterID, conferenceID, trackID,
            UserRole.CHAIR)) {
            throw new IllegalAccessException("User is not a PC chair");
        }

    }

    /**
     * Method that gets the three smallest elements of an array.
     *
     * @param numberOfPapers A list corresponding to the number of papers of every reviewer
     * @return an array with the 3 smallest elements
     */
    private List<Integer> gettingSmallest(List<Integer> numberOfPapers) {
        List<Integer> smallest = new ArrayList<>();
        int numberOfRepeats = 3;
        if (numberOfPapers.size() < numberOfRepeats) {
            numberOfRepeats = numberOfPapers.size();
        }
        for (int j = 0; j < numberOfRepeats; j++) {
            int minIndex = 0;
            // Find the index of the minimum element
            for (int k = 1; k < numberOfPapers.size(); k++) {
                if (numberOfPapers.get(k) < numberOfPapers.get(minIndex)) {
                    minIndex = k;
                }
            }
            // Store the indices of the smallest elements and set the chosen minimum to a large value
            smallest.add(minIndex);
            numberOfPapers.set(minIndex, Integer.MAX_VALUE);
        }
        return smallest;

    }
}
