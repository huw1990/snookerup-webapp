# AWS Singlebox Install Details

This page documents how this application is deployed in a single EC2 instance alongside other components (DBs etc.),
managed through Docker Compose. The deployment process is managed through GitHub Actions.

## Required Configuration

The setup/install requires a number of values to be set as GitHub Repository Secrets. A full list is:

| Secret Name           |                                               Description                                               |                              Example Value |
|:----------------------|:-------------------------------------------------------------------------------------------------------:|-------------------------------------------:|
| AWS_ACCESS_KEY_ID     |                                           AWS IAM access key                                            |                                  `AKIA...` |
| AWS_SECRET_ACCESS_KEY |                                        AWS IAM secret access key                                        |                             `wJalrXUtn...` |
| AWS_REGION            |                                               AWS region                                                |                                `eu-west-2` |
| DOMAIN_NAME           |                              Root domain already configured in AWS Route53                              |                            `snookerup.com` |
| ADMIN_EMAIL           |                             Personal email for Let's Encrypt notifications                              |                       `myname@example.com` |
| TF_STATE_BUCKET       |        Name of AWS S3 bucket where we store TerraForm state. Created during the "bootstrap" step        |             `snookerup-tf-state-123456789` |
| TF_LOCK_TABLE         | Name of the AWS DynamoDB table where we store TerraForm lock state. Created during the "bootstrap" step |                       `snookerup-tf-locks` |
| COGNITO_USER_POOL_ID  |                               User pool ID already created in AWS Cognito                               |                      `eu-west-2_XXXXXXXXX` |
| COGNITO_CLIENT_NAME   |                               Client Name already created in AWS Cognito                                |                                `snookerup` |
| COGNITO_CLIENT_ID     |                                Client ID already created in AWS Cognito                                 |               `2xxxxxxxxxxxxxxxxxxxxxxxxx` |
| COGNITO_CLIENT_SECRET |                              Client Secret already created in AWS Cognito                               |               `2xxxxxxxxxxxxxxxxxxxxxxxxx` |
| EC2_INSTANCE_ID       |                      AWS EC2 instance ID. Created during the "infrastructure" step                      |                      `i-0123456789abcdef0` |
| INVITE_CODES          |       A list of invite codes for people to sign up with, to restrict access when first launching        |         `CRUCIBLE, 147_BREAK, SNOOKER_CUE` |
## Setup Stages

The various stages of setup, in different GitHub Actions workflows, are:

### Bootstrap

In this stage, we create up front (i.e. before the main infrastructure workflow) the S3 bucket and DynamoDB table
required for TerraForm state and locks, as well as the ECR repository where our Docker images will be stored.

After running this step (which is only ever required to be run once), the following secrets can be set, from the output
of the task:

* TF_STATE_BUCKET
* TF_LOCK_TABLE

### Infrastructure

In this stage, we use TerraForm to create an EC2 instance (using standard TerraForm files found in
"/infrastructure/terraform/"), then run a cloud-init script (named "user_data.sh.tpl") to set up the application
environment, and setup backing up of DBs (via a cron job) and uploading to S3.

This task should be run once on first setup then every time the infrastructure needs to change (assuming this means
some change to the TerraForm or cloud-init script).

After running this step the first time, the following secrets can be set, from the output of the task:

* EC2_INSTANCE_ID

### Deploy App

In this stage, we build this application (i.e. the Spring Boot app in this repo), then build a Docker image for it,
upload to AWS ECR, and finally use AWS SSM to instruct the existing EC2 image to pull the latest version of the app
and restart services to take the update.