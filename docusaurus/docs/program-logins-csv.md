---
id: program-logins-csv
title: Canvas Logins.csv
sidebar_label: Canvas Logins.csv
---

The `logins.csv` file is a method for creating trust between multiple instances of Canvas.

An example of a trust is allowing students in a home college instance of Canvas to be enrolled into a teaching college Canvas instance remotely through the CVC-OEI program. This built in program provides a standard implementation of the business logic needed to generate the logins.csv file from an SIS system, and deliver it to the Canvas instance for the teaching college.

## How to Run

Specify the `--generate-logins-csv` command line option.

## Tips

* Typically, the logins.csv file can be processed quickly on the Canvas side. You can run this program multiple times a day using job scheduling to continually feed new records into Canvas.
* It is important to ensure that the logins.csv is generated and imported **before** sending enrollment. This ensures that cross-enrolled students in your SIS will be matched up with their trust accounts in Canvas.
* Exclude persons intended for processing through logins.csv from your users.csv to avoid any account conflicts. 

## Banner

The delivered integration expects the _teaching college_ to have an Oracle schema `NICCRSXCHNG` which owns a Canvas staging table named `N_STAG_CANVAS`. Records are added to this table as students are enrolled through the CVC-OEI program.

In the configuration file, include the `sis-banner` profile. Beyond that, no special configuration is needed aside from what is already documented on the [configuration page](configuration).

### Example

```shell
java -jar cvc-oei-sis-tools-1.x.x.jar \
    --generate-logins-csv \
    --spring.profiles.active=PROFILE_GOES_HERE
```

## Colleague

To be developed in the future.

## PeopleSoft

To be developed in the future.