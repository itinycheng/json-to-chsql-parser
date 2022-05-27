package com.chsql.parser.model;

import com.chsql.parser.common.SqlContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.chsql.parser.common.Constant.AS;
import static com.chsql.parser.common.Constant.BRACKET_LEFT;
import static com.chsql.parser.common.Constant.COMMA;
import static com.chsql.parser.common.Constant.EMPTY;
import static com.chsql.parser.common.Constant.SPACE;
import static com.chsql.parser.snippet.SqlExpression.EQ;
import static java.util.stream.Collectors.joining;

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
        List<String> whereList = new ArrayList<>();
        if (from.size() > 1) {
            List<String> tableJoins = new ArrayList<>();
            for (SqlTable sqlTable : from) {
                tableJoins.add(sqlTable.sqlJoinKey());
            }
            String tableJoinSql =
                    IntStream.range(0, tableJoins.size() - 1)
                            .mapToObj(
                                    idx ->
                                            String.format(
                                                    EQ.expression,
                                                    tableJoins.get(idx),
                                                    tableJoins.get(idx + 1)))
                            .collect(joining(COMMA));
            whereList.add(tableJoinSql);
        }

        if (where != null) {
            String filterSql = where.toSQL(context);
            if (filterSql.startsWith(BRACKET_LEFT)) {
                filterSql =
                        whereList.isEmpty()
                                ? filterSql
                                : filterSql.substring(1, filterSql.length() - 1);
                whereList.add(filterSql);
            }
        }

        String sql = EMPTY;
        if (CollectionUtils.isNotEmpty(whereList)) {
            sql = "WHERE " + String.join(" AND ", whereList);
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
        List<String> tableList = new ArrayList<>();
        for (SqlTable sqlTable : from) {
            String sql = sqlTable.toSQL(context);
            tableList.add(String.join(SPACE, sql, AS, sqlTable.ident()));
        }
        return "FROM " + String.join(COMMA, tableList);
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
