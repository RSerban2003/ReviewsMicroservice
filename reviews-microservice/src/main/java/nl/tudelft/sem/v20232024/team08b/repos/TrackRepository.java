package nl.tudelft.sem.v20232024.team08b.repos;

import nl.tudelft.sem.v20232024.team08b.domain.Track;
import nl.tudelft.sem.v20232024.team08b.domain.TrackID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TrackRepository extends JpaRepository<Track, TrackID> {
}
