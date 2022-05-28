package com.chsql.parser.model;

import com.chsql.parser.SqlValidator;
import com.chsql.parser.common.SqlContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

import static com.chsql.parser.common.Constant.AS;
import static com.chsql.parser.common.Constant.DOT;
import static com.chsql.parser.common.Constant.SPACE;
import static com.chsql.parser.enums.SqlExpression.EQ;
import static com.chsql.parser.util.SqlUtil.tableIdent;

/** sql table. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlTable extends SqlNode {

    private String id;

    private String name;

    private String database;

    private String joinKey;

    private boolean globalJoin;

    private boolean distributed;

    @Override
    public String ident() {
        return tableIdent(id);
    }

    @Override
    public List<SqlColumn> getColumns() {
        return Collections.emptyList();
    }

    @Override
    public boolean validate(SqlValidator validator, SqlContext context) {
        return validator.validateTable(this);
    }

    @Override
    public String toSQL(SqlContext sqlContext, Object... relation) {
        SqlTable prev = (SqlTable) relation[0];
        String tableSql = String.join(SPACE, String.join(DOT, database, name), AS, ident());
        if (prev == null) {
            return tableSql;
        }

        String joinLiteral = globalJoin ? "GLOBAL JOIN" : "JOIN";
        String joinCondition = String.format(EQ.expression, prev.sqlJoinKey(), sqlJoinKey());
        return String.join(SPACE, joinLiteral, tableSql, "ON", joinCondition.trim());
    }

    public String sqlJoinKey() {
        return String.join(DOT, tableIdent(id), joinKey);
    }
}
