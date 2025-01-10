# SnookerUp CDK App

This directory contains an application that uses the AWS Cloud Development Kit (CDK) to describe AWS
resources as Java objects, which can then be deployed and destroyed.

While the application is Java, built with Maven, the triggering of each action on a specific "application"
(specific resource that makes up the solution) is done via Node.js and the scripts defined in the
package.json file in the root of this directory.

Based on the excellent [Stratospheric](https://stratospheric.dev/) book, including their defined AWS constructs.