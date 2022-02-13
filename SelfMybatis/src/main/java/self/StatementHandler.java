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

package self;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.sql.*;

/**
 * @author liuwei
 */
public class StatementHandler {

    public ResultSet prepare(Connection conn, Object proxy, Method method, Object[] args, SelfConfiguration config) throws SQLException {
        final String classPath = method.getDeclaringClass().getPackageName();
        final String className = method.getDeclaringClass().getName();
        final String methodName = method.getName();
        final String id = StringUtils.joinWith(".", classPath, className, methodName);
        final SqlSource sqlSource = config.getSqlSource(id);
        if (sqlSource.getType().equals(SqlType.SELECT)) {
            return select(conn, sqlSource);
        }
        if (sqlSource.getType().equals(SqlType.INSERT)) {
            return insert(conn, sqlSource);
        }
        throw new RuntimeException("don't support this sql type");
    }

    private ResultSet insert(Connection conn, SqlSource sqlSource) throws SQLException {
        final Statement statement = conn.createStatement();
        statement.execute(sqlSource.getSql());
        return null;
    }

    private ResultSet select(Connection conn, SqlSource sqlSource) throws SQLException {
        final Statement statement = conn.createStatement();
        return statement.executeQuery(sqlSource.getSql());
    }
}
