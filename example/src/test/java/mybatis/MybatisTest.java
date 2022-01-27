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

package mybatis;

import entity.Person;
import mapper.PersonMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

/**
 * @author liuwei
 */
public class MybatisTest {

    @Test
    public void test() {
        try(SqlSession session = buildSqlSessionFactory().openSession()) {
            PersonMapper personMapper = session.getMapper(PersonMapper.class);
            personMapper.save(Person.builder().id(1L).name("1").build());
            Person person = personMapper.getPersonById(1);
            System.out.println(person);
        }
    }

    public static SqlSessionFactory buildSqlSessionFactory() {
        String JDBC_DRIVER = "org.h2.Driver";
        String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
        String USER = "sa";
        String PASS = "";
        DataSource dataSource = new PooledDataSource(JDBC_DRIVER, DB_URL, USER, PASS);
        Environment environment = new Environment("Development", new JdbcTransactionFactory(), dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(PersonMapper.class);
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        return builder.build(configuration);
    }
}
