package com.cksql.parser;

import com.cksql.parser.common.ColumnExtra;
import com.cksql.parser.common.SqlContext;
import com.cksql.parser.common.TableExtra;
import com.cksql.parser.model.SqlSelect;
import com.cksql.parser.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/** sql parser test. */
public class SqlParserTest {

    private ClassLoader classLoader;

    private SqlContext context;

    @Before
    public void before() {
        classLoader = Thread.currentThread().getContextClassLoader();
        context =
                new SqlContext.Builder()
                        .addTable(new TableExtra(1L, "t_user", "ods"))
                        .addTable(new TableExtra(2L, "t_order", "ups"))
                        .addColumn(new ColumnExtra("id", 1L, "Int64"))
                        .addColumn(new ColumnExtra("type", 1L, "String"))
                        .addColumn(new ColumnExtra("favor", 1L, "Array(String)"))
                        .addColumn(new ColumnExtra("props", 1L, "Map(String, UInt64)"))
                        .addColumn(new ColumnExtra("id", 2L, "Int64"))
                        .addColumn(new ColumnExtra("user_id", 2L, "Int64"))
                        .addColumn(new ColumnExtra("goods", 2L, "Map(String, UInt32)"))
                        .build();
    }

    @Test
    public void testWhere() {
        parseToSQL("where.json");
    }

    private void parseToSQL(String jsonFile) {
        File file = new File(classLoader.getResource(jsonFile).getFile());
        SqlSelect select = JsonUtil.toBean(file, SqlSelect.class);
        String sql = new SqlParser(context).parseQuery(select);
        System.out.println(sql);
    }
}
