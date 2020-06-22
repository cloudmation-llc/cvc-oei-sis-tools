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

package org.cvcoei.sistools.common.config;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SisDatabaseConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "cvc.sis.properties")
    public Map<String, String> datasourceProperties() {
        return new HashMap<>();
    }

    @Bean("sisDatasource")
    @ConditionalOnExpression("'${cvc.sis.type}' == 'oracle'")
    DataSource getOracleDatasource(@Autowired Map<String, String> datasourceProperties) throws SQLException {
        // Create Oracle datasource
        OracleDataSource oracleDataSource = new OracleDataSource();

        // Configure datasource
        oracleDataSource.setURL(datasourceProperties.get("url"));
        oracleDataSource.setUser(datasourceProperties.get("user"));
        oracleDataSource.setPassword(datasourceProperties.get("password"));

        return oracleDataSource;
    }

    @Bean("sisDatasource")
    @ConditionalOnExpression("'${cvc.sis.type}' == 'mssql'")
    DataSource getMssqlDatasource(@Autowired Map<String, String> datasourceProperties) {
        // Create SQL Server datasource
        SQLServerDataSource sqlServerDataSource = new SQLServerDataSource();

        // Configure datasource
        sqlServerDataSource.setServerName(datasourceProperties.get("serverName"));
        sqlServerDataSource.setPortNumber(Integer.parseInt(datasourceProperties.get("port")));
        sqlServerDataSource.setDatabaseName(datasourceProperties.get("database"));
        sqlServerDataSource.setUser(datasourceProperties.get("user"));
        sqlServerDataSource.setPassword(datasourceProperties.get("password"));

        return sqlServerDataSource;
    }

}
