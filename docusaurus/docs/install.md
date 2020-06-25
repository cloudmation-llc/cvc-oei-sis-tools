---
id: install
title: Installation
sidebar_label: Installation
---

## Requirements

* At least Java 8 runtime on your preferred platform
    * Verify by running `java -version`
* Network access to SIS data

## Getting Started

SIS Tools is a Java application packaged as a single executable JAR file. Available releases can be downloaded from the [GitHub releases](https://github.com/cloudmation-llc/cvc-oei-sis-tools/releases) page. The project follows [semantic versioning](https://semver.org), and each new release will have incremented version number.

It can be installed virtually in any environment where a Java application can run, and has the appropriate network access to reach the necessary SIS data. As an example, SIS systems often include in their architecture a job submission server where scheduled business processes run. This is an ideal place to run this application. 

1. Create a directory, and download the latest JAR file to that location
    * If you have complete access to your environment, you might consider a location such as `/opt/cvcoei`
    * Cloud hosting customers may only be able to create a directory within their home assigned account

2. Create a `config` directory in the same location as the JAR file.