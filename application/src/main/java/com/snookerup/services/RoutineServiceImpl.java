package com.snookerup.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snookerup.model.AllRoutines;
import com.snookerup.model.Routine;
import com.snookerup.model.ScoreWithRoutineContext;
import com.snookerup.model.db.Score;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the RoutineService interface, providing operations related to routines.
 *
 * @author Huw
 */
@Service
@Slf4j
public class RoutineServiceImpl implements RoutineService, CommandLineRunner {

    /** The name of the file containing all the routine file names. */
    public static final String ALL_ROUTINES_JSON_FILE = "routines/all-routines.json";

    private final Map<String, Routine> routineIdToRoutines = new HashMap<>();
    private final Set<String> allTags = new HashSet<>();
    private final Map<String, Set<Routine>> tagsToRoutines = new HashMap<>();
    private final List<Routine> allRoutines = new ArrayList<>();
    private final Random randomGenerator = new Random();

    @Override
    public void run(String... args) throws Exception {
        log.debug("Loading routine config JSON files into memory");
        ObjectMapper objectMapper = new ObjectMapper();
        AllRoutines allRoutinesFromFile = objectMapper.readValue(new ClassPathResource(ALL_ROUTINES_JSON_FILE).getInputStream(),
                AllRoutines.class);
        List<String> routineFileNames = allRoutinesFromFile.getRoutineFileNames();
        for (String routineFileName : routineFileNames) {
            log.debug("Routine file name={}", routineFileName);
            Routine routine = objectMapper.readValue(new ClassPathResource(routineFileName).getInputStream(), Routine.class);
            log.debug("Parsed routine={}", routine);
            allRoutines.add(routine);
            routineIdToRoutines.put(routine.getId(), routine);
            allTags.addAll(routine.getTags());
            for (String tag : routine.getTags()) {
                tagsToRoutines.computeIfAbsent(tag, k -> new HashSet<>()).add(routine);
            }
        }
    }

    @Override
    public List<Routine> getAllRoutines() {
        return allRoutines;
    }

    @Override
    public Optional<Routine> getRoutineById(String id) {
        return Optional.ofNullable(routineIdToRoutines.get(id));
    }

    @Override
    public List<String> getAllTags() {
        return allTags.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public List<Routine> getRoutinesForTag(String tag) {
        Set<Routine> routines = tagsToRoutines.get(tag);
        if (routines == null) {
            return Collections.emptyList();
        }
        return routines.stream().collect(Collectors.toList());
    }

    @Override
    public Routine getRandomRoutine() {
        int random = randomGenerator.nextInt(allRoutines.size());
        return allRoutines.get(random);
    }

    @Override
    public ScoreWithRoutineContext addRoutineContextToScore(Score score) {
        Routine routineForScore = routineIdToRoutines.get(score.getRoutineId());
        return ScoreWithRoutineContext.builder()
                .score(score)
                .routineForScore(routineForScore)
                .build();
    }
}
