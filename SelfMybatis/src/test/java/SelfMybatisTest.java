/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import mapper.PersonMapper;
import org.junit.jupiter.api.Test;
import self.SelfConfiguration;
import self.SelfSqlSession;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author liuwei
 */
public class SelfMybatisTest {

    @Test
    public void test() {
        try(SelfSqlSession session = buildSqlSessionFactory()) {
            PersonMapper personMapper = session.getMapper(PersonMapper.class);
            personMapper.createTable();
            personMapper.save();
            List<Object> personList = personMapper.list();
            for (Object person: personList) {
                System.out.println(person.toString());
            }
        }
    }

    public static SelfSqlSession buildSqlSessionFactory() {
        String JDBC_DRIVER = "org.h2.Driver";
        String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
        String USER = "sa";
        String PASS = "";

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(USER);
        config.setPassword(PASS);
        config.setDriverClassName(JDBC_DRIVER);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        DataSource dataSource = new HikariDataSource(config);

        SelfConfiguration configuration = new SelfConfiguration(dataSource);
//        configuration.getTypeHandlerRegistry().register(String[].class, JdbcType.VARCHAR, StringArrayTypeHandler.class);
        configuration.addMapper(PersonMapper.class);
        return new SelfSqlSession(configuration);
    }
}
