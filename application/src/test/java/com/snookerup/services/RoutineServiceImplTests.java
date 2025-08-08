package com.snookerup.services;

import com.snookerup.model.Routine;
import com.snookerup.model.ScoreWithRoutineContext;
import com.snookerup.model.db.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the RoutineServiceImpl class.
 *
 * @author Huw
 */
class RoutineServiceImplTests {

    RoutineServiceImpl routineService;

    @BeforeEach
    public void beforeEach() {
        routineService = new RoutineServiceImpl();
    }

    @Test
    public void run_Should_PopulateRoutinesCollections() throws Exception {
        // Define variables

        // Set mock expectations

        // Execute method under test
        routineService.run();

        // Verify
        Map<String, Routine> routineIdToRoutines = (Map<String, Routine>) ReflectionTestUtils
                .getField(routineService, "routineIdToRoutines");
        Set<String> allTags = (Set<String>) ReflectionTestUtils.getField(routineService, "allTags");
        Map<String, Set<Routine>> tagsToRoutines = (Map<String, Set<Routine>>) ReflectionTestUtils
                .getField(routineService, "tagsToRoutines");
        List<Routine> allRoutines = (List<Routine>) ReflectionTestUtils.getField(routineService, "allRoutines");
        assertEquals(9, allRoutines.size());
        assertEquals(9, routineIdToRoutines.size());
        assertEquals(5, allTags.size());
        assertTrue(allTags.contains("break-building"));
        assertTrue(allTags.contains("positional-play"));
        assertTrue(allTags.contains("straight-cueing"));
        assertTrue(allTags.contains("match-situations"));
        assertTrue(allTags.contains("long-potting"));
        assertEquals(5, tagsToRoutines.size());
        assertEquals(7, tagsToRoutines.get("break-building").size());
        assertEquals(8, tagsToRoutines.get("positional-play").size());
        assertEquals(1, tagsToRoutines.get("straight-cueing").size());
        assertEquals(2, tagsToRoutines.get("match-situations").size());
        assertEquals(1, tagsToRoutines.get("long-potting").size());
    }

    @Test
    public void getAllRoutines_Should_ReturnAllRoutines() throws Exception {
        // Define variables

        // Set mock expectations

        // First call the run() method
        routineService.run();
        // Then execute method under test
        List<Routine> allRoutines = routineService.getAllRoutines();

        // Verify
        List<Routine> allRoutinesInService = (List<Routine>) ReflectionTestUtils.getField(routineService, "allRoutines");
        assertTrue(allRoutines.size() == allRoutinesInService.size());
        assertTrue(allRoutines.containsAll(allRoutinesInService));
    }

    @Test
    public void getRoutineById_Should_ReturnRoutine_When_IdExists() throws Exception {
        // Define variables

        // Set mock expectations

        // First call the run() method
        routineService.run();
        // Then execute method under test
        Optional<Routine> routine = routineService.getRoutineById("the-line-up");

        // Verify
        assertTrue(routine.isPresent());
        Routine routineInService = ((Map<String, Routine>) ReflectionTestUtils
                .getField(routineService, "routineIdToRoutines")).get("the-line-up");
        Optional<Routine> routineInServiceOpt = Optional.of(routineInService);
        assertEquals(routine, routineInServiceOpt);
    }

    @Test
    public void getRoutineById_Should_ReturnNull_When_IdDoesntExist() throws Exception {
        // Define variables

        // Set mock expectations

        // First call the run() method
        routineService.run();
        // Then execute method under test
        Optional<Routine> routine = routineService.getRoutineById("invalid-id");

        // Verify
        assertTrue(routine.isEmpty());
    }

    @Test
    public void getAllTags_Should_ReturnAllTagsAsList() throws Exception {
        // Define variables

        // Set mock expectations

        // First call the run() method
        routineService.run();
        // Then execute method under test
        List<String> allTags = routineService.getAllTags();

        // Verify
        List<String> allTagsInService = (List<String>) ((Set<String>) ReflectionTestUtils
                .getField(routineService, "allTags")).stream().collect(Collectors.toList());
        assertTrue(allTags.size() == allTagsInService.size());
        assertTrue(allTags.containsAll(allTagsInService));
    }

    @Test
    public void getRoutinesForTag_Should_ReturnListOfRoutinesForTag_When_TagExists() throws Exception {
        // Define variables
        String tag = "break-building";

        // Set mock expectations

        // First call the run() method
        routineService.run();
        // Then execute method under test
        List<Routine> routinesForTag = routineService.getRoutinesForTag(tag);

        // Verify
        List<Routine> routinesForTagsInService = (List<Routine>) ((Map<String, Set<Routine>>) ReflectionTestUtils
                .getField(routineService, "tagsToRoutines")).get(tag).stream().collect(Collectors.toList());
        assertTrue(routinesForTag.size() == routinesForTagsInService.size());
        assertTrue(routinesForTag.containsAll(routinesForTagsInService));
    }

    @Test
    public void getRoutinesForTag_Should_ReturnEmptyList_When_TagDoesntExist() throws Exception {
        // Define variables
        String tag = "invalid-tag";

        // Set mock expectations

        // First call the run() method
        routineService.run();
        // Then execute method under test
        List<Routine> routinesForTag = routineService.getRoutinesForTag(tag);

        // Verify
        assertTrue(routinesForTag.isEmpty());
    }

    @Test
    public void getRandomRoutine_Should_ReturnRoutine() throws Exception {
        // Define variables

        // Set mock expectations

        // First call the run() method
        routineService.run();
        // Then execute method under test
        Routine randomRoutine = routineService.getRandomRoutine();

        // Verify
        assertNotNull(randomRoutine);
    }

    @Test
    public void addRoutineContextToScore_Should_ReturnCreatedScoreWithRoutineContext() throws Exception {
        // Define variables
        Score score = new Score();
        score.setId(1L);
        score.setPlayerUsername("willo");
        score.setDateOfAttempt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        score.setRoutineId("the-line-up");
        score.setScoreValue(50);

        // Set mock expectations

        // First call the run() method
        routineService.run();
        // Then execute method under test
        ScoreWithRoutineContext scoreWithRoutineContext = routineService.addRoutineContextToScore(score);

        // Verify
        assertNotNull(scoreWithRoutineContext);
        assertEquals(score.getId(), scoreWithRoutineContext.getId());
        assertEquals(score.getPlayerUsername(), scoreWithRoutineContext.getPlayerUsername());
        assertEquals(score.getRoutineId(), scoreWithRoutineContext.getRoutineId());
        assertEquals(score.getDateOfAttempt(), scoreWithRoutineContext.getDateAndTimeOfAttempt());
        assertEquals(score.getScoreValue(), scoreWithRoutineContext.getScoreValue());
        assertNotNull(scoreWithRoutineContext.getRoutineTitle());
    }
}

