package nl.tudelft.sem.v20232024.team08b.bid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.v20232024.team08b.domain.Bid;
import nl.tudelft.sem.v20232024.team08b.repos.BidRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class MockBidRepository implements BidRepository {
    public final List<Bid> bids = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<Bid> findAll() {
        return null;
    }

    @Override
    public List<Bid> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Bid> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Bid> findAllById(Iterable<Long> longs) {
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
    public void delete(Bid entity) {

    }

    @Override
    public void deleteAll(Iterable<? extends Bid> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Bid> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Bid> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Bid> findById(Long aLong) {
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
    public <S extends Bid> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<Bid> entities) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Bid getOne(Long aLong) {
        return null;
    }

    @Override
    public <S extends Bid> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Bid> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Bid> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Bid> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Bid> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Bid> boolean exists(Example<S> example) {
        return false;
    }
}
