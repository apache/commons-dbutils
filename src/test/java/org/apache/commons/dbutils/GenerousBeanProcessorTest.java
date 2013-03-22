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
package org.apache.commons.dbutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import java.beans.PropertyDescriptor;
import java.sql.ResultSetMetaData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class GenerousBeanProcessorTest {
    
    GenerousBeanProcessor processor = new GenerousBeanProcessor();
    @Mock ResultSetMetaData metaData;
    PropertyDescriptor[] propDescriptors;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        propDescriptors = new PropertyDescriptor[3];
        
        propDescriptors[0] = new PropertyDescriptor("one", TestBean.class);
        propDescriptors[1] = new PropertyDescriptor("two", TestBean.class);
        propDescriptors[2] = new PropertyDescriptor("three", TestBean.class);
    }

    @SuppressWarnings("boxing") // test code
    @Test
    public void testMapColumnsToPropertiesWithOutUnderscores() throws Exception {
        when(metaData.getColumnCount()).thenReturn(3);
        
        when(metaData.getColumnLabel(1)).thenReturn("three");
        when(metaData.getColumnLabel(2)).thenReturn("one");
        when(metaData.getColumnLabel(3)).thenReturn("two");
        
        int[] ret = processor.mapColumnsToProperties(metaData, propDescriptors);
        
        assertNotNull(ret);
        assertEquals(4, ret.length);
        assertEquals(-1, ret[0]);
        assertEquals(2, ret[1]);
        assertEquals(0, ret[2]);
        assertEquals(1, ret[3]);
    }
    
    @SuppressWarnings("boxing") // test code
    @Test
    public void testMapColumnsToPropertiesWithUnderscores() throws Exception {
        when(metaData.getColumnCount()).thenReturn(3);
        
        when(metaData.getColumnLabel(1)).thenReturn("t_h_r_e_e");
        when(metaData.getColumnLabel(2)).thenReturn("o_n_e");
        when(metaData.getColumnLabel(3)).thenReturn("t_w_o");
        
        int[] ret = processor.mapColumnsToProperties(metaData, propDescriptors);
        
        assertNotNull(ret);
        assertEquals(4, ret.length);
        assertEquals(-1, ret[0]);
        assertEquals(2, ret[1]);
        assertEquals(0, ret[2]);
        assertEquals(1, ret[3]);
    }
    
    static class TestBean {
        private String one;
        private int two;
        private long three;
        
        public String getOne() {
            return one;
        }
        
        public void setOne(String one) {
            this.one = one;
        }
        
        public int getTwo() {
            return two;
        }
        
        public void setTwo(int two) {
            this.two = two;
        }
        
        public long getThree() {
            return three;
        }
        
        public void setThree(long three) {
            this.three = three;
        }
    }

}
