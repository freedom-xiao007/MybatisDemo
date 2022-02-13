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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuwei
 */
public class ResultHandler {

    public List<Object> parse(ResultSet res) throws SQLException {
        if (res == null) {
            return null;
        }

        final List<Object> list = new ArrayList<>(res.getFetchSize());
        final ResultSetMetaData metaData = res.getMetaData();
        while (res.next()) {
            final int count =metaData.getColumnCount();
            final List<Object> val = new ArrayList<>(count);
            for (int i=1; i <= count; i++) {
                final String name = metaData.getColumnName(i);
                final Object value = res.getObject(name);
                val.add(value);
            }
            list.add(val);
        }
        return list;
    }
}
