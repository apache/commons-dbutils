/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.dbutils;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BeanPropertyListTest {



    static class TestBean {

        private String name;

        private List<String> favorite;




        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        public List<String> getFavorite() {
            return favorite;
        }

        public void setFavorite(List<String> favorite) {
            this.favorite = favorite;
        }
    }

    private QueryRunner runner;

    @BeforeEach
    public void setUp() throws Exception {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        runner = new QueryRunner(dataSource);

        // create table
        runner.update("create table TEST_BEAN(name varchar(30), favorite varchar(100) )");

        // insert rows
        runner.update("insert into TEST_BEAN(name, favorite) values(?, ?)", "John", "FOOTBALL,SHOPPING");
    }

    @AfterEach
    public void tearDown() throws Exception {
        runner.update("drop table TEST_BEAN");
    }

    @Test
    void test() throws Exception {
        String sql = "select * from TEST_BEAN";
        TestBean bean = runner.query(sql, new BeanHandler<>(TestBean.class));

        Assertions.assertEquals("John", bean.getName());
        Assertions.assertEquals(2, bean.getFavorite().size());
        Assertions.assertEquals("FOOTBALL", bean.getFavorite().get(0));
        Assertions.assertEquals("SHOPPING", bean.getFavorite().get(1));
    }


}
