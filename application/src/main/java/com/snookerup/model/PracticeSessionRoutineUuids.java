package com.snookerup.model;

import com.snookerup.errorhandling.RoutineUuidDoesntExistException;
import com.snookerup.model.db.nosql.PracticeSessionRoutine;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps a list of practice session routine UUIDs.
 *
 * @author Huw
 */
@Data
@Slf4j
public class PracticeSessionRoutineUuids {

    private List<String> uuids = new ArrayList<>();

    /**
     * Filter the existing routines from the practice session, based on the UUIDs provided in this object.
     * @param practiceSessionRoutines The existing routines in the practice session
     * @return A filtered list of routines, matching the UUIDs provided
     * @throws RoutineUuidDoesntExistException If a UUID in this object wasn't found in the list of routines
     */
    public List<PracticeSessionRoutine> filterFromPracticeSessionRoutines(
            List<PracticeSessionRoutine> practiceSessionRoutines) throws RoutineUuidDoesntExistException {
        log.debug("Updating routine order for practice session routines={}, requested new order={}",
                practiceSessionRoutines, uuids);
        if (uuids.isEmpty()) {
            log.debug("Uuids list empty");
            return new ArrayList<>();
        }
        List<PracticeSessionRoutine> newRoutines = new ArrayList<>();
        for (String uuid : uuids) {
            PracticeSessionRoutine routineForUuid = getRoutineMatchingUuid(uuid, practiceSessionRoutines);
            if (routineForUuid == null) {
                log.debug("Routine not found for uuid={}", uuid);
                throw new RoutineUuidDoesntExistException(uuid);
            }
            newRoutines.add(routineForUuid);
        }
        return newRoutines;
    }

    private PracticeSessionRoutine getRoutineMatchingUuid(String uuid,
                                                          List<PracticeSessionRoutine> practiceSessionRoutines) {
        for (PracticeSessionRoutine routine : practiceSessionRoutines) {
            if (routine.getUuid().equals(uuid)) {
                return routine;
            }
        }
        return null;
    }
}
