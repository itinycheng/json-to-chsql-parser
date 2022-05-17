package com.cksql.parser.model;

import com.cksql.parser.common.SqlContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
        String sql = where.toSQL(context);
        return null;
    }

    private String selectSQL(SqlContext context) {
        List<String> selectList = new ArrayList<>();
        for (SqlNode sqlNode : select) {
            if (sqlNode instanceof SqlColumn) {
                sqlNode.unwrap(SqlColumn.class).getQualifier();
            }
        }
        return null;
    }

    private String fromSQL(SqlContext context) {
        return null;
    }
}
