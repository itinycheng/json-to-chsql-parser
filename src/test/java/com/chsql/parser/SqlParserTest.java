package com.chsql.parser;

import com.chsql.parser.common.ColumnExtra;
import com.chsql.parser.common.SqlContext;
import com.chsql.parser.common.TableExtra;
import com.chsql.parser.enums.JoinType;
import com.chsql.parser.model.SqlSelect;
import com.chsql.parser.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/** sql parser test. */
public class SqlParserTest {

    private ClassLoader classLoader;

    private SqlContext context;

    @Before
    public void before() {
        classLoader = Thread.currentThread().getContextClassLoader();
        context =
                new SqlContext.Builder()
                        .addTable(
                                new TableExtra(
                                        1L,
                                        "t_user",
                                        "ods",
                                        "ReplicatedReplacingMergeTree",
                                        "id",
                                        JoinType.COMMON))
                        .addTable(
                                new TableExtra(
                                        2L,
                                        "t_order",
                                        "ups",
                                        "ReplicatedReplacingMergeTree",
                                        "user_id",
                                        JoinType.COMMON))
                        .addColumn(new ColumnExtra("id", 1L, "Int64"))
                        .addColumn(new ColumnExtra("type", 1L, "String"))
                        .addColumn(new ColumnExtra("region", 1L, "String"))
                        .addColumn(new ColumnExtra("favor", 1L, "Array(String)"))
                        .addColumn(new ColumnExtra("props", 1L, "Map(String, UInt64)"))
                        .addColumn(new ColumnExtra("create_at", 1L, "DateTime64(3)"))
                        .addColumn(new ColumnExtra("id", 2L, "Int64"))
                        .addColumn(new ColumnExtra("user_id", 2L, "Int64"))
                        .addColumn(new ColumnExtra("goods", 2L, "Map(String, UInt32)"))
                        .build();
    }

    @Test
    public void testGroupBy() {
        parseToSQL("group_by_limit.json");
    }

    @Test
    public void testWhere() {
        parseToSQL("group_by_where_limit.json");
    }

    @Test
    public void testTopNAndOther() {
        parseToSQL("topn_and_other.json");
    }

    @Test
    public void testTableJoin() {
        parseToSQL("table_join.json");
    }

    @Test
    public void testTreeMap() {
        Map<String, String> treeMap = new TreeMap<>(Comparator.naturalOrder());
        treeMap.put("1", "a");
        treeMap.put("3", "c");
        treeMap.put("2", "b");
        treeMap.values().forEach(System.out::println);
    }

    private void parseToSQL(String jsonFile) {
        File file = new File(classLoader.getResource(jsonFile).getFile());
        SqlSelect select = JsonUtil.toBean(file, SqlSelect.class);
        String sql = new SqlParser(context).parseQuery(select);
        System.out.println(sql);
    }
}
