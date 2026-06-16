import { MongoClient } from 'mongodb';
import { program } from 'commander';
import fs from 'fs/promises';
import path from 'path';

/**
 * Imports a series of Routines in JSON files into a specified collection in the provided MongoDB instance.
 * @param uri The URI to connect to MongoDB
 * @param dbName The name of the database to connect to
 * @param collectionName The name of the collection in the database to add the Routine objects to
 * @param dir The directory on the local machine which contains the JSON files.
 * @param user The user to run the command as. Must already exist in MongoDB.
 * @param pwd The password for the provided user
 * @param authSource The database in MongoDB used for auth
 * @returns {Promise<void>}
 */
async function importJsonFiles({ uri, db: dbName, collection: collectionName, dir, user, pwd, authSource }) {
    // Set up connection options safely
    const mongoOptions = {};
    if (user && pwd) {
        mongoOptions.auth = {
            username: user,
            password: pwd
        };
        // Send the authSource parameter to options
        mongoOptions.authSource = authSource;
    }

    const client = new MongoClient(uri, mongoOptions);

    try {
        // First, connect to MongoDB and test the connection
        await client.connect();
        const db = client.db(dbName);
        const collection = db.collection(collectionName);
        await db.command({ ping: 1 });

        // Check we can access the provided directory of JSON files
        try {
            await fs.access(dir);
        } catch {
            console.error(`Directory '${dir}' does not exist. Unable to load JSON files.`);
            return;
        }

        // Find all JSON files in the directory
        const files = await fs.readdir(dir);
        const jsonFiles = files.filter(file => file.endsWith('.json'));
        if (jsonFiles.length === 0) {
            console.error(`No JSON files found in '${dir}'`);
            return;
        }

        // Now parse each JSON file in turn
        console.log(`Found ${jsonFiles.length} JSON file(s) to process.`);
        let addedCount = 0;
        let skippedCount = 0;
        for (const filename of jsonFiles) {
            const filePath = path.join(dir, filename);

            try {
                // Parse the file into JSON
                const fileContent = await fs.readFile(filePath, 'utf-8');
                const data = JSON.parse(fileContent);

                // Ensure the JSON is an object and has a "routineId" field
                if (typeof data !== 'object' || data === null || Array.isArray(data)) {
                    console.warn(`Skipped [${filename}]: File content is not a JSON object.`);
                    continue;
                }
                if (!('routineId' in data)) {
                    console.warn(`Skipped [${filename}]: Missing required 'routineId' field.`);
                    continue;
                }

                // Before adding to the DB, check if the routine already exists
                const docId = data.routineId;
                const exists = await collection.findOne({ routineId: docId });
                if (exists) {
                    console.log(`Skipped [${filename}]: Object with routineId '${docId}' already exists.`);
                    skippedCount++;
                } else {
                    // No existing Routine found, so add to the DB
                    await collection.insertOne(data);
                    console.log(`Added [${filename}]: Object with routineId '${docId}' successfully inserted.`);
                    addedCount++;
                }
            } catch (err) {
                console.error(`Error [${filename}]: Reason: ${err.message}`);
            }
        }

        // Log a final report, giving details of the script execution
        console.log('Execution Summary:');
        console.log(`   - Successfully added: ${addedCount}`);
        console.log(`   - Skipped (Duplicates): ${skippedCount}`);

        try {
            const totalDocs = await collection.countDocuments({});
            console.log(`   - Total documents now in '${collectionName}': ${totalDocs}`);
        } catch (err) {
            console.error(`Could not retrieve final document count: ${err.message}`);
        }

    } catch (err) {
        console.error(`Connection Error: Failed to connect to MongoDB. ${err.message}`);
    } finally {
        await client.close();
    }
}

/*
 * Command line configuration using Commander. By default, connects to:
 * - Local MongoDB
 * - Database "snookerup"
 * - Collection "routines"
 * - Directory "routine-json-files/"
 * Requires user, password and authSource as params though.
 */
program
    .description("Import a directory of JSON files into a MongoDB collection, skipping duplicates based on the 'id' field.")
    .option('--uri <string>', 'MongoDB connection URI', 'mongodb://localhost:27017/')
    .option('--db <string>', 'Target MongoDB database name', 'snookerup')
    .option('--collection <string>', 'Target MongoDB collection name', 'routines')
    .option('--dir <string>', 'Directory containing the JSON files', 'routine-json-files/')
    .requiredOption('--user <string>', 'MongoDB username')
    .requiredOption('--pwd <string>', 'MongoDB password')
    .requiredOption('--authSource <string>', 'Database where the user credentials are created')
    .action((options) => {
        importJsonFiles(options);
    });

// Entry point of the script
program.parse();