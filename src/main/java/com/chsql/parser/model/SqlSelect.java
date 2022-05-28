package com.chsql.parser.model;

import com.chsql.parser.common.SqlContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.chsql.parser.common.Constant.AS;
import static com.chsql.parser.common.Constant.BRACKET_LEFT;
import static com.chsql.parser.common.Constant.COMMA;
import static com.chsql.parser.common.Constant.EMPTY;
import static com.chsql.parser.common.Constant.SPACE;

/** sql select. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqlSelect {

    private List<SqlNode> select;

    private List<SqlTable> from;

    private SqlWhere where;

    private SqlOrderBy orderBy;

    private List<SqlNode> groupBy;

    private SqlLimit limit;

    public String toSQL(SqlContext context) {
        String select = selectSQL(context);
        String from = fromSQL(context);
        String where = whereSQL(context);
        String groupBy = groupBySQL(context);
        String orderBy = orderBySQL(context);
        String baseSql = String.join(SPACE, select, from, where, groupBy, orderBy);
        return limit != null ? limit.toSQL(context, baseSql) : baseSql;
    }

    private String orderBySQL(SqlContext context) {
        if (orderBy == null) {
            return EMPTY;
        }

        return orderBy.toSQL(context);
    }

    private String whereSQL(SqlContext context) {
        if (where == null) {
            return EMPTY;
        }

        String sql = where.toSQL(context);
        if (sql.startsWith(BRACKET_LEFT)) {
            sql = sql.substring(1, sql.length() - 1);
        }
        if (StringUtils.isNotEmpty(sql)) {
            sql = "WHERE " + sql;
        }
        return sql;
    }

    private String selectSQL(SqlContext context) {
        List<String> selectList = new ArrayList<>();
        for (SqlNode sqlNode : select) {
            String sql;
            if (sqlNode instanceof SqlColumn || sqlNode instanceof SqlFunction) {
                sql = sqlNode.toSQL(context);
            } else {
                throw new RuntimeException("Select statement only support column and function.");
            }
            selectList.add(String.join(SPACE, sql, AS, sqlNode.ident()));
        }
        return "SELECT " + String.join(COMMA, selectList);
    }

    private String fromSQL(SqlContext context) {
        List<SqlTable> distributedTables = new ArrayList<>(from.size());
        List<SqlTable> localTables = new ArrayList<>(from.size());
        for (SqlTable sqlTable : from) {
            if (sqlTable.isDistributed()) {
                distributedTables.add(sqlTable);
            } else {
                localTables.add(sqlTable);
            }
        }

        List<SqlTable> fromTables = new ArrayList<>(from.size());
        fromTables.addAll(distributedTables);
        fromTables.addAll(localTables);

        List<String> tableList = new ArrayList<>();
        SqlTable prevTable = null;
        for (SqlTable sqlTable : fromTables) {
            String sql = sqlTable.toSQL(context, prevTable);
            tableList.add(sql);
            prevTable = sqlTable;
        }
        return "FROM " + String.join(SPACE, tableList);
    }

    private String groupBySQL(SqlContext context) {
        List<String> groupList = new ArrayList<>();
        for (SqlNode sqlNode : groupBy) {
            if (select.contains(sqlNode)) {
                groupList.add(sqlNode.ident());
                continue;
            }
            if (sqlNode instanceof SqlLiteral) {
                continue;
            }
            groupList.add(sqlNode.toSQL(context));
        }

        String sql = String.join(COMMA, groupList);
        if (StringUtils.isNotEmpty(sql)) {
            sql = "GROUP BY " + sql;
        }
        return sql;
    }
}
