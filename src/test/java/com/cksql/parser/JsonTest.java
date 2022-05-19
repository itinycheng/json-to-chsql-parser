package com.cksql.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/** Json test. */
public class JsonTest {

    @Test
    public void test0() throws JsonProcessingException {
        Object obj = new ObjectMapper().readValue("[1,2]", Object.class);
        System.out.println(obj);
    }

    @Test
    public void test1() {
        String format1 = String.format("args: %s, %s", new Object[] {"a", "b"});
        String format2 = String.format("args: %s, %s", Arrays.asList("a", "b").toArray());
        System.out.println(format1);
        System.out.println(format2);
    }

    @Test
    public void test2() {
        Map<Integer, String> treeMap = new TreeMap<>(Comparator.naturalOrder());
        treeMap.put(2, "2");
        treeMap.put(0, "0");
        treeMap.put(1, "1");
        System.out.println(Arrays.toString(treeMap.values().toArray()));
    }
}
