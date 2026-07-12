package com.snookerup.services;

import com.snookerup.model.db.nosql.Routine;
import com.snookerup.model.addedcontext.PracticeSessionRoutineWithRoutineContext;
import com.snookerup.model.addedcontext.ScoreWithRoutineContext;
import com.snookerup.model.db.Score;
import com.snookerup.model.db.nosql.PracticeSessionRoutine;
import com.snookerup.repositories.RoutineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Implementation of the RoutineService interface, providing operations related to routines.
 *
 * @author Huw
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RoutineServiceImpl implements RoutineService {

    /** The MongoRepository implementation for this collection, where the bulk of our queries will go to. */
    private final RoutineRepository routineRepository;

    /** A MongoTemplate instance, for queries that don't quite fit into the MongoRepository interface. */
    private final MongoTemplate mongoTemplate;

    /**
     * All the tags in the collection of routines. We can safely only call it from the DB and store it since new
     * routines are not added outside of an app restart.
     */
    private List<String> allTags = null;

    @Override
    public Page<Routine> getRoutines(String tag, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();

        // 1. Handle optional search term
        if (search != null && !search.trim().isEmpty()) {
            // Escape special regex characters to prevent syntax issues if users type things like keys/brackets
            String escapedSearch = Pattern.quote(search.trim());

            // "i" flag makes it case-insensitive
            // The query will look for anything *containing* the escaped string
            criteriaList.add(Criteria.where("title").regex(escapedSearch, "i"));
        }

        // 2. Handle optional List of tags field (Matches if the DB array contains ANY of these values)
        if (tag != null && !tag.isEmpty()) {
            criteriaList.add(Criteria.where("tags").in(tag));
        }

        // 3. Link criteria with an AND operator if any exist
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        // 4. Fetch the paginated data
        List<Routine> list = mongoTemplate.find(query, Routine.class);

        // 5. Wrap inside a Page object using PageableExecutionUtils
        // (It optimizes performance by avoiding the count query if it's the first/only page)
        return PageableExecutionUtils.getPage(
                list,
                pageable,
                () -> mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Routine.class)
        );
    }

    @Override
    public List<Routine> getAllRoutines() {
        return routineRepository.findAll();
    }

    @Override
    public Optional<Routine> getRoutineById(String id) {
        return routineRepository.findByRoutineId(id);
    }

    @Override
    public List<String> getAllTags() {
        if (allTags == null) {
            // Don't use the repository here, since MongoTemplate has explicit native support for "distinct"
            allTags = mongoTemplate.findDistinct("tags", Routine.class, String.class);
        }
        return allTags;
    }

    @Override
    public List<Routine> getRoutinesForTag(String tag) {
        return routineRepository.findByTags(tag);
    }

    @Override
    public Routine getRandomRoutine() {
        return routineRepository.getRandomRoutine();
    }

    @Override
    public ScoreWithRoutineContext addRoutineContextToScore(Score score) {
        Routine routineForScore = routineRepository.findByRoutineId(score.getRoutineId()).get();
        return ScoreWithRoutineContext.builder()
                .score(score)
                .routineForScore(routineForScore)
                .build();
    }

    @Override
    public PracticeSessionRoutineWithRoutineContext addRoutineContextToPracticeSessionRoutine(
            PracticeSessionRoutine practiceSessionRoutine) {
        Routine routineForScore = routineRepository.findByRoutineId(practiceSessionRoutine.getRoutineId()).get();
        return PracticeSessionRoutineWithRoutineContext.builder()
                .routineWithVariations(practiceSessionRoutine)
                .routineContext(routineForScore)
                .build();
    }
}
