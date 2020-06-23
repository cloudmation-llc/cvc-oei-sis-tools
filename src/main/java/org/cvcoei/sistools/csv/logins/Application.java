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

package org.cvcoei.sistools.csv.logins;

import com.opencsv.CSVWriter;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;

@SuppressWarnings("ALL")
@SpringBootApplication(
    scanBasePackages = { "org.cvcoei.sistools.common", "org.cvcoei.sistools.csv.logins" },
    proxyBeanMethods = false)
@Log4j2
public class Application {

    /**
     * Entry point.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }

    @Service
    public static class Runner implements ApplicationRunner {

        public final static String[] CSV_HEADER = new String[] {
            "user_id",
            "login_id",
            "existing_user_id",
            "root_account" };

        private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .build();

        @Value("${cvc.canvas.accountId}")
        String canvasAccountId;

        @Value("${cvc.canvas.apiToken}")
        String canvasApiToken;

        @Value("${cvc.canvas.host}")
        String canvasHost;

        @Value("${cvc.logins.canvasTrustSuffix}")
        String canvasTrustSuffix;

        @Value("${cvc.logins.outputFile}")
        String pathOutputFile;

        @Value("${cvc.logins.sisQuery}")
        String sqlLogins;

        @Autowired
        DataSource sisDatasource;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            // Create a JdbcTemplate for interacting with the SIS database
            JdbcTemplate jdbc = new JdbcTemplate(sisDatasource);

            // Set up output path
            Path outputPath = Paths.get(pathOutputFile);
            Optional<Path> outputDirectory = Optional.ofNullable(outputPath.getParent());
            outputDirectory.ifPresent(this::createDirectory);

            // Open a local CSV file
            try(CSVWriter writer = new CSVWriter(Files.newBufferedWriter(outputPath))) {
                // Write header
                writer.writeNext(CSV_HEADER);

                // Execute SIS query
                jdbc
                    .queryForList(sqlLogins)
                    .forEach(record -> {
                        // Write record
                        writer.writeNext(new String[] {
                            (String) record.get("user_id"),
                            (String) record.get("login_id"),
                            (String) record.get("existing_user_id"),
                            (String) record.get("root_account") + canvasTrustSuffix
                        });
                    });
            }

            // Build HTTP URL for SIS import PAI
            HttpUrl sisImportUrl = new HttpUrl.Builder()
                .scheme("https")
                .host(canvasHost)
                .addPathSegment("api")
                .addPathSegment("v1")
                .addPathSegment("accounts")
                .addPathSegment(canvasAccountId)
                .addPathSegment("sis_imports")
                .addQueryParameter("import_type", "instructure_csv")
                .addQueryParameter("extension", "csv")
                .build();

            log.info("Constructed import API URL {}", sisImportUrl);

            // Package the CSV file into a request body
            RequestBody sisImportBody = RequestBody.create(
                MediaType.get("text/csv"),
                Files.readAllBytes(outputPath));

            // Build HTTP request deliver logins file to Canvas environment
            Request sisImportRequest = new Request.Builder()
                .header("Authorization", "Bearer " + canvasApiToken)
                .post(sisImportBody)
                .url(sisImportUrl)
                .build();

            // Deliver file to Canvas environment
            try (Response response = httpClient.newCall(sisImportRequest).execute()) {
                log.info("Canvas API response {}", response);
            }
        }

        /**
         * Helper method to create a directories from a path. If the directories already exist, then this
         * method does nothing.
         * @param directory The directory to create
         */
        private void createDirectory(Path directory) {
            try {
                Files.createDirectories(directory);
            }
            catch(IOException ioException) {
                throw new RuntimeException(ioException);
            }
        }
    }

}
