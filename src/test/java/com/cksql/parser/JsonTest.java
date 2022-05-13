package com.cksql.parser;

import com.cksql.parser.model.SqlLiteral;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

/** Json test. */
public class JsonTest {

    @Test
    public void test0() throws JsonProcessingException {
        SqlLiteral literal = new ObjectMapper().readValue("{\"value\": [1,2]}", SqlLiteral.class);
        System.out.println(literal);
    }
}
