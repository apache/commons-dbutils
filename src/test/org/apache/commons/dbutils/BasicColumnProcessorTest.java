/*
 * Copyright 2003-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.dbutils;

import java.sql.SQLException;

/**
 * BasicColumnProcessorTest
 */
public class BasicColumnProcessorTest extends BaseTestCase {

    private static final RowProcessor rowProc =
        new BasicRowProcessor(new BasicColumnProcessor());

    /**
     * Constructor for BasicColumnProcessorTest.
     * @param name
     */
    public BasicColumnProcessorTest(String name) {
        super(name);
    }

    public void testProcess() throws SQLException {
        int rowCount = 0;
        TestBean b = null;
        while (this.rs.next()) {
            b = (TestBean) rowProc.toBean(this.rs, TestBean.class);
            assertNotNull(b);
            rowCount++;
        }

        assertEquals(ROWS, rowCount);
        assertEquals(13.0, b.getColumnProcessorDoubleTest(), 0);
    }

}
