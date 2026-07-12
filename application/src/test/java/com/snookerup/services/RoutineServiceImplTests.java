package com.snookerup.services;

import com.snookerup.model.db.nosql.*;
import com.snookerup.model.addedcontext.PracticeSessionRoutineWithRoutineContext;
import com.snookerup.model.addedcontext.ScoreWithRoutineContext;
import com.snookerup.model.db.Score;
import com.snookerup.repositories.RoutineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the RoutineServiceImpl class.
 *
 * @author Huw
 */
class RoutineServiceImplTests {

    private RoutineRepository mockRoutineRepository;
    private MongoTemplate mockMongoTemplate;
    private Routine mockRoutine;
    private Routine mockRoutine1;
    private Routine mockRoutine2;
    private Routine mockRoutine3;
    private Page<Routine> mockPage;
    private List<Routine> routineList;

    RoutineServiceImpl routineService;

    @BeforeEach
    public void beforeEach() {
        mockRoutineRepository = mock(RoutineRepository.class);
        mockMongoTemplate = mock(MongoTemplate.class);
        mockRoutine = mock(Routine.class);
        mockRoutine1 = mock(Routine.class);
        mockRoutine2 = mock(Routine.class);
        mockRoutine3 = mock(Routine.class);
        mockPage = mock(Page.class);

        routineList = List.of(mockRoutine, mockRoutine1, mockRoutine2, mockRoutine3);

        routineService = new RoutineServiceImpl(mockRoutineRepository, mockMongoTemplate);
    }

    @Test
    public void getRoutines_When_NoTagOrSearchTerm() {
        // Define variables
        String tag = null;
        String searchTerm = null;
        int pageNumber = 0;
        // Set small page size so the query goes into multiple pages
        int pageSize = 2;
        long totalElements = 10L;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Query query = new Query().with(pageable);

        // Set mock expectations
        when(mockMongoTemplate.find(query, Routine.class)).thenReturn(routineList);
        when(mockMongoTemplate.count(Query.of(query).limit(-1).skip(-1), Routine.class)).thenReturn(totalElements);

        // Then execute method under test
        Page<Routine> routinesPage = routineService.getRoutines(tag, searchTerm, pageNumber, pageSize);

        // Verify
        verify(mockMongoTemplate).find(query, Routine.class);
        verify(mockMongoTemplate).count(Query.of(query).limit(-1).skip(-1), Routine.class);
        assertEquals(routineList, routinesPage.toList());
        assertEquals(totalElements, routinesPage.getTotalElements());
    }

    @Test
    public void getRoutines_When_TagButNoSearchTerm() {
        // Define variables
        String tag = "break-building";
        String searchTerm = null;
        int pageNumber = 0;
        // Set small page size so the query goes into multiple pages
        int pageSize = 2;
        long totalElements = 10L;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("tags").in(tag));
        query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));

        // Set mock expectations
        when(mockMongoTemplate.find(query, Routine.class)).thenReturn(routineList);
        when(mockMongoTemplate.count(Query.of(query).limit(-1).skip(-1), Routine.class)).thenReturn(totalElements);

        // Then execute method under test
        Page<Routine> routinesPage = routineService.getRoutines(tag, searchTerm, pageNumber, pageSize);

        // Verify
        verify(mockMongoTemplate).find(query, Routine.class);
        verify(mockMongoTemplate).count(Query.of(query).limit(-1).skip(-1), Routine.class);
        assertEquals(routineList, routinesPage.toList());
        assertEquals(totalElements, routinesPage.getTotalElements());
    }

    @Test
    public void getRoutines_When_SearchTermButNoTagAndMultiplePages() {
        // Define variables
        String tag = null;
        String searchTerm = "line";
        int pageNumber = 0;
        // Set small page size so the query goes into multiple pages
        int pageSize = 2;
        long totalElements = 10L;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();
        String escapedSearch = Pattern.quote(searchTerm.trim());
        criteriaList.add(Criteria.where("title").regex(escapedSearch, "i"));
        query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));

        // Set mock expectations
        when(mockMongoTemplate.find(query, Routine.class)).thenReturn(routineList);
        when(mockMongoTemplate.count(Query.of(query).limit(-1).skip(-1), Routine.class)).thenReturn(totalElements);

        // Then execute method under test
        Page<Routine> routinesPage = routineService.getRoutines(tag, searchTerm, pageNumber, pageSize);

        // Verify
        verify(mockMongoTemplate).find(query, Routine.class);
        verify(mockMongoTemplate).count(Query.of(query).limit(-1).skip(-1), Routine.class);
        assertEquals(routineList, routinesPage.toList());
        assertEquals(totalElements, routinesPage.getTotalElements());
    }

    @Test
    public void getRoutines_When_SearchTermButNoTagAndSinglePage() {
        // Define variables
        String tag = null;
        String searchTerm = "line";
        int pageNumber = 0;
        // Set small page size so the query goes into multiple pages
        int pageSize = 20;
        long totalElements = 4L;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();
        String escapedSearch = Pattern.quote(searchTerm.trim());
        criteriaList.add(Criteria.where("title").regex(escapedSearch, "i"));
        query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));

        // Set mock expectations
        when(mockMongoTemplate.find(query, Routine.class)).thenReturn(routineList);
        when(mockMongoTemplate.count(Query.of(query).limit(-1).skip(-1), Routine.class)).thenReturn(totalElements);

        // Then execute method under test
        Page<Routine> routinesPage = routineService.getRoutines(tag, searchTerm, pageNumber, pageSize);

        // Verify
        verify(mockMongoTemplate).find(query, Routine.class);
        verify(mockMongoTemplate, never()).count(Query.of(query).limit(-1).skip(-1), Routine.class);
        assertEquals(routineList, routinesPage.toList());
        assertEquals(totalElements, routinesPage.getTotalElements());
    }

    @Test
    public void getRoutines_When_BothTagAndSearchTermAndMultiplePages() {
        // Define variables
        String tag = "break-building";
        String searchTerm = "line";
        int pageNumber = 0;
        // Set small page size so the query goes into multiple pages
        int pageSize = 2;
        long totalElements = 10L;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();
        String escapedSearch = Pattern.quote(searchTerm.trim());
        criteriaList.add(Criteria.where("title").regex(escapedSearch, "i"));
        criteriaList.add(Criteria.where("tags").in(tag));
        query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));

        // Set mock expectations
        when(mockMongoTemplate.find(query, Routine.class)).thenReturn(routineList);
        when(mockMongoTemplate.count(Query.of(query).limit(-1).skip(-1), Routine.class)).thenReturn(totalElements);

        // Then execute method under test
        Page<Routine> routinesPage = routineService.getRoutines(tag, searchTerm, pageNumber, pageSize);

        // Verify
        verify(mockMongoTemplate).find(query, Routine.class);
        verify(mockMongoTemplate).count(Query.of(query).limit(-1).skip(-1), Routine.class);
        assertEquals(routineList, routinesPage.toList());
        assertEquals(totalElements, routinesPage.getTotalElements());
    }

    @Test
    public void getRoutines_When_BothTagAndSearchTermAndSinglePage() {
        // Define variables
        String tag = "break-building";
        String searchTerm = "line";
        int pageNumber = 0;
        // Set small page size so the query goes into multiple pages
        int pageSize = 20;
        long totalElements = 4L;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();
        String escapedSearch = Pattern.quote(searchTerm.trim());
        criteriaList.add(Criteria.where("title").regex(escapedSearch, "i"));
        criteriaList.add(Criteria.where("tags").in(tag));
        query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));

        // Set mock expectations
        when(mockMongoTemplate.find(query, Routine.class)).thenReturn(routineList);
        when(mockMongoTemplate.count(Query.of(query).limit(-1).skip(-1), Routine.class)).thenReturn(totalElements);

        // Then execute method under test
        Page<Routine> routinesPage = routineService.getRoutines(tag, searchTerm, pageNumber, pageSize);

        // Verify
        verify(mockMongoTemplate).find(query, Routine.class);
        verify(mockMongoTemplate, never()).count(Query.of(query).limit(-1).skip(-1), Routine.class);
        assertEquals(routineList, routinesPage.toList());
        assertEquals(totalElements, routinesPage.getTotalElements());
    }

    @Test
    public void getAllRoutines_Should_DelegateToRepository() {
        // Define variables
        List<Routine> allRoutinesList = List.of(mockRoutine);

        // Set mock expectations
        when(mockRoutineRepository.findAll()).thenReturn(allRoutinesList);

        // Then execute method under test
        List<Routine> allRoutines = routineService.getAllRoutines();

        // Verify
        verify(mockRoutineRepository).findAll();
        assertEquals(allRoutines, allRoutinesList);
    }

    @Test
    public void getRoutineById_Should_DelegateToRepository() {
        // Define variables
        String routineId = "the-line-up";

        // Set mock expectations
        when(mockRoutineRepository.findByRoutineId(routineId)).thenReturn(Optional.of(mockRoutine));

        // Then execute method under test
        Optional<Routine> routine = routineService.getRoutineById(routineId);

        // Verify
        verify(mockRoutineRepository).findByRoutineId(routineId);
        assertEquals(routine, Optional.of(mockRoutine));
    }

    @Test
    public void getAllTags_Should_DelegateToMongoTemplateForFirstCallOnly() {
        // Define variables
        String tag = "break-building";

        // Set mock expectations
        when(mockMongoTemplate.findDistinct("tags", Routine.class, String.class)).thenReturn(List.of(tag));

        // Then execute method under test
        List<String> allTags = routineService.getAllTags();

        // Verify
        verify(mockMongoTemplate).findDistinct("tags", Routine.class, String.class);
        assertEquals(allTags, List.of(tag));
        // Now call it again and verify we didn't call MongoTemplate again
        List<String> allTagsAgain = routineService.getAllTags();
        assertEquals(allTagsAgain, List.of(tag));
        // MongoTemplate should still have only been called once
        verify(mockMongoTemplate, times(1)).findDistinct("tags", Routine.class, String.class);
    }

    @Test
    public void getRoutinesForTag_Should_DelegateToRepository() {
        // Define variables
        String tag = "break-building";

        // Set mock expectations
        when(mockRoutineRepository.findByTags(tag)).thenReturn(List.of(mockRoutine));

        // Then execute method under test
        List<Routine> routinesForTag = routineService.getRoutinesForTag(tag);

        // Verify
        verify(mockRoutineRepository).findByTags(tag);
        assertEquals(routinesForTag, List.of(mockRoutine));
    }

    @Test
    public void getRandomRoutine_Should_DelegateToRepository() {
        // Define variables

        // Set mock expectations
        when(mockRoutineRepository.getRandomRoutine()).thenReturn(mockRoutine);

        // Then execute method under test
        Routine randomRoutine = routineService.getRandomRoutine();

        // Verify
        verify(mockRoutineRepository).getRandomRoutine();
        assertEquals(randomRoutine, mockRoutine);
    }

    @Test
    public void addRoutineContextToScore_Should_ReturnCreatedScoreWithRoutineContext() {
        // Define variables
        String routineId = "the-line-up";
        Score score = new Score();
        score.setId(1L);
        score.setPlayerUsername("willo");
        score.setDateOfAttempt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        score.setRoutineId(routineId);
        score.setScoreValue(50);

        // Set mock expectations
        when(mockRoutineRepository.findByRoutineId(routineId)).thenReturn(Optional.of(mockRoutine));
        when(mockRoutine.getUnit()).thenReturn(Unit.REDS);
        when(mockRoutine.getScoreUnit()).thenReturn(ScoreUnit.BREAK);
        when(mockRoutine.getTitle()).thenReturn("The Line Up");

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

    @Test
    public void addRoutineContextToPracticeSessionRoutine_Should_ReturnCreatedPracticeSessionRoutineWithRoutineContext() throws Exception {
        // Define variables
        String routineId = "the-line-up";
        PracticeSessionRoutine routine = new PracticeSessionRoutine();
        routine.setRoutineId(routineId);
        routine.setLoop(true);
        routine.setCushionLimit(3);
        routine.setUnitNumber(10);
        routine.setPotInOrder(true);
        routine.setStayOnOneSideOfTable(true);
        routine.setBallStriking(BallStriking.STUN);
        routine.setNumberOfAttempts(5);
        routine.setNote("Test note");

        // Set mock expectations
        when(mockRoutineRepository.findByRoutineId(routineId)).thenReturn(Optional.of(mockRoutine));
        when(mockRoutine.getUnit()).thenReturn(Unit.REDS);
        when(mockRoutine.getScoreUnit()).thenReturn(ScoreUnit.BREAK);
        when(mockRoutine.getTitle()).thenReturn("The Line Up");

        // Then execute method under test
        PracticeSessionRoutineWithRoutineContext routineWithRoutineContext =
                routineService.addRoutineContextToPracticeSessionRoutine(routine);

        // Verify
        assertNotNull(routineWithRoutineContext);
        assertEquals(routine.getRoutineId(), routineWithRoutineContext.getRoutineId());
        assertEquals(routine.isLoop(), routineWithRoutineContext.isLoop());
        assertEquals(routine.getCushionLimit(), routineWithRoutineContext.getCushionLimit());
        assertEquals(routine.getUnitNumber(), routineWithRoutineContext.getUnitNumber());
        assertEquals(routine.isPotInOrder(), routineWithRoutineContext.isPotInOrder());
        assertEquals(routine.isStayOnOneSideOfTable(), routineWithRoutineContext.isStayOnOneSideOfTable());
        assertEquals(routine.getBallStriking(), routineWithRoutineContext.getBallStriking());
        assertEquals(routine.getNumberOfAttempts(), routineWithRoutineContext.getNumberOfAttempts());
        assertEquals(routine.getNote(), routineWithRoutineContext.getNote());
        assertNotNull(routineWithRoutineContext.getRoutineTitle());
        assertNotNull(routineWithRoutineContext.getRoutineUnit());
    }
}

