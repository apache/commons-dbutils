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
package org.apache.commons.dbutils.handlers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;

import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.TestBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BeanMapHandlerTest {

    private BeanMapHandler<Long, TestBean> bmh;
    private Map<Long, TestBean> res;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private ResultSet rs;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private ResultSetMetaData rsmd;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private RowProcessor rp;

    private void handle() throws Exception {
        res = bmh.handle(rs);
        assertNotNull(res.get(Long.valueOf(23L)));
    }

    @BeforeEach
    public void setUp() throws Exception {
        when(Boolean.valueOf(rs.next())).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(rs.getObject(1)).thenReturn(Long.valueOf(23L));
        when(rs.getObject(2)).thenReturn(Long.valueOf(23L));
        when(rs.getObject("id")).thenReturn(Long.valueOf(23L));
        when(rs.getMetaData()).thenReturn(rsmd);
        when(rp.toBean(rs, TestBean.class)).thenReturn(new TestBean());
    }

    @Test
    void testBeanMapHandlerClassOfV() throws Exception {
        bmh = new BeanMapHandler<>(TestBean.class);
        handle();
    }

    @Test
    void testBeanMapHandlerClassOfVInt() throws Exception {
        bmh = new BeanMapHandler<>(TestBean.class, 2);
        handle();
    }

    @Test
    void testBeanMapHandlerClassOfVRowProcessor() throws Exception {
        bmh = new BeanMapHandler<>(TestBean.class, rp);
        handle();
    }

    @Test
    void testBeanMapHandlerClassOfVString() throws Exception {
        bmh = new BeanMapHandler<>(TestBean.class, "id");
        handle();
    }

    @Test
    void testEmptyResultSet() throws Exception {
        when(Boolean.valueOf(rs.next())).thenReturn(Boolean.FALSE);
        bmh = new BeanMapHandler<>(TestBean.class);
        res = bmh.handle(rs);
        assertNull(res.get(Long.valueOf(23L)));
    }

}
