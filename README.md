# School Agenda
[![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)](https://forthebadge.com) [![Build Status](https://travis-ci.org/palai103/school-agenda.svg?branch=master)](https://travis-ci.org/palai103/school-agenda) [![Coverage Status](https://coveralls.io/repos/github/palai103/school-agenda/badge.svg?branch=master)](https://coveralls.io/github/palai103/school-agenda?branch=master) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=it.unifi.app%3Aschool-agenda&metric=alert_status)](https://sonarcloud.io/dashboard?id=it.unifi.app%3Aschool-agenda)

School Agenda is an application developed using TDD techniques for the final exam of the Advanced Techniques and Tools for Software Development course at Università degli Studi di Firenze. 

A complete report of the project can be found [here](https://github.com/palai103/school-agenda/raw/master/Project%20Report.pdf).
## System requirements
 - Java 8 (or higher)
 - Maven
 - Docker
## Run the application
There are two different ways to run the application, eaither building it from source code with Maven or downloading the latest pre-packaged jar from the [releases](https://github.com/palai103/school-agenda/releases) page on Github.
### Setup the Mongo Docker instance
Before running the application, a valid Mongo Docker istance must be already running. You can create one with the following command:

    docker run --name mongo -p 27017:27017 --rm krnbr/mongo:4.2.6
The `krnbr/mongo` image was used instead of the `mongo` one because the former is already configured to act as a replica set, so we don't need to manually configure the instance to act as such.
### Create the package with Maven
To build the application locally from the source code, clone the repository and move into the home directory of the project, then run:

    mvn clean package
A jar with all dependencies will be located in the `./target/` subfolder of the project. 
### Execute the jar
Use `java -jar {jar_name} [options]` to run the application, where `{jar_name}` is the name of the jar that Maven created, and `[options]` are:
|  Option|Description|Allowed values|
|--|--|--|
| `--interface` | The type of UI to be shown |`gui` or `cli`  |
|`--mongo-host` | The IP address of the host running the Mongo database |A valid IP address, default is `localhost` |
|`--mongo-port` | The port on which the Mongo database is listening to | A valid 16-bit integer, default is `27017` |
| `--db-name` | The name of the database on the Mongo instance | A valid string, default is `schoolagenda`|
|`--db-students-collection` | The name of the Mongo collection containing student's data | A valid string, default is `students` |
|`--db-courses-collection` | The name of the Mongo collection containing course's data | A valid string, default is `courses` |
