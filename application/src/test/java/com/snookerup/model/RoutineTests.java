package com.snookerup.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import static com.snookerup.services.RoutineServiceImpl.LOCATION_OF_ROUTINES_JSON_FILES;
import static com.snookerup.services.RoutineServiceImpl.SCHEMA_FILE_NAME;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for the Routine class.
 *
 * @author Huw
 */
public class RoutineTests {

    /**
     * Test that all the routines files validate correctly against the JSON schema of the app.
     * @throws IOException
     */
    @Test
    public void validateAllRoutineJsonFiles() throws IOException {
        File routinesFilesDir = ResourceUtils.getFile(LOCATION_OF_ROUTINES_JSON_FILES);
        if (!routinesFilesDir.isDirectory()) {
            fail("Routines directory is not directory: " + routinesFilesDir.getAbsolutePath());
        }
        File[] routineFiles = routinesFilesDir.listFiles();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema jsonSchema = factory.getSchema(new FileInputStream(ResourceUtils.getFile(
                LOCATION_OF_ROUTINES_JSON_FILES + SCHEMA_FILE_NAME)));
        int routineFilesParsed = 0;
        for (File routineFile : routineFiles) {
            String fileName = routineFile.getName();
            if (!fileName.equals(SCHEMA_FILE_NAME)) {
                JsonNode jsonNode = objectMapper.readTree(new FileInputStream(routineFile));
                Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
                assertTrue(errors.isEmpty());
                routineFilesParsed++;
            }
        }
        assertTrue(routineFilesParsed == routineFiles.length - 1);
    }
}
