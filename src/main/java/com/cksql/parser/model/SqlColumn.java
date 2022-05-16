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
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

import static com.cksql.parser.common.Constant.DOT;
import static com.cksql.parser.common.Constant.EMPTY;

/** Sql identifier. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlColumn extends SqlNode {

    private String qualifier;

    private String[] names;

    public String toSQL(SqlContext context) {
        DataType dataType = getDataType(context);
        String column;
        if (names.length == 1) {
            column = names[0];
        } else if (names.length == 2) {
            LogicalType logicalType = dataType.getLogicalType();
            String key = String.join(EMPTY, logicalType.quote, names[1], logicalType.quote);
            column = String.join(EMPTY, names[0], "[", key, "]");
        } else {
            throw new RuntimeException("Length of names is not more than 2.");
        }

        return String.join(EMPTY, qualifier, DOT, column);
    }

    public DataType getDataType(SqlContext context) {
        long tableId = Long.parseLong(qualifier);
        Map<String, ColumnExtra> columnExtraMap = context.getTableColumnMap().get(tableId);
        ColumnExtra columnExtra = columnExtraMap.get(names[0]);
        DataType dataType = columnExtra.getDataType();
        if (dataType instanceof MapDataType) {
            return ((MapDataType) dataType).getKeyDataType();
        } else {
            return dataType;
        }
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

        return NumberUtils.isDigits(qualifier) && super.isValid(context);
    }
}
