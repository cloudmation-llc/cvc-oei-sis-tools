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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Implementation of a record source for the Colleague SIS which expects to find cross-enrollment records
 * in a local CSV file provided by an integration partner.
 */
@Log4j2
@Service
@ConditionalOnProperty(
    value="cvc.sis.type",
    havingValue = "colleague")
public class ColleagueCrossEnrollmentRecordSource extends CrossEnrollmentRecordSource {

    @Value("${cvc.sis.canvasInputFile}")
    String propertyInputFile;

    @SuppressWarnings("unchecked")
    @Override
    public List<CrossEnrollmentRecord> getRecords() {
        // Create and validate path of input file
        Path inputFile = Paths.get(propertyInputFile);
        if(Files.notExists(inputFile)) {
            throw new RuntimeException(
                new FileNotFoundException("Could not find cross-enrollment input file " + inputFile));
        }
        log.info("Loading cross-enrollment input file {}", inputFile);

        // Open CSV input file
        try(BufferedReader fileReader = Files.newBufferedReader(inputFile)) {
            // Read entries into local collection
            return new CsvToBeanBuilder(fileReader)
                .withType(CrossEnrollmentRecord.class)
                .build()
                .parse();
        }
        catch(Exception anyException) {
            // Rethrow as unchecked exception
            throw new RuntimeException(anyException);
        }
    }

}
