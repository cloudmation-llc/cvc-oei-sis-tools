---
id: configuration
title: Configuration
sidebar_label: Configuration
---

SIS Tools built using the Spring Framework, and uses the concept of [profiles](https://docs.spring.io/spring-boot/docs/2.3.1.RELEASE/reference/html/spring-boot-features.html#boot-features-profiles) to organize the configuration. Profiles help you set up different configurations for multiple SIS environments and Canvas environments.

A configuration profile is created by placing a file in the `config` subfolder that follows the name convention of `application-{profile}.yml`. **{profile}** is a placeholder that should be substituted with the actual name of a profile.

Visit the [`sample-configs` directory](https://github.com/cloudmation-llc/cvc-oei-sis-tools/tree/master/sample-configs) in the project repo for some starter examples.

## Built-In Profiles

SIS Tools comes packaged with built-in profiles to activate various features.

| Profile Name | Applicable Program(s) | Description |
| ---- | ---- | ---- |
| `sis-banner` | logins.csv | Enable support for the Banner SIS |

## Config Reference

```yaml
# Specify additional profiles to activate
spring.profiles.include:
  - sis-banner

cvc:
  canvas:
    # Specify the complete hostname for the Canvas instance
    host: INSITUTION-test.instructure.com

    # Specify the account ID
    accountId: 1

    # Provide an API token for an account authorization to import SIS data
    apiToken: API_TOKEN_HERE

  sis:
    type: oracle
    properties:
      # Specify the JDBC URL to your database instance
      url: "jdbc:oracle:thin:@HOST:PORT/SERVICE"

      # Specify the username to connect as
      user: "USER"

      # Specify the credential to authenticate
      password: "PASSWORD"
```

## Example: Create a Non-Production Profile

1. Create a file named `application-test.yml` in the `config` directory where you installed the executable JAR.
    * Feel free to copy the starter content from the sample above.
2. Provide a list of one or more profiles to activate for the `spring.profiles.include` property. Each supported SIS will have a packaged built-in configuration, and it is expected to activate a profile matching your institution.
3. For the `cvc.canvas` block, configure the Canvas target environment, and an API token authorized to make SIS imports.
4. For the `cvc.sis` block, configure the JDBC connection information. **Note:** _Not all SIS systems may support this block._

Follow the same steps to create a production profile.

## Multiple Institutions

To support multiple institutions, simply create additional named profiles such `application-{campusname}-test.yml`.

## Log Verbosity

The default log verbosity level is `info`, and log events will be written out using both standard console out, and one or more files in a local logs subdirectory.

SIS Tools supports an additional command line option `--log-level <level>` to change the log level. Verbosity can be lessened using the **error** or **warn** level, or enable more debugging output by specifying the **debug** level.

## Customization

The configuration system allows for overriding packaged values including the SQL queries used for pulling data from SIS systems. It is possible to customize these values. It would be advised to work with your CVC-OEI implementation team before doing so.