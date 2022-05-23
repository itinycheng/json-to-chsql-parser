package com.chsql.parser.model;

import com.chsql.parser.type.DataType;
import com.chsql.parser.type.LogicalType;
import com.chsql.parser.type.MapDataType;
import com.chsql.parser.common.ColumnExtra;
import com.chsql.parser.common.SqlContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.chsql.parser.common.Constant.DOT;
import static com.chsql.parser.common.Constant.EMPTY;
import static com.chsql.parser.common.Constant.UNDERSCORE;
import static com.chsql.parser.util.SqlUtil.tableIdent;

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
    public List<SqlColumn> getColumns() {
        return Collections.singletonList(this);
    }

    @Override
    public boolean isValid(SqlContext context) {
        if (names == null || names.length > 2) {
            return false;
        }

        DataType dataType = getDataType(context);
        if (dataType instanceof MapDataType) {
            return names.length == 2;
        }

        return true;
    }

    @Override
    public String toSQL(SqlContext context, Object... relation) {
        String column = names[0];
        if (names.length == 2) {
            DataType dataType = getMapKeyDataType(context);
            LogicalType logicalType = dataType.getLogicalType();
            String key = String.join(EMPTY, logicalType.quote, names[1], logicalType.quote);
            column = String.join(EMPTY, column, "[", key, "]");
        }

        String tableIdent = tableIdent(qualifier);
        return String.join(EMPTY, tableIdent, DOT, column);
    }

    public DataType getMapKeyDataType(SqlContext context) {
        Map<String, ColumnExtra> columnExtraMap = context.getTableColumnMap().get(qualifier);
        ColumnExtra columnExtra = columnExtraMap.get(names[0]);
        DataType dataType = columnExtra.getDataType();
        if (!(dataType instanceof MapDataType)) {
            throw new RuntimeException("DataType of SqlColumn is not Map.");
        }
        return ((MapDataType) dataType).getKeyDataType();
    }

    public DataType getDataType(SqlContext context) {
        Map<String, ColumnExtra> columnExtraMap = context.getTableColumnMap().get(qualifier);
        ColumnExtra columnExtra = columnExtraMap.get(names[0]);
        DataType dataType = columnExtra.getDataType();
        if (dataType instanceof MapDataType) {
            return ((MapDataType) dataType).getValueDataType();
        } else {
            return dataType;
        }
    }
}
