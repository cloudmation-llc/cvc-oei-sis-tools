---
id: program-logins-csv
title: Canvas Logins.csv
sidebar_label: Canvas Logins.csv
---

The `logins.csv` file is a method for creating trust between multiple instances of Canvas.

An example of a trust is allowing students in a home college instance of Canvas to be enrolled into a teaching college Canvas instance remotely through the CVC-OEI program. This built in program provides a standard implementation of the business logic needed to generate the logins.csv file from an SIS system, and deliver it to the Canvas instance for the teaching college.

## How to Run

Specify the `--generate-logins-csv` and `--profiles` command line options. 

### Example

```shell
java -jar cvc-oei-sis-tools.jar \
    --generate-logins-csv \
    --profiles test
```

## Tips

* Typically, the logins.csv file can be processed quickly on the Canvas side. You can run this program multiple times a day using job scheduling to continually feed new records into Canvas.
* It is important to ensure that the logins.csv is generated and imported **before** sending enrollment. This ensures that cross-enrolled students in your SIS will be matched up with their trust accounts in Canvas.
* Exclude persons intended for processing through logins.csv from your users.csv to avoid any account conflicts. 
* The generated logins.csv file can be found in the `out` subdirectory for inspection and debugging.
* Any errors reported on the Canvas side will be recorded in a dedicated log file which can be found in the `logs` subdirectory.

## Banner Implementation

The Banner integration expects the _teaching college_ to have an Oracle schema `NICCRSXCHNG` which owns a Canvas staging table named `N_STAG_CANVAS`. Records are added to this table as students are enrolled through the CVC-OEI program.

Banner is support is activated when the `cvc.sis.type` property in your configuration profile is set to `banner`.

Banner specific configuration examples are in the `sample-configs` folder.

## Colleague Implementation

The Colleague integration expects to be provided a local directory with one or more CSV files that contain the cross enrollment records. How the files are delivered to your system is set up when you first work with your CVC-OEI implementation team. One common method for handling this is through SFTP transfers.

Colleague is support is activated when the `cvc.sis.type` property in your configuration profile is set to `colleague`.

In your configuration profile, be sure to set a path to the Canvas input directory using the property key `cvc.cross-enrollment.inputDirectory`. The input directory supports [glob patterns](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher-java.lang.String-) so that you can select specific files for input, and ignore others.

Colleague specific configuration examples are in the `sample-configs` folder.

## PeopleSoft Implementation

To be developed in the future.