package com.chsql.parser.model;

import com.chsql.parser.common.SqlContext;
import com.chsql.parser.enums.BuildInFunction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.chsql.parser.common.Constant.COMMA;
import static com.chsql.parser.common.Constant.EMPTY;
import static com.chsql.parser.common.Constant.TOP_N_AND_OTHER_SQL_FORMAT;
import static com.chsql.parser.enums.SqlExpression.LIMIT;

/** sql limit. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqlLimit {

    private boolean hasOther;

    private int offset;

    private int rowCount;

    public String toSQL(SqlContext context, String baseSql) {
        return hasOther ? topNAndOther(context, baseSql) : topN(baseSql);
    }

    private String topN(String baseSql) {
        String limit = String.format(LIMIT.expression, offset, rowCount);
        return String.join(EMPTY, baseSql, limit);
    }

    private String topNAndOther(SqlContext context, String baseSql) {
        SqlSelect sqlSelect = context.getSqlSelect();
        if (offset != 0) {
            throw new RuntimeException(" value of offset must be 0.");
        }

        List<String> originColumns = new ArrayList<>();
        List<String> otherColumns = new ArrayList<>();
        for (SqlNode sqlNode : sqlSelect.getSelect()) {
            // origin
            originColumns.add(sqlNode.ident());
            // other
            SqlFunction sqlFunction = sqlNode.unwrap(SqlFunction.class);
            BuildInFunction function =
                    sqlFunction != null ? BuildInFunction.of(sqlFunction.getName()) : null;
            if (function != null && function.isAggFunc()) {
                otherColumns.add(String.format(BuildInFunction.SUM.format, sqlNode.ident()));
            } else {
                otherColumns.add("null");
            }
        }

        String originColumnSql = String.join(COMMA, originColumns);
        String otherColumnSql = String.join(COMMA, otherColumns);
        return String.format(
                TOP_N_AND_OTHER_SQL_FORMAT,
                baseSql,
                originColumnSql,
                rowCount,
                otherColumnSql,
                rowCount);
    }
}
