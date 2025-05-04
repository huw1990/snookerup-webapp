package com.snookerup.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.snookerup.services.RoutineServiceImpl.ALL_ROUTINES_JSON_FILE;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the Routine class.
 *
 * @author Huw
 */
public class RoutineTests {

    private static final String SCHEMA_FILE_NAME = "routines/routine-schema.json";

    /**
     * Test that all the routines files validate correctly against the JSON schema of the app.
     * @throws IOException
     */
    @Test
    public void validateAllRoutineJsonFiles() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        AllRoutines allRoutines = objectMapper.readValue(new ClassPathResource(ALL_ROUTINES_JSON_FILE).getInputStream(),
                AllRoutines.class);
        List<String> routineFileNames = allRoutines.getRoutineFileNames();
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema jsonSchema = factory.getSchema(new ClassPathResource(SCHEMA_FILE_NAME).getInputStream());
        int routineFilesParsed = 0;
        for (String routineFileName : routineFileNames) {
            JsonNode jsonNode = objectMapper.readTree(new ClassPathResource(routineFileName).getInputStream());
            Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
            assertTrue(errors.isEmpty());
            routineFilesParsed++;
        }
        assertTrue(routineFilesParsed == routineFileNames.size());
    }
}
