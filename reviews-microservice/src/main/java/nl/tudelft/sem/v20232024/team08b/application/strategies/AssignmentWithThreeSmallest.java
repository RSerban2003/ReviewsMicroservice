package nl.tudelft.sem.v20232024.team08b.application.strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nl.tudelft.sem.v20232024.team08b.domain.Bid;
import nl.tudelft.sem.v20232024.team08b.domain.Paper;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.domain.ReviewID;
import nl.tudelft.sem.v20232024.team08b.repos.BidRepository;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.springframework.stereotype.Component;

@Component
public class AssignmentWithThreeSmallest implements AutomaticAssignmentStrategy{
  private final BidRepository bidRepository;
  private final ReviewRepository reviewRepository;
  public AssignmentWithThreeSmallest(BidRepository bidRepository,
                                     ReviewRepository reviewRepository) {

    this.bidRepository = bidRepository;
    this.reviewRepository = reviewRepository;
  }

  @Override
  public void automaticAssignment(List<Paper> papers, Long requesterID) {
    for (Paper paper : papers) {
      List<Bid> bids = bidRepository.getBidsOfPapers(requesterID, paper.getId());
      List<Long> users = bids.stream().map(Bid::getBidderID).collect(Collectors.toList());
      if (users.isEmpty()) {
        throw new IllegalArgumentException("At least One reviewer needed");
      }
      List<Integer> numberOfPapers = new ArrayList<>();
      for (Long user : users) {
        //byReviewer(user).size()
        numberOfPapers.add(0);
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

  /**
   * Method that gets the three smallest elements of an array.
   *
   * @param numberOfPapers A list corresponding to the number of papers of every reviewer
   * @return a list with the 3 smallest elements
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
