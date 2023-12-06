package nl.tudelft.sem.v20232024.team08b.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.v20232024.team08b.domain.Paper;
import nl.tudelft.sem.v20232024.team08b.domain.Review;
import nl.tudelft.sem.v20232024.team08b.repos.ReviewRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class TestReviewRepository implements ReviewRepository {
    public final List<Review> reviews = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }
    @Override
    public List<Review> findAll() {
        return null;
    }

    @Override
    public List<Review> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Review> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Review> findAllById(Iterable<Long> longs) {
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
    public void delete(Review entity) {

    }

    @Override
    public void deleteAll(Iterable<? extends Review> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Review> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Review> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Review> findById(Long aLong) {
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
    public <S extends Review> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<Review> entities) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Review getOne(Long aLong) {
        return null;
    }

    @Override
    public <S extends Review> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Review> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Review> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Review> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Review> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Review> boolean exists(Example<S> example) {
        return false;
    }
}
