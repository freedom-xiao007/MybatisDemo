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

package raw;

import lombok.Data;
import org.junit.jupiter.api.Test;

import java.sql.*;

/**
 * @author liuwei
 */
public class JdbcTest {

    @Test
    public void test() {
        Connection conn = null;
        Statement stmt = null;
        try {
            // STEP 1: Register JDBC driver
            // JDBC driver name and database URL
            String JDBC_DRIVER = "org.h2.Driver";
            Class.forName(JDBC_DRIVER);

            //STEP 2: Open a connection
            System.out.println("Connecting to database...");
            String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
            //  Database credentials
            String USER = "sa";
            String PASS = "";
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //STEP 3: Execute a query
            System.out.println("Creating table in given database...");
            stmt = conn.createStatement();
            String sql =  "CREATE TABLE   REGISTRATION " +
                    "(id INTEGER not NULL, " +
                    " first VARCHAR(255), " +
                    " last VARCHAR(255), " +
                    " age INTEGER, " +
                    " PRIMARY KEY ( id ))";
            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");

            String insertSql = "insert into REGISTRATION (id, first, last, age) values (?, ?, ?, ?)";
            PreparedStatement insertPreparedStatement = conn.prepareStatement(insertSql);
            insertPreparedStatement.setInt(1, 1);
            insertPreparedStatement.setString(2, "1");
            insertPreparedStatement.setString(3, "1");
            insertPreparedStatement.setInt(4, 1);
            insertPreparedStatement.executeUpdate();

            String querySql = "select * from REGISTRATION";
            ResultSet resultSet = stmt.executeQuery(querySql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                Registration registration = new Registration();
                registration.setId(resultSet.getInt(metaData.getColumnName(1)));
                registration.setFirst(resultSet.getString(metaData.getColumnName(2)));
                registration.setLast(resultSet.getString(metaData.getColumnName(3)));
                registration.setAge(resultSet.getInt(metaData.getColumnName(4)));
                System.out.println(registration);
            }

            // STEP 4: Clean-up environment
            stmt.close();
            conn.close();
        } catch(Exception se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }//Handle errors for Class.forName
        finally {
            //finally block used to close resources
            try{
                if(stmt!=null) stmt.close();
            } catch(SQLException ignored) {
            } // nothing we can do
            try {
                if(conn!=null) conn.close();
            } catch(SQLException se){
                se.printStackTrace();
            } //end finally try
        } //end try
        System.out.println("Goodbye!");
    }

    @Data
    static class Registration {
        private int id;
        private String first;
        private String last;
        private int age;
    }
}
