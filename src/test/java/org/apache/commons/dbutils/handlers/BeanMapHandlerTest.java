/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.dbutils.handlers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;

import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.TestBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BeanMapHandlerTest {

    private BeanMapHandler<Long, TestBean> bmh;
    private Map<Long, TestBean> res;
    @Mock private ResultSet rs;
    @Mock private ResultSetMetaData rsmd;
    @Mock private RowProcessor rp;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(Boolean.valueOf(rs.next())).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(rs.getObject(1)).thenReturn(Long.valueOf(23L));
        when(rs.getObject(2)).thenReturn(Long.valueOf(23L));
        when(rs.getObject("id")).thenReturn(Long.valueOf(23L));
        when(rs.getMetaData()).thenReturn(rsmd);
        when(rp.toBean(rs, TestBean.class)).thenReturn(new TestBean());
    }

    private void handle() throws Exception {
        res = bmh.handle(rs);
        assertNotNull(res.get(Long.valueOf(23L)));
    }

    @Test
    public void testBeanMapHandlerClassOfV() throws Exception {
        bmh = new BeanMapHandler<Long, TestBean>(TestBean.class);
        handle();
    }

    @Test
    public void testBeanMapHandlerClassOfVRowProcessor() throws Exception {
        bmh = new BeanMapHandler<Long, TestBean>(TestBean.class, rp);
        handle();
    }

    @Test
    public void testBeanMapHandlerClassOfVInt() throws Exception {
        bmh = new BeanMapHandler<Long, TestBean>(TestBean.class, 2);
        handle();
    }

    @Test
    public void testBeanMapHandlerClassOfVString() throws Exception {
        bmh = new BeanMapHandler<Long, TestBean>(TestBean.class, "id");
        handle();
    }

    @Test
    public void testEmptyResultSet() throws Exception {
        when(Boolean.valueOf(rs.next())).thenReturn(Boolean.FALSE);
        bmh = new BeanMapHandler<Long, TestBean>(TestBean.class);
        res = bmh.handle(rs);
        assertNull(res.get(Long.valueOf(23L)));
    }

}
