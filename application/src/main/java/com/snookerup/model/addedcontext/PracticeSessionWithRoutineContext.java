package com.snookerup.model.addedcontext;

import com.snookerup.model.db.nosql.PracticeSession;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A UI-centric version of a practice session from the database, with added routine context, e.g. adds the title and
 * routine-specific values to the score, as required in the UI.
 *
 * @author Huw
 */
@Getter
public class PracticeSessionWithRoutineContext {

    private final String id;

    private final String playerUsername;

    private final String title;

    private final String description;

    private final List<PracticeSessionRoutineWithRoutineContext> routines;

    private final int numberOfRoutines;

    private final int distinctRoutinesAndVariations;

    private final int totalAttempts;

    @Builder
    public PracticeSessionWithRoutineContext(PracticeSession practiceSession,
                                             List<PracticeSessionRoutineWithRoutineContext> routinesWithContext) {
        this.id = practiceSession.getId();
        this.playerUsername = practiceSession.getPlayerUsername();
        this.title = practiceSession.getTitle();
        this.description = practiceSession.getDescription();
        this.numberOfRoutines = practiceSession.getNumberOfRoutines();
        this.distinctRoutinesAndVariations = practiceSession.getDistinctRoutinesAndVariations();
        this.totalAttempts = practiceSession.getTotalAttempts();
        this.routines = routinesWithContext;
    }

    /**
     * Get a list just containing the UUIDs of each routine in this practice session, in the current order.
     * @return A list of strings, of the routine UUIDs in current order
     */
    public List<String> getCurrentRoutineUUIDsOrder() {
        return routines.stream()
                .map(routine -> routine.getUuid())
                .collect(Collectors.toList());
    }
}