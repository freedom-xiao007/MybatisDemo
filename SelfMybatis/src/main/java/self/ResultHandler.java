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

import self.typehandler.TypeHandler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuwei
 */
public class ResultHandler {

    public List<Object> parse(String id, ResultSet res, SelfConfiguration config) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (res == null) {
            return null;
        }

        ResultMap resultMap = config.getResultType(id);

        final List<Object> list = new ArrayList<>(res.getFetchSize());
        while (res.next()) {
            Class<?> returnType = (Class<?>) resultMap.getReturnType();
            Object val = returnType.getDeclaredConstructor().newInstance();
            for (Field field: returnType.getDeclaredFields()) {
                final String name = field.getName();
                TypeHandler typeHandler = resultMap.getTypeHandlerMaps().get(field.getType().getName());
                Object value = typeHandler.getResult(res, name);
                String methodEnd = name.substring(0, 1).toUpperCase() + name.substring(1);
                Method setMethod = val.getClass().getDeclaredMethod("set" + methodEnd, field.getType());
                setMethod.invoke(val, value);
            }
            list.add(val);
        }
        return list;
    }
}
