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

import lombok.extern.log4j.Log4j2;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a record source for the Banner SIS which queries the Oracle database directly to
 * read cross-enrollment records from a staging table provided by an external integration partner.
 */
@Log4j2
@Service
@ConditionalOnProperty(
    value="cvc.sis.type",
    havingValue = "banner")
public class BannerCrossEnrollmentRecordSource extends CrossEnrollmentRecordSource {

    @Value("${cvc.sis.url}")
    private String oracleUrl;

    @Value("${cvc.sis.user}")
    private String oracleUser;

    @Value("${cvc.sis.password}")
    private String oraclePassword;

    @Value("${cvc.canvas-logins.sql.banner}")
    private String sqlQueryStagingTable;

    @Override
    public List<CrossEnrollmentRecord> getRecords() {
        try {
            // Create Oracle datasource
            OracleDataSource oracleDataSource = new OracleDataSource();

            // Configure datasource
            oracleDataSource.setURL(oracleUrl);
            oracleDataSource.setUser(oracleUser);
            oracleDataSource.setPassword(oraclePassword);

            // Create a JdbcTemplate for interacting with the SIS database
            JdbcTemplate jdbc = new JdbcTemplate(oracleDataSource);

            // Execute SIS query into a collection
            List<CrossEnrollmentRecord> crossEnrollmentRecords = new ArrayList<>();

            jdbc
                .queryForList(sqlQueryStagingTable)
                .forEach(record -> {
                    crossEnrollmentRecords.add(
                        new CrossEnrollmentRecord(
                            (String) record.get("root_account"),
                            (String) record.get("existing_user_id"),
                            (String) record.get("login_id"),
                            (String) record.get("user_id")
                        ));
                });

            return crossEnrollmentRecords;
        }
        catch(Exception anyException) {
            // Rethrow as unchecked exception
            throw new RuntimeException(anyException);
        }
    }

}
