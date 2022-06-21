package com.chsql.parser.model;

import com.chsql.parser.common.SqlContext;
import com.chsql.parser.enums.BuildInFunction;
import com.chsql.parser.type.ArrayDataType;
import com.chsql.parser.type.DataType;
import com.chsql.parser.type.MapDataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.chsql.parser.common.Constant.COMMA;
import static com.chsql.parser.common.Constant.DEFAULT_ARRAY;
import static com.chsql.parser.common.Constant.DEFAULT_MAP;
import static com.chsql.parser.common.Constant.EMPTY;
import static com.chsql.parser.common.Constant.NULL;
import static com.chsql.parser.common.Constant.ROW_NUM;
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
        originColumns.add(ROW_NUM);
        otherColumns.add(String.valueOf(rowCount));
        for (SqlNode sqlNode : sqlSelect.getSelect()) {
            // origin
            originColumns.add(sqlNode.ident());
            // other
            SqlFunction sqlFunction = sqlNode.unwrap(SqlFunction.class);
            BuildInFunction function = sqlFunction != null ? sqlFunction.getFunction() : null;
            if (function != null && function.isAggFunc()) {
                otherColumns.add(String.format(BuildInFunction.SUM.format, sqlNode.ident()));
            } else {
                otherColumns.add(defaultColumnValue(sqlNode, context));
            }
        }

        return String.format(
                TOP_N_AND_OTHER_SQL_FORMAT,
                baseSql,
                String.join(COMMA, originColumns),
                rowCount,
                String.join(COMMA, otherColumns),
                rowCount);
    }

    private String defaultColumnValue(SqlNode sqlNode, SqlContext context) {
        DataType columnType;
        if (sqlNode instanceof SqlFunction) {
            SqlFunction sqlFunction = sqlNode.unwrap(SqlFunction.class);
            BuildInFunction function = sqlFunction.getFunction();
            columnType = function.resultType;
        } else {
            columnType = sqlNode.unwrap(SqlColumn.class).getDataType(context);
        }

        if (columnType instanceof ArrayDataType) {
            return DEFAULT_ARRAY;
        } else if (columnType instanceof MapDataType) {
            return DEFAULT_MAP;
        } else {
            return NULL;
        }
    }
}
