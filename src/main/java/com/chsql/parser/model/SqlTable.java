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
import static com.chsql.parser.common.Constant.BRACKET_LEFT;
import static com.chsql.parser.common.Constant.BRACKET_RIGHT;
import static com.chsql.parser.common.Constant.DOT;
import static com.chsql.parser.common.Constant.EMPTY;
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
        SqlTable prevTable = (SqlTable) relation[0];
        boolean allowSubQuery = (boolean) relation[1];

        String tableSql = String.join(SPACE, String.join(DOT, database, name), AS, ident());
        if (prevTable == null) {
            return tableSql;
        }

        if (allowSubQuery) {
            tableSql = toSubQuery(sqlContext, tableSql);
        }

        String joinLiteral = globalJoin ? "GLOBAL JOIN" : "JOIN";
        String joinCondition = String.format(EQ.expression, prevTable.sqlJoinKey(), sqlJoinKey());
        return String.join(SPACE, joinLiteral, tableSql, "ON", joinCondition.trim());
    }

    private String toSubQuery(SqlContext sqlContext, String originTableSql) {
        SqlWhere where = sqlContext.getSqlSelect().getWhere();
        SqlWhere extractedWhere = where.extractWhere(this);
        if (extractedWhere == null) {
            return originTableSql;
        }

        //  (select * from db.table where col = 1) as t
        String tableSql = String.join(DOT, database, name);
        String whereSql = extractedWhere.toSQL(sqlContext);
        String subQuery = String.join(SPACE, "SELECT * FROM", tableSql, "WHERE", whereSql);
        return String.join(
                SPACE, String.join(EMPTY, BRACKET_LEFT, subQuery, BRACKET_RIGHT), AS, ident());
    }

    public String sqlJoinKey() {
        return String.join(DOT, tableIdent(id), joinKey);
    }
}
