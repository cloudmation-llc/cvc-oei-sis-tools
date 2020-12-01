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

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.log4j.Log4j2;
import org.cvcoei.sistools.common.io.FileUtilities;
import org.cvcoei.sistools.common.io.ReaderWithCharset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Implementation of a record source for the Colleague SIS which expects to find cross-enrollment records
 * in a local directory of CSV files provided by an integration partner.
 */
@Log4j2
@Service
@ConditionalOnProperty(
    value="cvc.sis.type",
    havingValue = "colleague")
public class ColleagueCrossEnrollmentRecordSource extends CrossEnrollmentRecordSource {

    private static final List<CrossEnrollmentRecord> EMPTY = new ArrayList<>();

    @Value("${cvc.cross-enrollment.completedDirectory}")
    String propertyCompletedDirectory;

    @Value("${cvc.cross-enrollment.failedDirectory}")
    String propertyFailedDirectory;

    @Value("${cvc.cross-enrollment.inputDirectory}")
    String propertyInputDirectory;

    @Value("${cvc.cross-enrollment.inputPattern}")
    String propertyInputPattern;

    @Override
    public List<CrossEnrollmentRecord> getRecords() throws Exception {
        // Resolve local filesystem
        FileSystem filesystem = FileSystems.getDefault();

        // Parse input directory
        Path inputDirectory = Paths.get(propertyInputDirectory);
        log.debug("Using {} for the input file directory", inputDirectory);
        PathMatcher inputFileMatcher = filesystem.getPathMatcher("glob:" + propertyInputPattern);

        // Setup completed directory
        Path completedDirectory = inputDirectory.resolve(propertyCompletedDirectory);
        log.debug("Using {} for the completed file directory", completedDirectory);
        Files.createDirectories(completedDirectory);

        // Setup failed directory
        Path failedDirectory = inputDirectory.resolve(propertyFailedDirectory);
        log.debug("Using {} for the failed file directory", failedDirectory);
        Files.createDirectories(failedDirectory);

        try {
            // Define a supplier which creates a final collection of all the cross-enrollment records
            Supplier<List<CrossEnrollmentRecord>> supplier = ArrayList::new;

            // Walk input directory
            return Files
                .walk(inputDirectory, 1)

                // Log the file getting scanned before any processing for debugging
                .peek(path -> log.debug("Scanning input file {}", path))

                // Capture only files which match the provided pattern
                .filter(path -> Files.isRegularFile(path) && inputFileMatcher.matches(path))

                // Convert each CSV into a stream of records, and merge into a single output
                .flatMap(path -> {
                    // Open CSV input file
                    try(ReaderWithCharset readerWithCharset = FileUtilities.getReaderWithCharsetDetection(path)) {
                        log.info("Processing cross-enrollment input file {} (charset = {})", path, readerWithCharset.getCharset());

                        // Read entry(ies) from file
                        List<CrossEnrollmentRecord> parsedRecords =
                            new CsvToBeanBuilder<CrossEnrollmentRecord>(readerWithCharset.getReader())
                                .withType(CrossEnrollmentRecord.class)
                                .build()
                                .parse();

                        // Move successfully parsed file into completed directory
                        FileUtilities.move(path, completedDirectory.resolve(path.getFileName()));

                        log.info("Successfully parsed cross-enrollment file {}", path);
                        return parsedRecords.stream();
                    }
                    catch(Exception exception) {
                        // Move the file with the error back into the input directory
                        FileUtilities.move(path, failedDirectory.resolve(path.getFileName()));

                        log.error("Failed to parse {} - input filed has been moved to the failed directory", path, exception);
                        return EMPTY.stream();
                    }
                })

                // Log each discovered record
                .peek(crossEnrollmentRecord -> {
                    log.debug("Processing cross-enrollment record {}", crossEnrollmentRecord);
                })

                // Send to a final collection
                .collect(Collectors.toCollection(supplier));
        }
        catch(Exception anyException) {
            // Rethrow as unchecked exception
            throw new RuntimeException(anyException);
        }
    }

}
