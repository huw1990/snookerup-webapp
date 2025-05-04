package com.snookerup.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snookerup.model.Routine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
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

    /** The location of the directory containing the schema and routine JSON files (in src/resources/routines in the code). */
    public static final String LOCATION_OF_ROUTINES_JSON_FILES = "classpath:routines/";
    /** The name of the schema file, so that we don't try to parse it as a routine. */
    public static final String SCHEMA_FILE_NAME = "routine-schema.json";

    private final Map<String, Routine> routineIdToRoutines = new HashMap<>();
    private final Set<String> allTags = new HashSet<>();
    private final Map<String, Set<Routine>> tagsToRoutines = new HashMap<>();

    @Override
    public void run(String... args) throws Exception {
        log.debug("Loading routine config JSON files into memory");
        File routinesFilesDir = ResourceUtils.getFile(LOCATION_OF_ROUTINES_JSON_FILES);
        if (!routinesFilesDir.isDirectory()) {
            throw new IllegalStateException("Routines directory is not directory: " + routinesFilesDir.getAbsolutePath());
        }
        File[] routineFiles = routinesFilesDir.listFiles();
        ObjectMapper objectMapper = new ObjectMapper();
        for (File routineFile : routineFiles) {
            String fileName = routineFile.getName();
            if (!fileName.equals(SCHEMA_FILE_NAME)) {
                log.debug("Routine file name={}", fileName);
                Routine routine = objectMapper.readValue(routineFile, Routine.class);
                log.debug("Parsed routine={}", routine);
                routineIdToRoutines.put(routine.getId(), routine);
                allTags.addAll(routine.getTags());
                for (String tag : routine.getTags()) {
                    tagsToRoutines.computeIfAbsent(tag, k -> new HashSet<>()).add(routine);
                }
            }
        }
    }

    @Override
    public List<Routine> getAllRoutines() {
        return routineIdToRoutines.values().stream().collect(Collectors.toList());
    }

    @Override
    public Routine getRoutineById(String id) {
        return routineIdToRoutines.get(id);
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
}
