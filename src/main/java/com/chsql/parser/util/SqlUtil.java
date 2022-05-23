package com.chsql.parser.util;

import com.chsql.parser.model.SqlColumn;
import com.chsql.parser.model.SqlNode;
import com.chsql.parser.model.SqlOrderBy;
import com.chsql.parser.model.SqlSelect;
import com.chsql.parser.model.SqlWhere;

import java.util.ArrayList;
import java.util.List;

/** sql utils. */
public class SqlUtil {

    public static List<SqlColumn> extractAllColumns(SqlSelect sqlSelect) {
        List<SqlColumn> allColumns = new ArrayList<>();

        for (SqlNode sqlNode : sqlSelect.getSelect()) {
            allColumns.addAll(sqlNode.getColumns());
        }

        SqlWhere where = sqlSelect.getWhere();
        if (where != null) {
            allColumns.addAll(where.getAllColumns());
        }

        SqlOrderBy orderBy = sqlSelect.getOrderBy();
        if (orderBy != null) {
            allColumns.addAll(orderBy.getAllColumns());
        }

        return allColumns;
    }

    public static String tableIdent(String tableId) {
        return "t" + tableId;
    }
}
