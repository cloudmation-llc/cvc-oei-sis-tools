---
id: configuration
title: Configuration
sidebar_label: Configuration
---

SIS Tools uses a feature of the underlying Spring Framework called [profiles](https://docs.spring.io/spring-boot/docs/2.3.1.RELEASE/reference/html/spring-boot-features.html#boot-features-profiles) to organize the configuration. Profiles allow you to specify separate named configurations. For example, many implementors will have a profile for production, and another one for non-production. If supporting multiple colleges in a district, then each institution may have its own set of profiles. 

A configuration profile is created by placing a file in the `config` subfolder that follows the name convention of `application-{profile}.yml`. **{profile}** is a placeholder that should be substituted with the actual name of a profile.

Use the `--profiles` command line argument to activate one or more profiles when running a program.

Visit the [`sample-configs` directory](https://github.com/cloudmation-llc/cvc-oei-sis-tools/tree/master/sample-configs) in the project repo for some starter examples.

## Config Reference

```yaml
cvc:
  canvas:
    # Specify the complete hostname for the Canvas instance
    host: INSITUTION-test.instructure.com

    # Specify the account ID
    accountId: 1

    # Provide an API token for an account authorization to import SIS data
    apiToken: API_TOKEN_HERE

  sis:
    # Specify the SIS type (banner, colleague, peoplesoft)
    type: banner

    # [Banner/PeopleSoft only] Specify the JDBC URL to your database instance
    url: "jdbc:oracle:thin:@HOST:PORT/SERVICE"

    # [Banner/PeopleSoft only] Specify the username to connect as
    user: "USER"

    # [Banner/PeopleSoft only] Specify the credential to authenticate
    password: "PASSWORD"
```

## Example: Create a Non-Production Profile

This example assumes using the Banner SIS. Configurations may be different for other SIS vendors.

1. Create a file named `application-test.yml` in the `config` directory where you installed the executable JAR.
    * Feel free to copy the starter content from the sample above.
2. For the `cvc.canvas` block, configure the Canvas target environment, and an API token authorized to make SIS imports.
3. For the `cvc.sis` block, configure the JDBC connection information.

Follow the same steps to create a production profile.

## Multiple Colleges

To support multiple colleges, simply create additional named profiles such `application-{campusname}-test.yml`.

## Log Verbosity

The default log verbosity level is `info`, and log events will be written out using both standard console out, and one or more files in a local logs subdirectory.

SIS Tools supports an additional command line option `--log-level <level>` to change the log level. Verbosity can be lessened using the **error** or **warn** level, or enable more debugging output by specifying the **debug** level.

## Customization

The configuration system allows for overriding packaged values including the SQL queries used for pulling data from SIS systems. It is possible to customize these values, but before doing so, first consult with your CVC-OEI implementation team.