package nl.tudelft.sem.v20232024.team08b.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.v20232024.team08b.domain.Track;
import nl.tudelft.sem.v20232024.team08b.repos.TrackRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class MockTrackRepository implements TrackRepository {
    public final List<Track> tracks = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }
    @Override
    public List<Track> findAll() {
        return null;
    }

    @Override
    public List<Track> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Track> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Track> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Track entity) {

    }

    @Override
    public void deleteAll(Iterable<? extends Track> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Track> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Track> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Track> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Track> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<Track> entities) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Track getOne(Long aLong) {
        return null;
    }

    @Override
    public <S extends Track> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Track> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Track> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Track> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Track> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Track> boolean exists(Example<S> example) {
        return false;
    }
}
