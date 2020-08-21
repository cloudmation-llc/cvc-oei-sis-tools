/*
 * Copyright 2020 California Community Colleges Chancellor's Office
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cvcoei.sistools;

import com.google.common.io.Resources;
import org.cvcoei.sistools.common.log4j.CommandLineLookup;
import org.cvcoei.sistools.csv.logins.LoginsCsvApplication;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import picocli.CommandLine.Model.ArgGroupSpec;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.ParseResult;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

public class Launcher {

    /**
     * Entry point.
     * @param args Command line arguments
     */
    public static void main(String[] args) throws Exception {
        // Define a command line argument spec
        final CommandSpec commandSpec = CommandSpec.create();

        // Define an optional command line argument for setting the log verbosity level
        commandSpec.addOption(OptionSpec
            .builder("--log-level")
            .arity("1")
            .build());

        // Define an optional command line argument to simplify activating Spring profiles
        commandSpec.addOption(OptionSpec
            .builder("--profiles")
            .arity("1..*")
            .build());

        // Define an optional command line argument to print the program version
        commandSpec.addOption(OptionSpec
            .builder("--version")
            .build());

        // Define a group spec for identifying a job program to
        commandSpec.addArgGroup(ArgGroupSpec
            .builder()
            .addArg(OptionSpec
                .builder("--generate-logins-csv")
                .build())
            .build());

        // Configure Log4j command line lookup plugin to parse and recognize specific command line arguments
        ParseResult parseResult = CommandLineLookup.parse(commandSpec, args);

        // Check if the program version is requested
        if(parseResult.hasMatchedOption("--version")) {
            // Load version string from embedded properties file
            InputStream versionInputStream = Launcher
                .class
                .getClassLoader()
                .getResourceAsStream("version.properties");

            Properties versionProperties = new Properties();
            versionProperties.load(versionInputStream);

            System.out.println("CVC-OEI SIS Tools");
            System.out.println("Version " + versionProperties.getProperty("version"));

            System.exit(0);
        }

        // Evaluate which program has been requested and create the Spring application
        SpringApplication application;
        if(parseResult.hasMatchedOption("generate-logins-csv")) {
            application = new SpringApplication(LoginsCsvApplication.class);
        }
        else {
            throw new RuntimeException("A program type needs to be selected. See documentation for more information.");
        }

        // Apply requested Spring profiles
        if(parseResult.hasMatchedOption("--profiles")) {
            List<String> requestedProfiles = parseResult.matchedOption("--profiles").stringValues();
            application.setAdditionalProfiles(requestedProfiles.toArray(new String[0]));
        }

        // Run Spring application
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }

}
