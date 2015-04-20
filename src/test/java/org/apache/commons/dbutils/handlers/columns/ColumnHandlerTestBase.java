package org.apache.commons.dbutils.handlers.columns;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;

import org.apache.commons.dbutils.ColumnHandler;
import org.junit.Test;
import org.mockito.Mock;

public abstract class ColumnHandlerTestBase {

    @Mock
    protected ResultSet rs;
    protected final ColumnHandler handler;
    protected final Class<?> matchingType;

    public ColumnHandlerTestBase(ColumnHandler handler, Class<?> matchingType) {
        this.handler = handler;
        this.matchingType = matchingType;
    }

    @Test
    public void testMatch() {
        assertTrue(handler.match(matchingType));
    }

    @Test
    public void testMatchNegative() {
        assertFalse(handler.match(Integer.class));
    }

    @Test
    public abstract void testApplyType() throws Exception;
}
