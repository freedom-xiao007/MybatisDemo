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

package com.example.mybatisspring.mapper;

import com.example.mybatisspring.entity.Person;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author liuwei
 */
@Mapper
public interface PersonMapper {

    @Insert("create table person(id int not null, name varchar(255))")
    Integer createTable();

    @Insert("Insert into person(id, name) values (#{id}, #{name})")
    Integer save(Person person);

    @Select("Select id, name from Person where id=#{id}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property="name", column = "name"),
    })
    Person getPersonById(Integer id);

    @Select("Select id, name from Person where id=#{id} and name=#{name}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property="name", column = "name"),
    })
    Person getPersonByCondition(@Param("id") Integer id, @Param("name") String name);

    @Select("Select * from Person where id=#{id} or name=#{name}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property="name", column = "name"),
    })
    List<Person> getPersonByMap(Map<String, Object> query);

    @Select("Select id, name from Person where id=#{id} and name=#{person.name}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property="name", column = "name"),
    })
    Person getPersonByMul(@Param("person") Person person, @Param("id") Integer id);
}
