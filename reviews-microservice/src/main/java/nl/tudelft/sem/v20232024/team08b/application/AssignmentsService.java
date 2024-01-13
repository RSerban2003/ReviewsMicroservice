package nl.tudelft.sem.v20232024.team08b.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import nl.tudelft.sem.v20232024.team08b.domain.Bid;
import nl.tudelft.sem.v20232024.team08b.domain.Paper;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.domain.Track;
import nl.tudelft.sem.v20232024.team08b.domain.TrackID;
import nl.tudelft.sem.v20232024.team08b.domain.User;
import nl.tudelft.sem.v20232024.team08b.exceptions.ConflictOfInterestException;
import nl.tudelft.sem.v20232024.team08b.dtos.review.TrackPhase;
import nl.tudelft.sem.v20232024.team08b.dtos.review.UserRole;
import nl.tudelft.sem.v20232024.team08b.repos.BidRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignmentsService {
    private final ReviewRepository reviewRepository;
    private final BidRepository bidRepository;

    private final VerificationService verificationService;
    private final TrackRepository trackRepository;

    /**
     * Default constructor for the service.
     *
     * @param bidRepository repository storing the bids
     * @param reviewRepository repository storing the reviews
     * @param trackRepository
     */
    @Autowired
    public AssignmentsService(BidRepository bidRepository,
                              ReviewRepository reviewRepository, VerificationService verificationService,
                              TrackRepository trackRepository) {
        this.bidRepository = bidRepository;
        this.reviewRepository = reviewRepository;
        this.verificationService = verificationService;
        this.trackRepository = trackRepository;
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
                if (!verificationService.verifyRoleFromPaper(userID, paperID, UserRole.CHAIR)) {
                    throw new IllegalAccessException("You are not PC chair for this track");
                }
                break;
            case REVIEWER:
                if (!verificationService.verifyRoleFromPaper(userID, paperID, UserRole.REVIEWER)) {
                    throw new NotFoundException("There is no such a user in this track");
                }
                verificationService.verifyCOI(paperID, userID);
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
        verificationService.verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.ASSIGNING));

        verifyIfUserCanAssign(requesterID, paperID, UserRole.CHAIR);
        verifyIfUserCanAssign(reviewerID, paperID, UserRole.REVIEWER);
        verificationService.verifyIfTrackExists(paperID);

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
        if (!verificationService.verifyPaper(paperID)) {
            throw new NotFoundException("this paper does not exist");
        }
        if (!verificationService.verifyRoleFromPaper(requesterID, paperID, UserRole.CHAIR)) {
            throw new IllegalAccessException("Only pc chairs are allowed to do that");
        }
        verificationService.verifyTrackPhaseThePaperIsIn(paperID, List.of(TrackPhase.ASSIGNING,
            TrackPhase.FINAL, TrackPhase.REVIEWING));
        List<Long> userIds = new ArrayList<>();
        List<Review> reviews = reviewRepository.findByReviewIDPaperID(paperID);
        for (Review review : reviews) {
            userIds.add(review.getReviewID().getReviewerID());
        }
        return userIds;
    }

    /**
     * This method assigns automatically reviewers to papers
     *
     * @param requesterID ID of a requester
     * @param conferenceID ID of a conferenceID
     * @param trackID ID of a trackID
     * @throws IllegalAccessException If the requester does not have a permission to assign
     * @throws NotFoundException If the reviewer is not in the track of paper
     * @throws ConflictOfInterestException If reviewer can not be assigned due to conflict of interest
     */
     public void assignAuto(Long requesterID, Long conferenceID, Long trackID) throws NotFoundException, IllegalAccessException {
         // Check if such paper exists
         if (!verificationService.verifyTrack(conferenceID,trackID)) {
             throw new NotFoundException("No such track exists");
         }
         // Check if such user exists and has correct privileges
         if (!verificationService.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.REVIEWER)) {
             throw new NotFoundException("No such user exists");
         }
         // Check if such user exists and has correct privileges
         if (!verificationService.verifyRoleFromTrack(requesterID, conferenceID, trackID, UserRole.CHAIR)) {
             throw new IllegalAccessException("User is not a PC chair");
         }
         TrackID trackID1 = new TrackID(conferenceID,trackID);
         Optional<Track> opTrack = trackRepository.findById(trackID1);
         Track track = opTrack.get();
         List<Paper> papers = track.getPapers();
         for (Paper paper: papers){
             List<Bid> bids = bidRepository.getBidsOfPapers(requesterID, paper.getId());
             List<User> users = bids.stream().map(Bid::getBidder).collect(Collectors.toList());
             if (1 > users.size()) {
                 throw new IllegalArgumentException("At least One reviewer needed");
             }
             List<Integer> numberOfPapers = new ArrayList<>();
             for (User user: users) {
                 numberOfPapers.add(byReviewer(user.getId()).size());
             }
             int min0 = 0;
             int min1 = 1;
             int min2 = 2;
             for (int i = 0; i < numberOfPapers.size(); i++) {

                 for (int j = 0; j < 3; j++) {
                     int minIndex = 0;

                     // Find the index of the minimum element
                     for (int k = 1; k < numberOfPapers.size(); k++) {
                         if (numberOfPapers.get(k) < numberOfPapers.get(minIndex)) {
                             minIndex = k;
                         }
                     }

                     // Store the indices of the smallest elements and set the chosen minimum to a large value
                     if (j == 0) {
                         min0 = minIndex;
                     } else if (j == 1) {
                         min1 = minIndex;
                     } else if (j == 2) {
                         min2 = minIndex;
                     }

                     numberOfPapers.set(minIndex,Integer.MAX_VALUE);
                 }
                 ReviewID reviewID1 = new ReviewID(paper.getId(), users.get(min0).getId());
                 ReviewID reviewID2 = new ReviewID(paper.getId(), users.get(min0).getId());
                 ReviewID reviewID3 = new ReviewID(paper.getId(), users.get(min0).getId());
                 Review toSave1 = new Review();
                 Review toSave2 = new Review();
                 Review toSave3 = new Review();
                 toSave1.setReviewID(reviewID1);
                 toSave2.setReviewID(reviewID2);
                 toSave3.setReviewID(reviewID3);
                 reviewRepository.save(toSave1);
                 reviewRepository.save(toSave2);
                 reviewRepository.save(toSave3);

             }

         }



    }

    private List<Paper> byReviewer(Long UserID) {
         return new ArrayList<>();
    }
}
