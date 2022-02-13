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

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuwei
 */
public class SelfConfiguration {

    private final DataSource dataSource;
    private final Map<String, SqlSource> sqlCache = new HashMap<>();

    public SelfConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Mapper添加
     * 保存接口方法的SQL类型和方法
     * 方法路径作为唯一的id
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
        }
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
}
