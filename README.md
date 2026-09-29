# SnookerUp Webapp

<img src="https://raw.githubusercontent.com/huw1990/snookerup-webapp/main/application/src/main/resources/static/images/snookerup_logo.png" alt="SnookerUp Logo" width="150" />

This is a monorepo hosting the SnookerUp Webapp and corresponding infrastructure.

# What is SnookerUp?

SnookerUp is a service designed to help snooker players improve their game through solo practice, by providing a
catalogue of routines to try and allowing users to track their scores over time.

Some useful features include:

* Searchable catalogue of routines, where users can search by title of routine, and filter by tag/category (e.g. 
"break building")
* Similar routines grouped under a single catalogue entry through the use of "variations", (e.g. both a full, 15-red
Line Up and a shorter, 10-red Line Up are both catalogued under the same routine, named "The Line Up")
* Ability to post scores for a routine (and any specific variations applied), private to the user.
* Allow users to view all previous scores (as well all scores in a particular time period), and drill down further into
analysis of similar previous scores to gauge progress over time.
* Provide a user with a number of slots for pre-configured practice sessions, where routines and variations can be added
in advance, and scores can be submitted for the session as a whole, rather than on a per-attempt basis.

# Architecture

The application is deployed through a series of GitHub Actions workflows, where some components are manually
configured up front:

* A Cognito user pool is created and configured through the AWS UI to set up email template content, app clients, etc.
* An S3 bucket is created to host the images associated with each routine in the catalogue.

With the up front infrastructure in place, the GitHub Actions workflows achieve the following:

* Stage One ("01-bootstrap.yml"):
  * Creates an S3 bucket for storing Terraform state, a DynamoDB table for managing Terraform locks, and an ECR
repository for hosting Docker images of our Spring Boot web app and any other custom images.
  * Only needs to be run once per system.
* Stage Two ("02-infrastructure-setup.yml"):
  * Runs the Terraform to provision the EC2 instance that will host our application in AWS.
  * After the instance is created, uses cloud-init (see the user_data.sh.tpl file in infrastructure/terraform) to
install the necessary software, including setting up Docker Compose to run the application and it's corresponding DBs etc.
  * For redundancy, the cloud-init script also initiates nightly database dumps (using pg-dump and mongodump), which are
then stored in S3 for 7 days, in case of failure.
  * Needs to be run every time the infrastructure of the app changes (e.g. if a new component is required).
* Stage Three ("03-deploy-app.yml"):
  * Builds the Spring Boot app, publishes the build to ECR, then uses AWS Systems Manager (SSM) to trigger the EC2
instance to pull the latest image and restart the app.
  * Needs to be run every time the Spring Boot app code changes.
* Stage Four ("04-update-routines.yml"):
  * Updates the catalogue of routines the webapp uses (see the infrastructure/routine-db-import directory for the Node.js
script, the "seeder", that takes a directory of JSON files and populates MongoDB idempotently).
  * Builds the Docker image for the seeder, pushes to ECR, then uses SSM to instruct the EC2 instance to pull the latest
container and run it, with an optional ability to drop the entire routine catalogue and re-import, e.g. if the format of
the routine JSON file changes.
  * Needs to be run every time the routine catalogue needs to be updated, e.g. when there are more routines to add.

Once this process is followed, the result is a setup in AWS that looks like:

```
+----------------------------------------------------------------------------------------------------+
|                                         DNS & Security                                             |
|                                                                                                    |
|                                         +----------------+                                         |
|                                         |  AWS Route 53  |                                         |
|                                         |  (DNS Records) |                                         |
|                                         +-------+--------+                                         |
+-------------------------------------------------|--------------------------------------------------+
                                                  | A Record (Public IP)
                                                  v
+----------------------------------------------------------------------------------------------------+
| AWS Region (e.g. eu-west-2) - Default VPC                                                          |
|                                                                                                    |
|   +--------------------------------------------------------------------------------------------+   |
|   | AWS Security Group                                                                         |   |
|   |  - Inbound: Ports 80 (HTTP) & 443 (HTTPS)                                                  |   |
|   |  - Outbound: All traffic                                                                   |   |
|   |                                                                                            |   |
|   |   +------------------------------------------------------------------------------------+   |   |
|   |   | AWS EC2 Instance + Elastic IP                                                      |   |   |
|   |   | (Managed via AWS SSM | IAM Role Profile attached)                                  |   |   |
|   |   |                                                                                    |   |   |
|   |   |  +------------------------------------------------------------------------------+  |   |   |
|   |   |  | Docker Compose Stack (`app-network` Bridge Network)                          |  |   |   |
|   |   |  |                                                                              |  |   |   |
|   |   |  |   +-----------------------------------------------------------------------+  |  |   |   |
|   |   |  |   | Nginx Reverse Proxy (Ports 80/443)                                    |  |  |   |   |
|   |   |  |   |  - Let's Encrypt SSL/TLS Termination (Certbot Cron Renewal)           |  |  |   |   |
|   |   |  |   +----------------------------------+------------------------------------+  |  |   |   |
|   |   |  |                                      |                                       |  |   |   |
|   |   |  |                                      | Proxy (http://app:8080)               |  |   |   |
|   |   |  |                                      v                                       |  |   |   |
|   |   |  |   +-----------------------------------------------------------------------+  |  |   |   |
|   |   |  |   | Spring Boot Web App (Java Backend Container)                          |  |  |   |   |
|   |   |  |   +-------------------+-------------------------------+-------------------+  |  |   |   |
|   |   |  |                       |                               |                      |  |   |   |
|   |   |  |        JDBC (:5432)   v               MongoDB (:27017) v                     |  |   |   |
|   |   |  |   +-----------------------+               +-----------------------+          |  |   |   |
|   |   |  |   | PostgreSQL Container  |               | MongoDB Container     |          |  |   |   |
|   |   |  |   | (Named Volume Host)   |               | (Named Volume Host)   |          |  |   |   |
|   |   |  |   +-----------------------+               +-----------------------+          |  |   |   |
|   |   |  |                                                                              |  |   |   |
|   |   |  +------------------------------------------------------------------------------+  |   |   |
|   |   |                                                                                    |   |   |
|   |   |  +------------------------------------------------------------------------------+  |   |   |
|   |   |  | Host OS Scheduled Tasks (Cron)                                               |  |   |   |
|   |   |  |  - `snookerup-backup.sh`: Dumps Postgres & Mongo -> Syncs to S3 Bucket       |  |   |   |
|   |   |  |  - `renew-certs.sh`: Renews SSL certificates via Certbot                     |  |   |   |
|   |   |  +------------------------------------------------------------------------------+  |   |   |
|   |   +------------------------------------------------------------------------------------+   |   |
|   +--------------------------------------------------------------------------------------------+   |
|                                                                                                    |
+----------------------------------------------------------------------------------------------------+
       | (Image Pulls)                        | (OAuth2 Auth & Admin)               | (Sync Backups &
       v                                      v                                     |  Catalogue Images)
+-----------------------+          +--------------------+                 +---------v-------------+
| Amazon ECR            |          | AWS Cognito        |                 | Amazon S3             |
|  - Webapp Image       |          |  - User Pool       |                 |  - DB Backup Storage  |
|  - Seeder Tool Image  |          |  - App Client      |                 |  - Catalogue Images   |
+-----------------------+          +--------------------+                 |  - Public Access Block|
                                                                          +-----------------------+
```

# Running Locally

Use the compose.yaml file to run the entire stack locally, with the following command:

`docker compose up`

This will run the required databases (PostgreSQL, MongoDB) locally, as well as using Keycloak to imitate Cognito.