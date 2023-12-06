package nl.tudelft.sem.v20232024.team08b.paper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.v20232024.team08b.domain.Paper;
import nl.tudelft.sem.v20232024.team08b.repos.PaperRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class MockPaperRepository implements PaperRepository {
    public final List<Paper> papers = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public List<Paper> findAll() {
        return null;
    }

    @Override
    public List<Paper> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Paper> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Paper> findAllById(Iterable<Long> longs) {
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
    public void delete(Paper entity) {

    }

    @Override
    public void deleteAll(Iterable<? extends Paper> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Paper> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Paper> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Paper> findById(Long aLong) {
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
    public <S extends Paper> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<Paper> entities) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Paper getOne(Long aLong) {
        return null;
    }

    @Override
    public <S extends Paper> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Paper> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Paper> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Paper> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Paper> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Paper> boolean exists(Example<S> example) {
        return false;
    }
}
