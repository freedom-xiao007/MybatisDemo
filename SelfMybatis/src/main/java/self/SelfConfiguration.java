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
import self.annotation.Insert;
import self.annotation.Select;
import self.typehandler.LongTypeHandler;
import self.typehandler.StringTypeHandler;
import self.typehandler.TypeHandler;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

/**
 * @author liuwei
 */
public class SelfConfiguration {

    private final DataSource dataSource;
    private final Map<String, SqlSource> sqlCache = new HashMap<>();
    private final Map<String, ResultMap> resultMapCache = new HashMap<>();
    private final Map<String, Map<String, Integer>> jdbcTypeCache = new HashMap<>();
    private final Map<Class<?>, Map<Integer, TypeHandler>> typeHandlerMap = new HashMap<>();

    public SelfConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
        try {
            initTypeHandlers();
            initJdbcTypeCache();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initTypeHandlers() {
        final Map<Integer, TypeHandler> varchar = new HashMap<>();
        varchar.put(JDBCType.VARCHAR.getVendorTypeNumber(), StringTypeHandler.getInstance());
        typeHandlerMap.put(String.class, varchar);

        final Map<Integer, TypeHandler> intType = new HashMap<>();
        intType.put(JDBCType.INTEGER.getVendorTypeNumber(), LongTypeHandler.getInstance());
        typeHandlerMap.put(Long.class, intType);
    }

    private void initJdbcTypeCache() throws SQLException {
        try (Connection conn = dataSource.getConnection()){
            final DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet tableNameRes = dbMetaData.getTables(conn.getCatalog(),null, null,new String[] { "TABLE" });
            final List<String> tableNames = new ArrayList<>(tableNameRes.getFetchSize());
            while (tableNameRes.next()) {
                tableNames.add(tableNameRes.getString("TABLE_NAME"));
            }

            for (String tableName : tableNames) {
                try {
                    String sql = "select * from " + tableName;
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();
                    Map<String, Integer> jdbcTypeMap = new HashMap<>(columnCount);
                    for (int i = 1; i < columnCount + 1; i++) {
                        jdbcTypeMap.put(meta.getColumnName(i).toLowerCase(), meta.getColumnType(i));
                    }
                    jdbcTypeCache.put(tableName.toLowerCase(), jdbcTypeMap);
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Mapper添加
     * 方法路径作为唯一的id
     * 保存接口方法的SQL类型和方法
     * 保存接口方法返回类型
     * @param mapperClass mapper
     */
    public void addMapper(Class<?> mapperClass) {
        final String classPath = mapperClass.getPackageName();
        final String className = mapperClass.getName();
        for (Method method: mapperClass.getMethods()) {
            final String id = StringUtils.joinWith("." ,classPath, className, method.getName());
            for (Annotation annotation: method.getAnnotations()) {
                if (annotation instanceof Select) {
                    addSqlSource(id, ((Select) annotation).value(), SqlType.SELECT);
                    continue;
                }
                if (annotation instanceof Insert) {
                    addSqlSource(id, ((Insert) annotation).value(), SqlType.INSERT);
                }
            }

            addResultMap(id, method);
        }

    }

    private void addResultMap(String id, Method method) {
        if (method.getReturnType().getName().equals("void")) {
            return;
        }

        Type type = method.getGenericReturnType();
        Type returnType;
        if (type instanceof ParameterizedType) {
            returnType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            returnType = method.getReturnType();
        }
        resultMapCache.put(id, ResultMap.builder()
                .returnType(returnType)
                .typeHandlerMaps(buildTypeHandlerMaps((Class<?>) returnType))
                .build());
    }

    private Map<String, TypeHandler> buildTypeHandlerMaps(Class<?> returnType) {
        final String tableName = StringUtils.substringAfterLast(returnType.getTypeName(), ".").toLowerCase();
        final Map<String, TypeHandler> typeHandler = new HashMap<>(returnType.getDeclaredFields().length);
        for (Field field : returnType.getDeclaredFields()) {
            final String javaType = field.getType().getName();
            final String name = field.getName();
            final Integer jdbcType = jdbcTypeCache.get(tableName).get(name);
            typeHandler.put(javaType, typeHandlerMap.get(field.getType()).get(jdbcType));
        }
        return typeHandler;
    }

    private void addSqlSource(final String id, final String sql, final SqlType selectType) {
        System.out.println("add sql source: " + id);
        final SqlSource sqlSource = SqlSource.builder()
                .type(selectType)
                .sql(sql)
                .build();
        sqlCache.put(id, sqlSource);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public SqlSource getSqlSource(final String id) {
        if (sqlCache.containsKey(id)) {
            return sqlCache.get(id);
        }
        throw new RuntimeException("don't find mapper match: " + id);
    }

    public ResultMap getResultType(String id) {
        if (resultMapCache.containsKey(id)) {
            return resultMapCache.get(id);
        }
        throw new RuntimeException("don't find mapper result type: " + id);
    }
}
