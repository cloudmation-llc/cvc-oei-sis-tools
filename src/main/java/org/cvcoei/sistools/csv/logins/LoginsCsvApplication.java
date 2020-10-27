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
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cvcoei.sistools.common.http.HttpApiService;
import org.cvcoei.sistools.common.json.JsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

@SpringBootApplication(
    scanBasePackages = { "org.cvcoei.sistools.common", "org.cvcoei.sistools.csv.logins" },
    proxyBeanMethods = false,
    exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JndiDataSourceAutoConfiguration.class,
        XADataSourceAutoConfiguration.class
    })
@EnableAsync
@Log4j2
public class LoginsCsvApplication {

    @Service
    public static class Runner implements ApplicationRunner {

        public final static String[] CSV_HEADER = new String[] {
            "user_id",
            "login_id",
            "existing_user_id",
            "root_account" };

        private final Logger sisErrorsLog = LogManager.getLogger("canvas.sis-import.errors");

        @Value("${cvc.canvas.accountId}")
        String canvasAccountId;

        @Value("${cvc.canvas.apiToken}")
        String canvasApiToken;

        @Value("${cvc.canvas.host}")
        String canvasHost;

        @Value("${cvc.cross-enrollment.outputFile}")
        String pathOutputFile;

        @Autowired
        HttpApiService httpApiService;

        @Autowired
        JsonService jsonService;

        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        @Autowired
        CrossEnrollmentRecordSource crossEnrollmentRecordSource;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            log.debug("Cross-enrollment record source {}", crossEnrollmentRecordSource);

            // Resolve input records from the configured source
            List<CrossEnrollmentRecord> inputRecords = crossEnrollmentRecordSource.getRecords();

            // Validate there is at least one record to process
            if(inputRecords.size() < 1) {
                log.info("No cross-enrollment records to process - exiting");
                System.exit(0);
            }

            // Set up output path
            Path outputPath = Paths.get(pathOutputFile);
            Path outputDirectory = outputPath.getParent();
            createDirectory(outputDirectory);

            // Open a local CSV file and transform input
            try(CSVWriter writer = new CSVWriter(Files.newBufferedWriter(outputPath))) {
                // Write header
                writer.writeNext(CSV_HEADER);

                // Iterate and write records
                for(CrossEnrollmentRecord record : inputRecords) {
                    writer.writeNext(new String[] {
                        record.getTeachingCollegeId(),
                        record.getHomeCollegeLoginId(),
                        record.getHomeCollegeId(),
                        record.getCanvasRootAccount()
                    });
                }
            }

            // Build HTTP URL for SIS import API
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

            log.debug("Constructed import API URL {}", sisImportUrl);

            // Package the CSV file into a request body
            RequestBody sisImportBody = RequestBody.create(
                Files.readAllBytes(outputPath),
                MediaType.get("text/csv"));

            // Build HTTP request deliver logins file to Canvas environment
            Request sisImportRequest = new Request.Builder()
                .header("Authorization", "Bearer " + canvasApiToken)
                .post(sisImportBody)
                .url(sisImportUrl)
                .build();

            // Deliver file to Canvas environment
            Map<String, Object> importCreationResponse =
                httpApiService.call(sisImportRequest);
            log.debug("Import creation response from Canvas {}", importCreationResponse);

            // Fix request ID (Gson treats all numeric values as floating point)
            int importRequestId = ((Double) importCreationResponse.get("id")).intValue();
            log.info("Uploaded logins.csv, and created SIS import with ID {}", importRequestId);

            // Build request for polling the import status API
            HttpUrl sisStatusUrl = new HttpUrl.Builder()
                .scheme("https")
                .host(canvasHost)
                .addPathSegment("api")
                .addPathSegment("v1")
                .addPathSegment("accounts")
                .addPathSegment(canvasAccountId)
                .addPathSegment("sis_imports")
                .addPathSegment(Integer.toString(importRequestId))
                .addQueryParameter("import_type", "instructure_csv")
                .addQueryParameter("extension", "csv")
                .build();

            Request sisStatusRequest = new Request.Builder()
                .header("Authorization", "Bearer " + canvasApiToken)
                .get()
                .url(sisStatusUrl)
                .build();

            log.debug("Constructed import status API URL {}", sisStatusUrl);

            // Poll the import status API until the job is completed or has an error
            Map<String, Object> finalStatusResponse = httpApiService
                .poll(
                    sisStatusRequest,
                    Duration.ofSeconds(15),
                    "workflow_state != 'initializing' and workflow_state != 'created' and workflow_state != 'importing'");

            log.debug("Final import status {}", finalStatusResponse);

            // Write the final output status from Canvas to a local file for inspection
            Path importStatusOutputFile = outputPath.resolveSibling("canvas_sis_import_status_" + importRequestId + ".log");

            Files.write(
                importStatusOutputFile,
                Collections.singletonList(jsonService.toJsonPretty(finalStatusResponse)),
                Charset.defaultCharset());

            // Rename the logins.csv output file with its import ID for archiving
            Files.move(
                outputPath,
                outputPath.resolveSibling("logins_" + importRequestId + ".csv"));

            log.info("SIS import completed. Check logs for output");

            // If there is an errors attachment, fetch and log the file contents for inspection
            if(finalStatusResponse.containsKey("errors_attachment")) {
                sisErrorsLog.error("Logging errors from Canvas SIS import {}", importRequestId);

                Map<String, Object> errorAttachment = (Map<String, Object>) finalStatusResponse.get("errors_attachment");

                // Fetch the error output from Canvas and pipe into dedicated log file through Log4j
                Request sisErrorsDownloadRequest = new Request
                    .Builder()
                    .url((String) errorAttachment.get("url"))
                    .build();

                httpApiService.fetchLines(sisErrorsDownloadRequest, sisErrorsLog::error);
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
