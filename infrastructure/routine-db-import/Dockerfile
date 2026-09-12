FROM node:20-alpine
WORKDIR /usr/src/app

COPY package*.json ./
RUN npm install --omit=dev

COPY . .

# Pass arguments directly to import-json.mjs using environment variables
CMD node import-json.mjs \
  --uri "mongodb://${MONGO_HOST}:${MONGO_PORT}/" \
  --db "${MONGO_DATABASE}" \
  --collection "routines" \
  --dir "./routine-json-files/" \
  --user "${MONGO_USERNAME}" \
  --pwd "${MONGO_PASSWORD}" \
  --authSource "${MONGO_AUTH_DB}"