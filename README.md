# aws-ec2-start-stop-tools
[![Build Status](https://travis-ci.org/Sylvain-Bugat/aws-ec2-start-stop-tools.svg?branch=master)](https://travis-ci.org/Sylvain-Bugat/aws-ec2-start-stop-tools)
========================
Simple but useful tools to start and stop Amazon AWS EC2 instances.

## Configuration

Copy or edit sample ec2tools.ini file with targeted instance and order
```
[SECTION1]
instanceId1=START
...

[SECTION2]
instanceId1=STOP
...
```

## Execution

just launch the jar with one or more section arguments:
```bash
java -jar start-stop-ec2-tools-1.0-SNAPSHOT-jar-with-dependencies.jar -s SECTION1 -s SECTION2
```

## Compile and build

Using maven 2.2.1 or more recent, type this command:
```bash
mvn clean install
```
