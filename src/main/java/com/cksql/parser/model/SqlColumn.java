package com.cksql.parser.model;

import com.cksql.parser.common.ColumnExtra;
import com.cksql.parser.common.SqlContext;
import com.cksql.parser.type.DataType;
import com.cksql.parser.type.LogicalType;
import com.cksql.parser.type.MapDataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

import static com.cksql.parser.common.Constant.DOT;
import static com.cksql.parser.common.Constant.EMPTY;
import static com.cksql.parser.common.Constant.UNDERSCORE;
import static com.cksql.parser.util.SqlUtil.tableIdent;

/** Sql column. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlColumn extends SqlNode {

    private String qualifier;

    private String[] names;

    @Override
    public String ident() {
        String[] formatted = new String[names.length + 1];
        formatted[0] = tableIdent(qualifier);
        System.arraycopy(names, 0, formatted, 1, names.length);
        return String.join(UNDERSCORE, formatted);
    }

    @Override
    public boolean isValid(SqlContext context) {
        if (names == null || names.length > 2) {
            return false;
        }

        DataType dataType = getDataType(context);
        if (dataType instanceof MapDataType && names.length != 2) {
            // new RuntimeException("The map data needs to specify a key.");
            return false;
        }

        return super.isValid(context);
    }

    public String toSQL(SqlContext context) {
        DataType dataType = getDataType(context);
        String column = names[0];
        if (names.length == 2) {
            LogicalType logicalType = dataType.getLogicalType();
            String key = String.join(EMPTY, logicalType.quote, names[1], logicalType.quote);
            column = String.join(EMPTY, column, "[", key, "]");
        }

        String tableIdent = tableIdent(qualifier);
        return String.join(EMPTY, tableIdent, DOT, column);
    }

    public DataType getDataType(SqlContext context) {
        Map<String, ColumnExtra> columnExtraMap = context.getTableColumnMap().get(qualifier);
        ColumnExtra columnExtra = columnExtraMap.get(names[0]);
        DataType dataType = columnExtra.getDataType();
        if (dataType instanceof MapDataType) {
            return ((MapDataType) dataType).getKeyDataType();
        } else {
            return dataType;
        }
    }
}
