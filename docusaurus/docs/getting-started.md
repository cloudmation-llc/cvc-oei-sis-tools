---
id: getting-started
title: Getting Started
sidebar_label: Getting Started
---

## Requirements

* At least Java 8 runtime on your preferred platform
    * Verify by running `java -version`
* Network access to SIS data

## Download

SIS Tools is a Java application packaged as a single executable JAR file. Available releases can be downloaded from the [GitHub releases](https://github.com/cloudmation-llc/cvc-oei-sis-tools/releases) page. The project follows [semantic versioning](https://semver.org), and each new release will have incremented version number.

It can be installed virtually in any environment where a Java application can run, and has the appropriate network access to reach the necessary SIS data. As an example, SIS systems often include in their architecture a job submission server where scheduled business processes run. This is an ideal place to run this application. 

## Installation

1. Create a directory, and download the [latest JAR file](https://github.com/cloudmation-llc/cvc-oei-sis-tools/releases/latest) to that location
    * If you have complete access to your environment, you might consider a location such as `/opt/cvcoei`
    * Cloud hosting customers may only be able to create a directory within their home assigned account

2. Create a `config` directory in the same location as the JAR file. See the [configuration](configuration) page for additional detail.

## Built-In Programs

SIS Tools packages together multiple tools for CVC-OEI integrations. Programs are activated by specifying a command line option.

| Flag | Progam |
| ---- | ---- |
| `--generate-logins-csv` | Generate the logins.csv file |
    
## Running a Program

**Basic Example to Generate logins.csv:**

```shell
java -jar cvc-oei-sis-tools-1.x.x.jar \
    --generate-logins-csv \
    --spring.profiles.active=PROFILE_GOES_HERE
```