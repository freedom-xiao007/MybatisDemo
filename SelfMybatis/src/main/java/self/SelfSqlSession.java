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

import java.io.Closeable;
import java.lang.reflect.Proxy;

/**
 * @author liuwei
 */
public class SelfSqlSession implements Closeable {

    private final SelfConfiguration config;

    public SelfSqlSession(SelfConfiguration configuration) {
        this.config = configuration;
    }

    @Override
    public void close() {
    }

    public <T> T getMapper(Class<?> mapperClass) {
        final MapperProxy<T> proxy = new MapperProxy(config);
        return (T) Proxy.newProxyInstance(mapperClass.getClassLoader(), new Class[] {mapperClass}, proxy);
    }
}
