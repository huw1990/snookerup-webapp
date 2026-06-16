# Routine Database Import Tool

This is a Node.js script that takes a directory of files, where each file contains a single Routine in JSON, then
individually adds each Routine to the database if it does not already exist in the database.

## Usage

The script uses Commander (https://github.com/tj/commander.js/) to describe the command line interface, which in short
involves the following params:

- '--uri <string>': MongoDB connection URI. Defaults to 'mongodb://localhost:27017/' 
- '--db <string>': Target MongoDB database name. Defaults to 'snookerup'
- '--collection <string>': Target MongoDB collection name'. Defaults to 'routines'
- '--dir <string>': Directory containing the JSON files. Defaults to 'routine-json-files/'
- '--user <string>': Username to run as
- '--pwd <string>': Password for the user we're running as
- '--authSource <string>': Authentication database for MongoDB

To run with all the defaults, you still must specify user, pwd, and authSource:

`node import-json.mjs --user user --pwd password --authSource admin`

Or with custom values:

`node import-json.mjs --uri "mongodb+srv://user:pass@cluster.mongodb.net/" --db store_db --collection products --dir ./data_imports --user user --pwd password --authSource admin`